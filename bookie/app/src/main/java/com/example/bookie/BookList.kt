package com.example.bookie

import android.util.Log
import kotlinx.coroutines.InternalCoroutinesApi
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object BookApi {
    private const val BASE_URL = "https://www.googleapis.com/books/v1/"
    private const val API_KEY = "AIzaSyC-oqciOHywzKdpNuMHQba1KQffR_dNXg4"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: BookApiService = retrofit.create(BookApiService::class.java)

    @OptIn(InternalCoroutinesApi::class)
    suspend fun getBooks(startIndex: Int, maxResults: Int): List<Book> {
        try {
            val response: BookResponse = service.getBooks(startIndex, maxResults, API_KEY)
            Log.d("test",response.toString())
            return response.items.map { volume ->
                val bookInfo = volume.volumeInfo
                val imageLinks = bookInfo.imageLinks?.let {
                    ImageLinks( it.thumbnail,it.large,it.small)
                }

                Book(bookInfo.title, bookInfo.authors, imageLinks,bookInfo.description,bookInfo.publishedDate,bookInfo.pageCount,bookInfo.averageRating)
            }
        } catch (e: HttpException) {
            // Log the details of the HTTP exception
            Log.e("load book", "HTTP exception: ${e.code()}")
            e.response()?.errorBody()?.string()?.let { Log.e("load book", it) }
            throw e
        } catch (e: IOException) {
            // Log the details of the IOException
            Log.e("load book", "IOException: ${e.message}")
            throw e
        } catch (e: Exception) {
            // Log the details of other exceptions
            Log.e("load book", "Exception: ${e.message}")
            throw e
        }
    }


}
