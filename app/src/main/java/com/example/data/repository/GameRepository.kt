package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.data.local.GameDao
import com.example.data.local.GameDatabase
import com.example.engine.EngineRegistry
import com.example.model.GameItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GameRepository(private val gameDao: GameDao) {

    val allGames: Flow<List<GameItem>> = gameDao.getAllGames()

    fun getGameById(id: Long): Flow<GameItem?> = gameDao.getGameById(id)

    suspend fun getGameByIdDirect(id: Long): GameItem? = gameDao.getGameByIdDirect(id)

    suspend fun addGameFromUri(context: Context, uri: Uri): Result<GameItem> = withContext(Dispatchers.IO) {
        try {
            // 1. Attempt to take persistable URI permission so it survives app reboots
            try {
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            } catch (e: SecurityException) {
                Log.w("GameRepository", "Persistable URI permission not granted or unsupported: ${e.message}")
            }

            // 2. Query file metadata (name and size) via ContentResolver
            var displayName: String? = null
            var sizeBytes: Long = 0L

            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        displayName = cursor.getString(nameIndex)
                    }
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1 && !cursor.isNull(sizeIndex)) {
                        sizeBytes = cursor.getLong(sizeIndex)
                    }
                }
            }

            // Fallback for name if query was null
            val finalName = displayName ?: uri.lastPathSegment?.substringAfterLast('/') ?: "game_rom"

            // Extract extension
            val extension = finalName.substringAfterLast('.', "").lowercase()

            // Deduce platform from registry
            val platformInfo = EngineRegistry.inferPlatform(extension)

            // Check if game with same URI already exists
            val existing = gameDao.getGameByUri(uri.toString())
            if (existing != null) {
                // Update availability and return
                val updated = existing.copy(
                    isAvailable = true,
                    statusMessage = "جاهز للربط",
                    lastVerified = System.currentTimeMillis()
                )
                gameDao.updateGame(updated)
                return@withContext Result.success(updated)
            }

            // Create new GameItem
            val newItem = GameItem(
                uriString = uri.toString(),
                fileName = finalName,
                fileExtension = extension,
                fileSizeBytes = sizeBytes,
                platformName = platformInfo.nameAr,
                platformCode = platformInfo.code,
                badgeColorHex = platformInfo.badgeColorHex,
                dateAdded = System.currentTimeMillis(),
                isAvailable = true,
                lastVerified = System.currentTimeMillis(),
                statusMessage = "جاهز للربط"
            )

            val generatedId = gameDao.insertGame(newItem)
            val insertedItem = newItem.copy(id = generatedId)
            Result.success(insertedItem)
        } catch (e: Exception) {
            Log.e("GameRepository", "Failed to add game from URI: $uri", e)
            Result.failure(e)
        }
    }

    suspend fun verifyGameAvailability(context: Context, game: GameItem): GameItem = withContext(Dispatchers.IO) {
        val uri = Uri.parse(game.uriString)
        var isAvailable = false
        var status = "جاهز للربط"

        try {
            // Check if we can open a file descriptor for reading
            context.contentResolver.openFileDescriptor(uri, "r")?.use {
                isAvailable = true
                status = "جاهز للربط"
            } ?: run {
                isAvailable = false
                status = "الملف غير متوفر (تعذر الوصول)"
            }
        } catch (e: SecurityException) {
            isAvailable = false
            status = "أُلغيت صلاحية الوصول للملف"
            Log.w("GameRepository", "SecurityException verifying URI: ${game.uriString}", e)
        } catch (e: Exception) {
            isAvailable = false
            status = "الملف مفقود أو تم نقله"
            Log.w("GameRepository", "Exception verifying URI: ${game.uriString}", e)
        }

        val updated = game.copy(
            isAvailable = isAvailable,
            statusMessage = status,
            lastVerified = System.currentTimeMillis()
        )
        gameDao.updateGame(updated)
        updated
    }

    suspend fun verifyAllGames(context: Context, currentGames: List<GameItem>): List<GameItem> = withContext(Dispatchers.IO) {
        currentGames.map { game ->
            verifyGameAvailability(context, game)
        }
    }

    suspend fun deleteGame(context: Context, game: GameItem) = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(game.uriString)
            context.contentResolver.releasePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: Exception) {
            Log.w("GameRepository", "Could not release persistable permission: ${e.message}")
        }
        gameDao.deleteGame(game)
    }

    suspend fun clearAll(context: Context, games: List<GameItem>) = withContext(Dispatchers.IO) {
        games.forEach { game ->
            try {
                val uri = Uri.parse(game.uriString)
                context.contentResolver.releasePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Ignore permission release errors on bulk clear
            }
        }
        gameDao.deleteAll()
    }

    companion object {
        fun create(context: Context): GameRepository {
            val db = GameDatabase.getDatabase(context)
            return GameRepository(db.gameDao())
        }
    }
}
