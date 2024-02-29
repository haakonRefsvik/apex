package no.uio.ifi.in2000.rakettoppskytning

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeScreenViewModel : ViewModel(){
    init {
        viewModelScope.launch { Log.d("JANNE",hentFly().time.toString()) }
    }

}