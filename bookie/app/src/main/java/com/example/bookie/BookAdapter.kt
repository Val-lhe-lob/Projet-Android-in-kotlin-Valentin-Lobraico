package com.example.bookie

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bookie.databinding.ItemBookBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class BookAdapter(private val onBookClickListener: OnBookClickListener) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private val books = mutableListOf<Book>()
    private val firestore = Firebase.firestore


    fun addBooks(newBooks: List<Book>) {
        books.addAll(newBooks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.bind(book)
    }

    override fun getItemCount(): Int {
        return books.size
    }

    inner class BookViewHolder(private val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedBook = books[position]
                    onBookClickListener.onBookClick(clickedBook)
                }
            }
        }

        fun bind(book: Book) {
            binding.titleTextView.text = book.title
            binding.authorTextView.text = book.authors?.joinToString(", ") ?: "Unknown Author"

            // Vérifier la présence du livre dans le cloud Firestore et mets à jour la couleur du titre si il est dedans
            checkBookInFirestore(book.title.toString())

            book.imageLinks?.small?.let {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .fit()
                    .into(binding.thumbnailImageView, object : Callback {
                        override fun onSuccess() {
                            Log.d("Picasso", "Image loaded successfully")
                        }

                        override fun onError(e: Exception?) {
                            Log.e("Picasso", "Error loading image", e)
                        }
                    })
            }
        }

        private fun checkBookInFirestore(bookTitle: String) {
            firestore.collection("book")
                .whereEqualTo("booktitle", bookTitle)
                .get()
                .addOnSuccessListener { documents ->
                    val isBookPresent = !documents.isEmpty
                    // Change la couleur du titre en fonction de son existence dans le cloud Firestore
                    if (isBookPresent) {
                        binding.titleTextView.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green))
                    } else {
                        binding.titleTextView.setTextColor(ContextCompat.getColor(binding.root.context, R.color.red))
                    }
                }
        }
    }
}