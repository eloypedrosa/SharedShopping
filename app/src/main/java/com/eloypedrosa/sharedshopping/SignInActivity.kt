package com.eloypedrosa.sharedshopping

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import android.util.Log
import android.widget.Button
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SignInActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 9001
    private lateinit var auth: FirebaseAuth
    private val repo = FirestoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth

        val btn = findViewById<Button>(R.id.btnSignIn)
        btn.setOnClickListener { startGoogleSignIn() }
    }

    override fun onStart() {
        super.onStart()
        val current = auth.currentUser
        if (current != null) {
            repo.ensureUserDocument { /* ignore */ }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun startGoogleSignIn() {
        val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
            com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(this, gso)
        startActivityForResult(client.signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { t ->
                    if (t.isSuccessful) {
                        repo.ensureUserDocument { }
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Log.w("SignIn", "signInWithCredential:failure", t.exception)
                    }
                }
            } catch (e: ApiException) {
                Log.w("SignIn", "Google sign in failed", e)
            }
        }
    }
}
