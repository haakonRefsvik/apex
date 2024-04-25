package no.uio.ifi.in2000.rakettoppskytning.scrollbar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private const val oneHundredPercentDecimal = 1f

private const val isEmpty = 0

/*
Kerry Bisset

Jetpack Compose Multiplatform Scrollbar/Scrolling
 */
@Composable
fun LazyColumnScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    rightSide: Boolean = true,
    alwaysShowScrollBar: Boolean = false,
    thickness: Dp = 1.dp,
    padding: Dp = 8.dp,
    thumbMinHeight: Float = 0.1f,
    thumbColor: Color = MaterialTheme.colorScheme.primary,
    thumbSelectedColor: Color = MaterialTheme.colorScheme.tertiary,
    thumbShape: Shape = CircleShape,
    selectionMode: ScrollbarSelectionMode = ScrollbarSelectionMode.Thumb,
    selectionActionable: ScrollbarSelectionActionable = ScrollbarSelectionActionable.Always,
    hideDelay: Duration = 400.toDuration(DurationUnit.MILLISECONDS),
    showItemIndicator: ListIndicatorSettings = ListIndicatorSettings.EnabledMirrored(
        100.dp,
        MaterialTheme.colorScheme.surface
    ),
    enabled: Boolean = true,
    indicatorContent: (@Composable (index: Int, isThumbSelected: Boolean) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if ((!enabled)) {
        content()
    } else {
        Box(modifier = modifier) {
            val visibilityState = remember {
                derivedStateOf {
                    calculateVisibilityStates(listState, showItemIndicator)
                }
            }

            // Use animateFloatAsState to smoothly transition the alpha value
            val alphaAbove: Float by animateFloatAsState(
                if (visibilityState.value.first != VisibilityState.CompletelyVisible) 1f else 0f,
                animationSpec = tween(250)
            )
            val alphaBelow: Float by animateFloatAsState(
                if (visibilityState.value.second != VisibilityState.CompletelyVisible) 1f else 0f,
                animationSpec = tween(250)
            )

            val heightAbove: Float by animateFloatAsState(
                if (visibilityState.value.first == VisibilityState.NotVisible) .8f else .25f,
                animationSpec = tween(1000)
            )

            val heightBelow: Float by animateFloatAsState(
                if (visibilityState.value.second == VisibilityState.NotVisible) 1f else .25f,
                animationSpec = tween(1000)
            )

            content()
            when(showItemIndicator){
                ListIndicatorSettings.Disabled -> {
                    // Do nothing
                }
                is ListIndicatorSettings.EnabledIndividualControl -> {
                    DisplayIndicator(
                        upIndication = true,
                        indicatorHeight = showItemIndicator.upperIndicatorHeight * heightAbove,
                        indicatorColor = showItemIndicator.upperIndicatorColor,
                        alpha = alphaAbove,
                        graphicIndicator = showItemIndicator.upperGraphicIndicator,
                        modifier = Modifier.align(Alignment.TopCenter).focusable(false)
                    )

                    DisplayIndicator(
                        upIndication = false,
                        indicatorHeight = showItemIndicator.lowerIndicatorHeight * heightBelow,
                        indicatorColor = showItemIndicator.lowerIndicatorColor.copy(alpha = alphaBelow),
                        alpha = alphaBelow,
                        graphicIndicator = showItemIndicator.lowerGraphicIndicator,
                        modifier = Modifier.align(Alignment.BottomCenter).focusable(false)
                    )
                }
                is ListIndicatorSettings.EnabledMirrored -> {
                    DisplayIndicator(
                        upIndication = true,
                        indicatorHeight = showItemIndicator.indicatorHeight * heightAbove,
                        indicatorColor = showItemIndicator.indicatorColor,
                        alpha = alphaAbove,
                        graphicIndicator = showItemIndicator.graphicIndicator,
                        modifier = Modifier.align(Alignment.TopCenter).focusable(false)
                    )

                    DisplayIndicator(
                        upIndication = false,
                        indicatorHeight = showItemIndicator.indicatorHeight * heightBelow,
                        indicatorColor = showItemIndicator.indicatorColor.copy(alpha = alphaBelow),
                        alpha = alphaBelow,
                        graphicIndicator = showItemIndicator.graphicIndicator,
                        modifier = Modifier.align(Alignment.BottomCenter).focusable(false),
                        graphicModifier = Modifier.rotate(180f)
                    )
                }
            }

            InternalLazyColumnScrollbar(
                listState = listState,
                modifier = Modifier,
                rightSide = rightSide,
                alwaysShowScrollBar = alwaysShowScrollBar,
                thickness = thickness,
                padding = padding,
                thumbMinHeight = thumbMinHeight,
                thumbColor = thumbColor,
                thumbSelectedColor = thumbSelectedColor,
                selectionActionable = selectionActionable,
                hideDelay = hideDelay,
                thumbShape = thumbShape,
                selectionMode = selectionMode,
                indicatorContent = indicatorContent,
            )
        }
    }
}

