package com.example.bookie

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth



class ConnexionActivity : Activity() {

    private lateinit var auth: FirebaseAuth

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.connexion_activity)

        auth = Firebase.auth
    }


    private fun reload(){
        val intent = Intent(this, BookListActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun signOnClick(view:View){
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)

        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Le champ 'email' et le champ 'mot de passe' est vide !", Toast.LENGTH_SHORT).show()
        }
        else if (email.isEmpty()) {
            Toast.makeText(this, "Le champ 'email' est vide !", Toast.LENGTH_SHORT).show()
        }
        else if (password.isEmpty()) {
            Toast.makeText(this, "Le champ 'mot de passe' est vide !", Toast.LENGTH_SHORT).show()
        }
        else{
            signIn(email,password)
        }

    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    reload()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }



    companion object {
        private const val TAG = "EmailPassword"
    }


}

