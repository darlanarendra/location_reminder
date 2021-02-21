package com.udacity.project4.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.annotation.MainThread
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import org.koin.core.context.GlobalContext
import java.util.*

/**
 *
 */
open class PermissionManager: Fragment() {

    companion object{
        private val REQUEST_CODE = 420
        private val requests:Queue<PermissionRequest> = LinkedList()
        private val newRequest = MutableLiveData<Unit>()
        fun arePermissionsGranted(vararg permissions:String):Boolean = permissions.all{
            val context = GlobalContext.getOrNull()?.koin?.get<Application>()?:return false
            ActivityCompat.checkSelfPermission(context,it) == PackageManager.PERMISSION_GRANTED
        }
        @MainThread
        fun requestPermissions(vararg permissions:String, handler:(PermissionsResultEvent)->Unit){
            requests.offer(PermissionRequest(permissions,handler))
            newRequest.postValue({}())
        }
    }
    private var currentRequest:PermissionRequest?= null

    @SuppressLint("FragmentLiveDataObserve")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        askNextRequest()

        newRequest.observe(this){
            askNextRequest()
        }
    }


    private fun askNextRequest(){
        currentRequest = requests.poll()?:return
        requestPermissions(currentRequest!!.permissions, REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val failedPermissions = grantResults.filter{ it == PackageManager.PERMISSION_DENIED}.mapIndexed{
            index,_->permissions[index]
        }

        val shouldShowRequestRational = failedPermissions.any{
            shouldShowRequestPermissionRationale((it))
        }

        currentRequest?.handler?.invoke(PermissionsResultEvent(permissions,shouldShowRequestRational,grantResults))
        askNextRequest()
    }
}


data class PermissionsResultEvent(val permissions:Array<out String>,
    val shouldShowRequestRational: Boolean,
    val grantResults:IntArray){
    val areAllGranted = grantResults.all {
        it == PackageManager.PERMISSION_GRANTED
    }

    override fun equals(other: Any?): Boolean {
        if(this == other ) return true
        if(javaClass != other?.javaClass) return false
        other as PermissionsResultEvent
        if(!permissions.contentEquals(other.permissions)) return false
        if(shouldShowRequestRational != other.shouldShowRequestRational) return false
        if(!grantResults.contentEquals(other.grantResults)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = permissions.contentHashCode()
        result = 31 * result + shouldShowRequestRational.hashCode()
        result = 31* result +grantResults.contentHashCode()
        return result
    }
}



private data class PermissionRequest(val permissions:Array<out String>,
                                      val handler:(PermissionsResultEvent)->Unit){
    override fun equals(other: Any?): Boolean {
        if(this == other) return true
        if(javaClass != other?.javaClass) return false
        other as PermissionRequest
        if(!permissions.contentEquals(other.permissions)) return false
        if(handler!= other.handler) return false
        return true
    }

    override fun hashCode(): Int {
        var result = permissions.contentHashCode()
        result = 31*result +handler.hashCode()
        return result
    }

}