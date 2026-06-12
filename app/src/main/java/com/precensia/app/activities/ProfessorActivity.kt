package com.precensia.app.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.precensia.app.adapters.AttendanceAdapter
import com.precensia.app.databinding.ActivityProfessorBinding
import com.precensia.app.models.AttendanceSheet
import java.text.SimpleDateFormat
import java.util.*

class ProfessorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfessorBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: AttendanceAdapter
    private var currentSheet: AttendanceSheet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfessorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        loadTodayAttendance()

        binding.btnValidate.setOnClickListener {
            validateAttendance()
        }
    }

    private fun loadTodayAttendance() {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        db.collection("attendance").document(date).get()
            .addOnSuccessListener { doc ->
                currentSheet = doc.toObject(AttendanceSheet::class.java)
                currentSheet?.let { sheet ->
                    adapter = AttendanceAdapter(sheet.records.toMutableList(), true)
                    binding.rvStudents.layoutManager = LinearLayoutManager(this)
                    binding.rvStudents.adapter = adapter
                } ?: run {
                    Toast.makeText(this, "Aucune fiche pour aujourd'hui", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateAttendance() {
        currentSheet?.let { sheet ->
            val updatedSheet = sheet.copy(isValidated = true, records = adapter.getRecords())
            db.collection("attendance").document(sheet.date).set(updatedSheet)
                .addOnSuccessListener {
                    Toast.makeText(this, "Fiche validée et enregistrée", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }
}
