package no.uio.ifi.in2000.rakettoppskytning.ui.home.filter

enum class FilterCategory(val toString: String)
{
    WIND_STRENGTH("wind strength"),
    WIND_DIR("wind direction"),
    RAIN("rain"),
    VIEW_DIST("viewing distance"),
    AIR_HUMID("air humidity"),
    DEW_POINT("dew point"),
    UNFILTERED("unfiltered")
}