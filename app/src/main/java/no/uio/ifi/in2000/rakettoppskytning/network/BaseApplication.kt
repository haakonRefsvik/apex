package no.uio.ifi.in2000.rakettoppskytning.network


import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/*

 */

/**
  *Adding the needed setup for the Hilt dependency injection library to this class.
 * */
@HiltAndroidApp
class BaseApplication : Application()