@Composable
private fun InternalLazyColumnScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    rightSide: Boolean = true,
    alwaysShowScrollBar: Boolean = false,
    thickness: Dp = 1.dp,
    padding: Dp = 10.dp,
    thumbMinHeight: Float = 0.1f,
    thumbColor: Color,
    thumbSelectedColor: Color,
    thumbShape: Shape = CircleShape,
    selectionMode: ScrollbarSelectionMode,
    selectionActionable: ScrollbarSelectionActionable,
    hideDelay: Duration,
    indicatorContent: (@Composable (index: Int, isThumbSelected: Boolean) -> Unit)? = null,
) {
    val firstVisibleItemIndex = remember { derivedStateOf { listState.firstVisibleItemIndex } }

     val coroutineScope = rememberCoroutineScope()

    var isSelected by remember { mutableStateOf(false) }

    var dragOffset by remember { mutableStateOf(0f) }

    val reverseLayout by remember { derivedStateOf { listState.layoutInfo.reverseLayout } }

    val realFirstVisibleItem by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.firstOrNull {
                it.index == listState.firstVisibleItemIndex
            }
        }
    }

    val isStickyHeaderInAction by remember {
        derivedStateOf {
            val realIndex = realFirstVisibleItem?.index ?: return@derivedStateOf false
            val firstVisibleIndex = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index
                ?: return@derivedStateOf false
            realIndex != firstVisibleIndex
        }
    }

    fun LazyListItemInfo.fractionHiddenTop(firstItemOffset: Int) =
        if (size == isEmpty) 0f else firstItemOffset / size.toFloat()

    fun LazyListItemInfo.fractionVisibleBottom(viewportEndOffset: Int) =
        if (size == isEmpty) 0f else (viewportEndOffset - offset).toFloat() / size.toFloat()

    val normalizedThumbSizeReal by remember {
        derivedStateOf {
            listState.layoutInfo.let {
                // If there are no items, return 0
                if (it.totalItemsCount == isEmpty) {
                    return@let 0f
                }

                val firstItem = realFirstVisibleItem ?: return@let 0f
                val firstPartial =
                    firstItem.fractionHiddenTop(listState.firstVisibleItemScrollOffset)
                val lastPartial = oneHundredPercentDecimal - it.visibleItemsInfo.last().fractionVisibleBottom(
                    it.viewportEndOffset - it.afterContentPadding
                )

                val realSize = it.visibleItemsInfo.size - if (isStickyHeaderInAction) 1 else isEmpty
                val realVisibleSize = realSize.toFloat() - firstPartial - lastPartial
                realVisibleSize / it.totalItemsCount.toFloat()
            }
        }
    }

    val normalizedThumbSize by remember {
        derivedStateOf {
            normalizedThumbSizeReal.coerceAtLeast(thumbMinHeight)
        }
    }

    fun offsetCorrection(top: Float): Float {
        val topRealMax = (oneHundredPercentDecimal - normalizedThumbSizeReal).coerceIn(0f, oneHundredPercentDecimal)
        if (normalizedThumbSizeReal >= thumbMinHeight) {
            return when {
                reverseLayout -> topRealMax - top
                else -> top
            }
        }

        val topMax = oneHundredPercentDecimal - thumbMinHeight
        return when {
            reverseLayout -> (topRealMax - top) * topMax / topRealMax
            else -> top * topMax / topRealMax
        }
    }

    fun offsetCorrectionInverse(top: Float): Float {
        if (normalizedThumbSizeReal >= thumbMinHeight)
            return top
        val topRealMax = oneHundredPercentDecimal - normalizedThumbSizeReal
        val topMax = oneHundredPercentDecimal - thumbMinHeight
        return top * topRealMax / topMax
    }

    val normalizedOffsetPosition by remember {
        derivedStateOf {
            listState.layoutInfo.let {
                if (it.totalItemsCount == isEmpty || it.visibleItemsInfo.isEmpty())
                    return@let 0f

                val firstItem = realFirstVisibleItem ?: return@let 0f
                val top = firstItem
                    .run { index.toFloat() + fractionHiddenTop(listState.firstVisibleItemScrollOffset) } / it.totalItemsCount.toFloat()
                offsetCorrection(top)
            }
        }
    }

    fun setDragOffset(value: Float) {
        val maxValue = (oneHundredPercentDecimal - normalizedThumbSize).coerceAtLeast(0f)
        dragOffset = value.coerceIn(0f, maxValue)
    }

     fun setScrollOffset(newOffset: Float) {
        setDragOffset(newOffset)
        val totalItemsCount = listState.layoutInfo.totalItemsCount.toFloat()
        val exactIndex = offsetCorrectionInverse(totalItemsCount * dragOffset)
        val index: Int = floor(exactIndex).toInt()
        val remainder: Float = exactIndex - floor(exactIndex)

        coroutineScope.launch {
            listState.scrollToItem(index = index, scrollOffset = isEmpty)
            val offset = realFirstVisibleItem
                ?.size
                ?.let { it.toFloat() * remainder }
                ?: 0f
            listState.scrollBy(offset)
        }
    }

    val isInAction = listState.isScrollInProgress || isSelected || alwaysShowScrollBar

    val isInActionSelectable = remember { mutableStateOf(isInAction) }
    val durationAnimationMillis = 500
    LaunchedEffect(isInAction) {
        if (isInAction) {
            isInActionSelectable.value = true
        } else {
            delay(timeMillis = durationAnimationMillis.toLong() + hideDelay.toLong(DurationUnit.MILLISECONDS))
            isInActionSelectable.value = false
        }
    }

    val alpha by animateFloatAsState(
        targetValue = if (isInAction) oneHundredPercentDecimal else 0f,
        animationSpec = tween(
            durationMillis = if (isInAction) 75 else durationAnimationMillis,
            delayMillis = if (isInAction) isEmpty else hideDelay.toInt(DurationUnit.MILLISECONDS)
        ),
        label = "scrollbar alpha value"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val maxHeightFloat = constraints.maxHeight.toFloat()

        // Aligning items to the top and towards the start/end based on `rightSide`
        Box(
            modifier = Modifier
                .align(if (rightSide) Alignment.TopEnd else Alignment.TopStart)
                .graphicsLayer(
                    translationY = maxHeightFloat * normalizedOffsetPosition
                )
        ) {
            // Using Column for vertical arrangement or Row for horizontal,
            // depending on your layout needs.
            Column { // or Row, if you need horizontal arrangement
                // Thumb Box
                Box(
                    modifier = Modifier
                        .padding(
                            start = if (rightSide) isEmpty.dp else padding,
                            end = if (!rightSide) isEmpty.dp else padding,
                        )
                        .clip(thumbShape)
                        .width(thickness)
                        .fillMaxHeight(normalizedThumbSize)
                        .alpha(alpha)
                        .background(if (isSelected) thumbSelectedColor else thumbColor)
                )

                // Optional indicator content
                if (indicatorContent != null) {
                    Box(
                        modifier = Modifier
                            .alpha(alpha)
                        // Additional modifiers to position this Box relative to the thumb Box
                        // might be needed depending on your exact requirements.
                    ) {
                        indicatorContent(firstVisibleItemIndex.value, isSelected)
                    }
                }
            }
        }

        @Composable
        fun DraggableBar() = Box(
            modifier = Modifier
                .align(if (rightSide) Alignment.TopEnd else Alignment.TopStart)
                .width(padding * 2 + thickness)
                .fillMaxHeight()
                .draggable(
                    state = rememberDraggableState { delta ->
                        val displace = if (reverseLayout) -delta else delta // side effect ?
                        if (isSelected) {
                            setScrollOffset(dragOffset + displace / maxHeightFloat)
                        }
                    },
                    orientation = Orientation.Vertical,
                    enabled = selectionMode != ScrollbarSelectionMode.Disabled,
                    startDragImmediately = true,
                    onDragStarted = onDragStarted@{ offset ->
                        if (maxHeightFloat <= 0f) return@onDragStarted
                        val newOffset = when {
                            reverseLayout -> (maxHeightFloat - offset.y) / maxHeightFloat
                            else -> offset.y / maxHeightFloat
                        }
                        val currentOffset = when {
                            reverseLayout -> oneHundredPercentDecimal - normalizedOffsetPosition - normalizedThumbSize
                            else -> normalizedOffsetPosition
                        }

                        when (selectionMode) {
                            ScrollbarSelectionMode.Full -> {
                                if (newOffset in currentOffset..(currentOffset + normalizedThumbSize))
                                    setDragOffset(currentOffset)
                                else
                                    setScrollOffset(newOffset)
                                isSelected = true
                            }

                            ScrollbarSelectionMode.Thumb -> {
                                if (newOffset in currentOffset..(currentOffset + normalizedThumbSize)) {
                                    setDragOffset(currentOffset)
                                    isSelected = true
                                }
                            }

                            ScrollbarSelectionMode.Disabled -> Unit
                        }
                    },
                    onDragStopped = {
                        isSelected = false
                    }
                )
        )

        val show = when (selectionActionable) {
            ScrollbarSelectionActionable.Always -> true
            ScrollbarSelectionActionable.WhenVisible -> isInActionSelectable.value
        }
        if (show) {
            DraggableBar()
        }
    }
}


