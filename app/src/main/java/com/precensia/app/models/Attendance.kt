package com.precensia.app.models

data class AttendanceRecord(
    val studentId: String = "",
    val studentName: String = "",
    var status: String = "PRESENT" // "PRESENT", "ABSENT", "LATE"
)

data class AttendanceSheet(
    val id: String = "",
    val date: String = "",
    val records: List<AttendanceRecord> = emptyList(),
    var isValidated: Boolean = false,
    val submittedBy: String = ""
)
