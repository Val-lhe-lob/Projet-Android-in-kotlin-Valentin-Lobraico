package com.example.bookie

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import com.example.bookie.databinding.ActivityBookDetailBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class BookDetailActivity : Activity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityBookDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val db = Firebase.firestore

        val bookDetail = intent.getSerializableExtra("bookDetail") as? Book

        if (bookDetail != null) {
            binding.titleTextView.text = "Titre : " + bookDetail.title
            binding.authorTextView.text =
                "Auteur(s) : " + bookDetail.authors?.joinToString(", ") ?: "Unknown Author"
            binding.descriptionTextView.text =
                "Description : " + (bookDetail.description ?: "No description")
            binding.publishedDateTextView.text =
                "Date de publication : " + (bookDetail.publishedDate ?: "No published date")
            binding.pageCountTextView.text =
                "Nombre de pages : " + (bookDetail.pageCount ?: "No page count").toString()
            binding.averageRatingTextView.text =
                "Notes : " + (bookDetail.averageRating ?: "No average rating").toString()

            bookDetail.imageLinks?.small?.let {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(binding.bookImageView)
            }

            val readButton = findViewById<Button>(R.id.readButton)
            val backButton = findViewById<Button>(R.id.btnBack)

            readButton.setOnClickListener {
                handleReadButton(bookDetail.title.toString(), currentUser?.uid) { bool, id ->
                    val db = Firebase.firestore
                    if (bool) {
                        val delBook = db.collection("book").document(id)
                        delBook.delete()
                            .addOnSuccessListener {
                                Log.i("Book Data Change", "Book has been deleted")
                            }
                            .addOnFailureListener { exception ->
                                Log.e(
                                    "Book Data Change",
                                    "Book couldn't be deleted or is already deleted"
                                )
                            }
                    } else {
                        val data = hashMapOf(
                            "userid" to currentUser?.uid,
                            "booktitle" to bookDetail.title,
                        )
                        db.collection("book")
                            .add(data)
                            .addOnSuccessListener { documentReference ->
                                Log.d(
                                    "Book Data Change",
                                    "DocumentSnapshot successfully written with ID: ${documentReference.id}"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.w("Book Data Change", "Error writing document", e)
                            }
                    }
                }
            }

            backButton.setOnClickListener {
                val intent = Intent(this, BookListActivity::class.java)
                startActivity(intent)
                finish()
            }

            // Écoute en temps réel les changements dans le cloud Firestore pour la couleur du titre du livre
            listenForBookChanges(bookDetail.title.toString())
        } else {
            binding.titleTextView.text = "Error loading book details"
            binding.authorTextView.visibility = View.GONE
        }
    }

    private fun handleReadButton(
        title: String,
        userId: String?,
        callback: (Boolean, String) -> Unit
    ) {
        val db = Firebase.firestore
        db.collection("book")
            .whereEqualTo("booktitle", title)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val documentUserId = document.getString("userid")
                    if (documentUserId == userId) {
                        callback(true, document.id)
                        return@addOnSuccessListener
                    }
                }
                callback(false, "")
            }
            .addOnFailureListener { exception ->
                Log.e("Book Data Change", "Error getting documents: ", exception)
                callback(false, "")
            }
    }

    private fun listenForBookChanges(bookTitle: String) {
        val db = Firebase.firestore
        db.collection("book")
            .whereEqualTo("booktitle", bookTitle)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("Book Data Change", "Listen failed", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    // Le livre est présent dans Firestore, change la couleur du titre -> vert
                    binding.titleTextView.setTextColor(ContextCompat.getColor(this, R.color.green))
                    binding.readButton.setText("Enlever des livres lus")
                } else {
                    // Le livre n'est pas présent dans Firestore, change la couleur du titre en rouge
                    binding.titleTextView.setTextColor(ContextCompat.getColor(this, R.color.red))
                    binding.readButton.setText("Mettre dans les livres lus")
                }
            }
    }
}