package no.uio.ifi.in2000.rakettoppskytning.network

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<Status>

    enum class Status{
        Available, Unavailable, Lost, Losing
    }
}