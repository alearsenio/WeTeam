package com.example.weteam

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.example.utils.OptionMenuActivity
import com.example.weteam.home.HomeActivity
import java.util.concurrent.Executors



class AuthentificationActivity : OptionMenuActivity() {

    lateinit var biometricPrompt: BiometricPrompt
    private val INTENT_AUTHENTICATE = 1
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Unlock your App")
        .setSubtitle("Use your finger or insert your pin")
        .setDeviceCredentialAllowed(true)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(getSavedTheme())
        setContentView(R.layout.activity_authetification)

    authenticate()
    }

    fun authenticate() {
        super.onStart()
        var authenticated = true

        if (BiometricManager.from(this).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {

            val executor = Executors.newSingleThreadExecutor()
            val activity: FragmentActivity = this // reference to activity
            biometricPrompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                            // user clicked negative button
                        } else if (errorCode == BiometricPrompt.ERROR_USER_CANCELED){

                        }
                    }
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        launchHomeActivity()
                    }
                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        println("not recognized")
                    }
                })

            biometricPrompt.authenticate(promptInfo)

        } else {
            val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

            if (km!!.isKeyguardSecure()) {
                val authIntent = km!!.createConfirmDeviceCredentialIntent(
                    "authetification",
                    "insert pin"
                )
                startActivityForResult(authIntent, INTENT_AUTHENTICATE)
            } else { // if there aren't any security locks added on phone, show a dialog
                val fragmentManager = supportFragmentManager
                val fragment = FireMissilesDialogFragment()
                fragment.show(fragmentManager, "fragment Alert")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //if some biometric lock are used, ask for them
    }

    fun launchHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    //fragment for dialog when there are no security lock available
    class FireMissilesDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return this?.let {
                // Where we track the selected items
                val builder =
                    AlertDialog.Builder(it.context, R.style.Theme_MaterialComponents_Light_Dialog_Alert)
                // Set the dialog title
                builder.setTitle("no lock set")
                    // Set the action buttons
                    .setPositiveButton("go to setting",
                        DialogInterface.OnClickListener { dialog, id ->
                            val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                            startActivity(intent)
                        })
                    .setNegativeButton("close app",
                        DialogInterface.OnClickListener { dialog, id ->
                            val d = context as Activity
                            d.finishAffinity()
                        })

                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == INTENT_AUTHENTICATE) {
            if (resultCode == RESULT_OK) {
                Log.i("aut", "ok")
                launchHomeActivity()
            }
            else{
                Log.i("aut", "error")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
