package com.precensia.app.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.precensia.app.adapters.AttendanceAdapter
import com.precensia.app.databinding.ActivityDelegateBinding
import com.precensia.app.models.AttendanceRecord
import com.precensia.app.models.AttendanceSheet
import com.precensia.app.models.User
import java.text.SimpleDateFormat
import java.util.*

class DelegateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDelegateBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: AttendanceAdapter
    private val studentList = mutableListOf<AttendanceRecord>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDelegateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        setupRecyclerView()
        loadStudents()

        binding.btnSubmit.setOnClickListener {
            submitAttendance()
        }
    }

    private fun setupRecyclerView() {
        adapter = AttendanceAdapter(studentList, true)
        binding.rvStudents.layoutManager = LinearLayoutManager(this)
        binding.rvStudents.adapter = adapter
    }

    private fun loadStudents() {
        db.collection("users").whereEqualTo("role", "STUDENT").get()
            .addOnSuccessListener { documents ->
                studentList.clear()
                for (doc in documents) {
                    val user = doc.toObject(User::class.java)
                    studentList.add(AttendanceRecord(user.uid, user.name, "PRESENT"))
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun submitAttendance() {
        val timestamp = System.currentTimeMillis()
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestamp))
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        
        // Amélioration : L'ID est maintenant unique par session (date + timestamp)
        // Cela permet plusieurs cours le même jour.
        val sheetId = "session_${timestamp}"
        
        val sheet = AttendanceSheet(
            id = sheetId,
            date = "$date ($time)",
            records = adapter.getRecords(), // Récupérer les données mises à jour de l'adapter
            isValidated = false,
            submittedBy = uid
        )

        db.collection("attendance").document(sheetId).set(sheet)
            .addOnSuccessListener {
                Toast.makeText(this, "Fiche de présence soumise avec succès", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Échec de l'envoi : ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