internal fun calculateVisibilityStates(
    listState: LazyListState,
    showItemIndicator: ListIndicatorSettings
): Pair<VisibilityState, VisibilityState> {
    val layoutInfo = listState.layoutInfo
    val totalItemCount = layoutInfo.totalItemsCount
    val visibleItems = layoutInfo.visibleItemsInfo
    val firstVisibleItemIndex = listState.firstVisibleItemIndex
    val firstItemVisibleOffset = listState.firstVisibleItemScrollOffset
    val viewportSize = layoutInfo.viewportSize.height

    if (layoutInfo.totalItemsCount == 0) {
        return Pair(VisibilityState.NotVisible, VisibilityState.NotVisible)
    }

    if (showItemIndicator is ListIndicatorSettings.Disabled) {
        return Pair(VisibilityState.CompletelyVisible, VisibilityState.CompletelyVisible)
    }

    // Calculate visibility for content above
    val contentAboveState = when {
        !layoutInfo.reverseLayout -> {
            if (firstVisibleItemIndex == 0 && firstItemVisibleOffset == 0) VisibilityState.CompletelyVisible
            else if (visibleItems.none { it.index == 0 }) VisibilityState.NotVisible
            else VisibilityState.PartiallyVisible
        }
        else -> {
            determineVisibilityState(visibleItems, totalItemCount, viewportSize)
        }
    }

    // Calculate visibility for content below
    val contentBelowState = when {
        !layoutInfo.reverseLayout -> {
            determineVisibilityState(visibleItems, totalItemCount, viewportSize)
        }

        else -> {
            if (firstVisibleItemIndex == 0 && firstItemVisibleOffset == 0) VisibilityState.CompletelyVisible
            else if (visibleItems.none { it.index == 0 }) VisibilityState.NotVisible
            else VisibilityState.PartiallyVisible
        }
    }

    return Pair(contentAboveState, contentBelowState)
}

private fun determineVisibilityState(
    visibleItems: List<LazyListItemInfo>,
    totalItemCount: Int,
    viewportSize: Int
): VisibilityState {
    val lastItem = visibleItems.lastOrNull()
    return if (lastItem != null && lastItem.index == totalItemCount - 1 && (lastItem.size + lastItem.offset) <= viewportSize) VisibilityState.CompletelyVisible
    else if (visibleItems.none { it.index == totalItemCount - 1 }) VisibilityState.NotVisible
    else VisibilityState.PartiallyVisible
}


@Composable
internal fun DisplayIndicator(
    upIndication: Boolean,
    indicatorHeight: Dp,
    indicatorColor: Color,
    alpha: Float,
    graphicIndicator: @Composable (modifier: Modifier, alpha: Float) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
    graphicModifier: Modifier = Modifier
) {
    val ratio = .5f
    Box(
        modifier = modifier
            .fillMaxWidth(1f)
            .height(IntrinsicSize.Max),
    ) {

    }
}

