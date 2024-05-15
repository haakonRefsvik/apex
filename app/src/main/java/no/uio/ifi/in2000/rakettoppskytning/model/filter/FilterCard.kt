package no.uio.ifi.in2000.rakettoppskytning.model.filter

import no.uio.ifi.in2000.rakettoppskytning.ui.home.filter.FilterCategory

/** data class to hold values for the filter cards*/
data class FilterCard(val icon: Int, val text: String, val type: FilterCategory)
