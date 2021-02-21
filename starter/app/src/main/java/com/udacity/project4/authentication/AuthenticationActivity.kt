package com.udacity.project4.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.viewmodel.RequestCodes
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig.Prompt.SIGN_IN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import kotlinx.android.synthetic.main.activity_authentication.*


/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {


    val RC_SIGN_IN = 9001
    val TAG = AuthenticationActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebaseAuth = FirebaseAuth.getInstance()
        if(firebaseAuth.currentUser != null){
            navigateToRemindersActivity()
            return
        }
        setContentView(R.layout.activity_authentication)
    }



    fun singIn(view: View) {
        onLoginButtonClicked()
    }

    private fun onLoginButtonClicked() {
        startActivityForResult(AuthUI.getInstance()
            .createSignInIntentBuilder().setAvailableProviders(
                listOf(
                    AuthUI.IdpConfig.GoogleBuilder().build(),
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.AppleBuilder().build()
                )).setAuthMethodPickerLayout(
                AuthMethodPickerLayout.Builder(R.layout.layout_auth_picker)
                    .setGoogleButtonId(R.id.gmail)
                    .setEmailButtonId(R.id.email).
                    setAppleButtonId(R.id.apple).build()
                ).build(),RC_SIGN_IN

            )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode != RC_SIGN_IN){
           return
        }
        if(resultCode == RESULT_OK){
            navigateToRemindersActivity()
            return
        }
    }

    private fun navigateToRemindersActivity() {
        startActivity(Intent(this,RemindersActivity::class.java))
        finish()
    }
//
//    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
//        updateUI(task?.getResult(ApiException::class.java))
//    }
}
