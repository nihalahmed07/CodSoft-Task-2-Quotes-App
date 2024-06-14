package com.alphax.quotesapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alphax.quotesapp.models.Quote

// Database configurations
@Database(
    entities = [Quote::class],
    version = 1
)
abstract class QuoteDataBase : RoomDatabase() {

    // DAO object
    abstract fun getQuoteDao(): QuoteDao
}
