package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "games")
data class GameItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uriString: String,
    val fileName: String,
    val fileExtension: String,
    val fileSizeBytes: Long,
    val platformName: String,
    val platformCode: String,
    val badgeColorHex: String = "#9D4EDD",
    val dateAdded: Long = System.currentTimeMillis(),
    val isAvailable: Boolean = true,
    val lastVerified: Long = System.currentTimeMillis(),
    val statusMessage: String = "جاهز للربط",
    val notes: String = ""
) {
    val formattedSize: String
        get() {
            if (fileSizeBytes <= 0) return "حجم غير معروف"
            val kb = fileSizeBytes / 1024.0
            val mb = kb / 1024.0
            val gb = mb / 1024.0
            return when {
                gb >= 1.0 -> String.format(Locale.US, "%.2f جيجابايت", gb)
                mb >= 1.0 -> String.format(Locale.US, "%.1f ميجابايت", mb)
                else -> String.format(Locale.US, "%.0f كيلوبايت", kb)
            }
        }

    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale.getDefault())
            return sdf.format(Date(dateAdded))
        }

    val cleanName: String
        get() {
            val dotIndex = fileName.lastIndexOf('.')
            return if (dotIndex > 0) fileName.substring(0, dotIndex) else fileName
        }
}
