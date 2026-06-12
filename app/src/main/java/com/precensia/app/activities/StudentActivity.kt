package com.precensia.app.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.precensia.app.adapters.AttendanceAdapter
import com.precensia.app.databinding.ActivityStudentBinding
import com.precensia.app.models.AttendanceSheet
import java.text.SimpleDateFormat
import java.util.*

class StudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        loadTodayAttendance()
    }

    private fun loadTodayAttendance() {
        val currentUserUid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        
        // Amélioration : Charger l'historique complet des fiches validées pour cet étudiant
        db.collection("attendance")
            .whereEqualTo("isValidated", true)
            .orderBy("id", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val myAttendanceList = mutableListOf<com.precensia.app.models.AttendanceRecord>()
                var presentCount = 0
                var totalSessions = 0

                for (doc in documents) {
                    val sheet = doc.toObject(AttendanceSheet::class.java)
                    val myRecord = sheet.records.find { it.studentId == currentUserUid }
                    
                    myRecord?.let {
                        // On réutilise l'adapter mais en affichant seulement nos records
                        // On pourrait créer un adapter spécifique pour l'historique étudiant
                        myAttendanceList.add(it.copy(studentName = "${sheet.date} - ${it.status}"))
                        if (it.status == "PRESENT") presentCount++
                        totalSessions++
                    }
                }

                val adapter = AttendanceAdapter(myAttendanceList, false)
                binding.rvStudents.layoutManager = LinearLayoutManager(this)
                binding.rvStudents.adapter = adapter
                
                val rate = if (totalSessions > 0) (presentCount * 100 / totalSessions) else 0
                binding.tvStatus.text = "Taux de présence : $rate% ($presentCount/$totalSessions)"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erreur lors du chargement de l'historique", Toast.LENGTH_SHORT).show()
            }
    }
}
