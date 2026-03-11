package com.hugodev.red_up.features.publications.data.datasources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface PublicationDao {
    @Query("SELECT * FROM publicaciones_cache ORDER BY id DESC")
    suspend fun getAll(): List<PublicationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(publication: PublicationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(publications: List<PublicationEntity>)

    @Query("DELETE FROM publicaciones_cache WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM publicaciones_cache")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(publications: List<PublicationEntity>) {
        clear()
        upsertAll(publications)
    }
}
