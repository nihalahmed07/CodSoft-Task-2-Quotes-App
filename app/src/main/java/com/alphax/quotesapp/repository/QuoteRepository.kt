package com.alphax.quotesapp.repository

import com.alphax.quotesapp.api.QuoteAPI
import com.alphax.quotesapp.db.QuoteDao
import com.alphax.quotesapp.models.Quote
import javax.inject.Inject

// bridge between View Model, API and Database
class QuoteRepository @Inject constructor(
    private val dao: QuoteDao,
    private val api: QuoteAPI
) {

    suspend fun getRandomQuote() = api.getRandomQuote()

    suspend fun getQuoteOfTheDay() = api.getQuoteOfTheDay()

    suspend fun upsert(quote: Quote) = dao.upsert(quote)

    suspend fun deleteQuote(quote: Quote) =
        dao.deleteSavedQuote(quote)

    fun getSavedQuotes() = dao.getSavedQuotes()
}
