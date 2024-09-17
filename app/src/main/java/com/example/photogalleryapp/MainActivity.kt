package com.example.photogalleryapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3) // Set GridLayoutManager programmatically

        fetchPhotos()
    }

    private fun fetchPhotos() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<List<Photo>> = RetrofitClient.apiService.getPhotos()
                if (response.isSuccessful) {
                    val photos = response.body()?.toMutableList() ?: mutableListOf()
                    // Add the two photo URLs
                    photos.add(Photo("1", "https://techingreek.com/wp-content/uploads/2014/08/android1.jpg"))
                    photos.add(Photo("2", "https://lumiere-a.akamaihd.net/v1/images/open-uri20150608-27674-18vqsrd_562644fe.jpeg?region=0%2C0%2C1580%2C880"))
                    withContext(Dispatchers.Main) {
                        photoAdapter = PhotoAdapter(photos)
                        recyclerView.adapter = photoAdapter
                    }
                } else {
                    // Handle the error gracefully
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}