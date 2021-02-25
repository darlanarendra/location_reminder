package com.udacity.project4.util

import androidx.test.espresso.idling.CountingIdlingResource

/**
 * Inorder to Junit to handle theasynchronous tasks
 */
object EspressoIdlingResource {
    private const val RESOURCE = "GLOBAL"
    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)
    fun increment(){
        countingIdlingResource.increment()
    }

    fun decrement(){
        if(countingIdlingResource.isIdleNow){
            countingIdlingResource.decrement()
        }
    }
}