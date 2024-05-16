package no.uio.ifi.in2000.rakettoppskytning.ui.home.filter

/**
 * FilterCategory categorizes filters: wind strength, wind direction, rain, viewing distance, air humidity, dew point, and unfiltered.
 * */
enum class FilterCategory(val string: String)
{
    WIND_STRENGTH("wind strength"),
    WIND_DIR("wind direction"),
    RAIN("rain"),
    VIEW_DIST("viewing distance"),
    AIR_HUMID("air humidity"),
    DEW_POINT("dew point"),
    UNFILTERED("unfiltered")
}