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
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        db.collection("attendance").document(date).get()
            .addOnSuccessListener { doc ->
                val sheet = doc.toObject(AttendanceSheet::class.java)
                sheet?.let {
                    val adapter = AttendanceAdapter(it.records.toMutableList(), false)
                    binding.rvStudents.layoutManager = LinearLayoutManager(this)
                    binding.rvStudents.adapter = adapter
                    
                    val status = if (it.isValidated) "Validée" else "En attente de validation"
                    binding.tvStatus.text = "Statut: $status"
                } ?: run {
                    Toast.makeText(this, "Fiche non encore disponible", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
