package com.dicoding.asclepius.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dicoding.asclepius.data.local.entity.CancerEntity

@Dao
interface CancerDao {
    @Query("SELECT * FROM cancers")
    fun getAllCancer() : List<CancerEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCancer(vararg cancers: CancerEntity)

    @Update
    fun updateCancer(cancer: CancerEntity)

    @Query("DELETE FROM cancers")
    fun deleteAllCancer()
}