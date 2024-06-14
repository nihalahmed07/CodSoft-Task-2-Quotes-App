package com.alphax.quotesapp.api

import com.alphax.quotesapp.models.Quote
import retrofit2.Response
import retrofit2.http.GET

interface QuoteAPI {

    // function which get random quote from the API: returns a list with one quote
    // endpoints-> /random
    // full url-> https://zenquotes.io/api/random
    @GET("random")
    suspend fun getRandomQuote(): Response<List<Quote>>

    @GET("today")
    suspend fun getQuoteOfTheDay(): Response<List<Quote>>
}
