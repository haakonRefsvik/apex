package no.uio.ifi.in2000.rakettoppskytning.search

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import no.uio.ifi.in2000.rakettoppskytning.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.R


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun searchbarM3(homeScreenViewModel :  HomeScreenViewModel, onSearch: (lat: Double, lon:Double) -> Unit){
    var query by remember{mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    var searchHistory = homeScreenViewModel.getSearchHistory().value.takeLast(3)

    SearchBar(
        query = query ,
        onQueryChange = {query = it},
        onSearch ={
            println("perdorming search on query: $query")

            val (lat, lon) = query.split(",").map { it.trim().toDoubleOrNull() }

            if (lat != null && lon != null) {
                onSearch(lat, lon)
                homeScreenViewModel.addToSearchHistory(query)
            } else {
                print("error")
                // Handle invalid latitude and longitude
                // e.g., display an error message, show a toast, etc.
            }
        },

        active = active,
        onActiveChange = {active = it },
        placeholder = {
            Text(text="Search")
        },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
        },
        trailingIcon = if(active){
            {
                IconButton(onClick = { if (query.isNotEmpty()) query = "" else active = false})
                {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                }
            }
        }else{
            null
        }
    )

    {
        searchHistory.forEach{  item ->
            ListItem(
                modifier = Modifier.clickable {query = item},
                headlineContent = {Text(text = item)},

                leadingContent = {
                    Icon(painter = painterResource(R.drawable.ic_history ),
                        contentDescription = "Search History")
                }
            )
        }




    }
}