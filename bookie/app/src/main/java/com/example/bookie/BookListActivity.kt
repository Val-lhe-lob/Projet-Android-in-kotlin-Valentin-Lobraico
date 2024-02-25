package com.example.bookie


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookie.databinding.BookListActivityBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookListActivity : Activity(), OnBookClickListener {

    private lateinit var binding: BookListActivityBinding
    private lateinit var bookAdapter: BookAdapter
    private var startIndex = 0
    private lateinit var disconnect: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BookListActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        disconnect = findViewById(R.id.disconnect)

        disconnect.setOnClickListener() {
                val intent = Intent(this, ConnexionActivity::class.java)
                startActivity(intent)
                finish()
        }
        setupRecyclerView()
        loadBooks()

    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        bookAdapter = BookAdapter(this) // Pass the listener instance
        binding.recyclerView.adapter = bookAdapter
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun loadBooks() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val books = BookApi.getBooks(0, 40)
                withContext(Dispatchers.Main) {
                    bookAdapter.addBooks(books)
                    startIndex += books.size
                }
            } catch (e: Exception) {
                Log.e("load book", "Failed to load books", e)
            }
        }
    }



    override fun onBookClick(book: Book) {
        val intent = Intent(this, BookDetailActivity::class.java)
        intent.putExtra("bookDetail", book)
        startActivity(intent)
    }
}