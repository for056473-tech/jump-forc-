package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.GameItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY dateAdded DESC")
    fun getAllGames(): Flow<List<GameItem>>

    @Query("SELECT * FROM games WHERE id = :id")
    fun getGameById(id: Long): Flow<GameItem?>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGameByIdDirect(id: Long): GameItem?

    @Query("SELECT * FROM games WHERE uriString = :uriString LIMIT 1")
    suspend fun getGameByUri(uriString: String): GameItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameItem): Long

    @Update
    suspend fun updateGame(game: GameItem)

    @Delete
    suspend fun deleteGame(game: GameItem)

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM games")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM games")
    suspend fun getCount(): Int
}
