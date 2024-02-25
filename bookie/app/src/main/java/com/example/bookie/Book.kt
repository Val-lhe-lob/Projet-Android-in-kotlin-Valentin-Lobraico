package com.example.bookie

import java.io.Serializable

data class BookResponse(
    val items: List<Volumes>
): Serializable

data class Volumes(
    val volumeInfo: Book
): Serializable

data class Book(
    val title: String?,
    val authors: List<String>?,
    val imageLinks: ImageLinks?,
    val description : String?,
    val publishedDate:String?,
    val pageCount:Int?,
    val averageRating: Float?,
): Serializable


data class ImageLinks(
    val small: String?,
    val thumbnail: String?,
    val large:String?,
): Serializable