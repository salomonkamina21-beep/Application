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
        // Amélioration : Charger toutes les fiches non validées au lieu d'une seule par date fixe
        db.collection("attendance")
            .whereEqualTo("isValidated", false)
            .limit(1) // Pour l'instant on traite la plus ancienne en attente
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
                    currentSheet = doc.toObject(AttendanceSheet::class.java)
                    currentSheet?.let { sheet ->
                        adapter = AttendanceAdapter(sheet.records.toMutableList(), true)
                        binding.rvStudents.layoutManager = LinearLayoutManager(this)
                        binding.rvStudents.adapter = adapter
                        binding.tvTitle.text = "Validation : ${sheet.date}"
                    }
                } else {
                    Toast.makeText(this, "Aucune fiche en attente de validation", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateAttendance() {
        currentSheet?.let { sheet ->
            val updatedSheet = sheet.copy(
                isValidated = true, 
                records = adapter.getRecords()
            )
            // Utiliser l'ID réel du document pour la mise à jour
            db.collection("attendance").document(sheet.id).set(updatedSheet)
                .addOnSuccessListener {
                    Toast.makeText(this, "Fiche validée avec succès", Toast.LENGTH_SHORT).show()
                    loadTodayAttendance() // Charger la suivante s'il y en a une
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erreur de validation : ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
