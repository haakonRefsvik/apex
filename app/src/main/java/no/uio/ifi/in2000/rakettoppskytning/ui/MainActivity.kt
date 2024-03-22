package no.uio.ifi.in2000.rakettoppskytning.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import no.uio.ifi.in2000.rakettoppskytning.R.string
import no.uio.ifi.in2000.rakettoppskytning.data.ApiKeyHolder
import no.uio.ifi.in2000.rakettoppskytning.data.database.FavoriteDatabase
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Data
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Details
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.favorite.FavoriteViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.RakettoppskytningTheme

class MainActivity : ComponentActivity() {
    //@RequiresApi(Build.VERSION_CODES.O)

    private val db by lazy {
        FavoriteDatabase.getInstance(this)
    }

    private val viewModel by viewModels<FavoriteViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FavoriteViewModel(db.dao) as T
                }
            }
        }
    )
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApiKeyHolder.in2000ProxyKey = resources.getString(string.in2000ProxyKey)
        setContent {
            RakettoppskytningTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state by viewModel.state.collectAsState()
                    Navigation(state = state, onEvent = viewModel::onEvent)
                }
            }
        }
    }
}
