package com.precensia.app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.precensia.app.databinding.ActivityLoginBinding
import com.precensia.app.models.User

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        checkUserRole(it.user?.uid)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erreur de connexion: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun checkUserRole(uid: String?) {
        if (uid == null) return
        
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    val intent = when (user.role) {
                        "PROFESSOR" -> Intent(this, ProfessorActivity::class.java)
                        "DELEGATE" -> Intent(this, DelegateActivity::class.java)
                        "STUDENT" -> Intent(this, StudentActivity::class.java)
                        else -> null
                    }
                    intent?.let {
                        startActivity(it)
                        finish()
                    }
                }
            }
    }
}
