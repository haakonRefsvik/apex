package no.uio.ifi.in2000.rakettoppskytning.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InternetConnectionViewModel : ViewModel() {
    val isInternetConnected = MutableLiveData<Boolean>()
}