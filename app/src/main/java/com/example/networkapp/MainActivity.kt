package com.example.networkapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var numberEditText: EditText
    private lateinit var showButton: Button
    private lateinit var comicImageView: ImageView
    private val fileName = "last_comic.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById(R.id.comicTitleTextView)
        descriptionTextView = findViewById(R.id.comicDescriptionTextView)
        numberEditText = findViewById(R.id.comicNumberEditText)
        showButton = findViewById(R.id.showComicButton)
        comicImageView = findViewById(R.id.comicImageView)

        showButton.setOnClickListener {
            val comicId = numberEditText.text.toString()
            if (comicId.isNotEmpty()) {
                downloadComic(comicId)
            } else {
                Toast.makeText(this, "Please enter a comic number", Toast.LENGTH_SHORT).show()
            }
        }

        // Load saved comic on app start if file exists
        loadSavedComic()
    }

    // Fetch comic data from the web
    private fun downloadComic(comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        val jsonObjectRequest = JsonObjectRequest(
            url,
            { response ->
                showComic(response)
                saveComic(response)
            },
            { error ->
                Toast.makeText(this, "Failed to load comic: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

    // Display the comic using the given JSON object
    private fun showComic(comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
    }

    // Save comic data to external storage
    private fun saveComic(comicObject: JSONObject) {
        try {
            val file = File(getExternalFilesDir(null), fileName)
            file.writeText(comicObject.toString()) // Save the JSON as a string
            Toast.makeText(this, "Comic saved successfully to external storage", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to save comic: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Load comic data from external storage and display it
    private fun loadSavedComic() {
        val file = File(getExternalFilesDir(null), fileName)
        if (file.exists()) {
            try {
                val jsonString = file.readText() // Read the saved JSON string
                val comicObject = JSONObject(jsonString)
                showComic(comicObject) // Display the comic
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to load saved comic: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No saved comic found in external storage", Toast.LENGTH_SHORT).show()
        }
    }
}
