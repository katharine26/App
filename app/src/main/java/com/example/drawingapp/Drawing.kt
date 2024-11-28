package com.example.drawingapp


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.Date

/*
Defines my drawing using a filename and filepath
 */
@Entity(tableName = "drawings")
@Serializable
data class Drawing(var filename: String, var filepath: String) {
    @PrimaryKey(autoGenerate = true)
    @Transient
    var id: Int = 0 // integer primary key for the DB

}

