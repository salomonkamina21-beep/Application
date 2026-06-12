package com.precensia.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.precensia.app.R
import com.precensia.app.models.AttendanceRecord

class AttendanceAdapter(
    private val records: MutableList<AttendanceRecord>,
    private val isEditable: Boolean
) : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvStudentName)
        val rbPresent: RadioButton = view.findViewById(R.id.rbPresent)
        val rbAbsent: RadioButton = view.findViewById(R.id.rbAbsent)
        val rbLate: RadioButton = view.findViewById(R.id.rbLate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]
        holder.tvName.text = record.studentName

        // Configuration de l'état initial
        when (record.status) {
            "PRESENT" -> holder.rbPresent.isChecked = true
            "ABSENT" -> holder.rbAbsent.isChecked = true
            "LATE" -> holder.rbLate.isChecked = true
        }

        // Désactiver les contrôles si on est en mode lecture seule (élève)
        holder.rbPresent.isEnabled = isEditable
        holder.rbAbsent.isEnabled = isEditable
        holder.rbLate.isEnabled = isEditable

        // Gestion des changements d'état
        holder.rbPresent.setOnClickListener { record.status = "PRESENT" }
        holder.rbAbsent.setOnClickListener { record.status = "ABSENT" }
        holder.rbLate.setOnClickListener { record.status = "LATE" }
    }

    override fun getItemCount() = records.size

    fun getRecords(): List<AttendanceRecord> = records
}
