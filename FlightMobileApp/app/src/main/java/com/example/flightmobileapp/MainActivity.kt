package com.example.flightmobileapp

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var urlViewModel: UrlViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = UrlListAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        urlViewModel = ViewModelProvider(this).get(UrlViewModel::class.java)
        urlViewModel.allUrls.observe(this, Observer { urls ->
            // Update the cached copy of the words in the adapter.
            urls?.let { adapter.setUrls(it) }
        })
        findViewById<Button>(R.id.connect).setOnClickListener {
            handleUrl(it)
        }
    }

    private fun handleUrl(view: View) {
        val edittext = findViewById<EditText>(R.id.editTextTextPersonName)
        val url = edittext.text.toString()
        val list = urlViewModel.allUrls
        val urlList = list.value
        if (urlList != null) {
            if (urlList.isNotEmpty()) {
                checkUrl(url, urlList)
            } else {
                val time = System.currentTimeMillis()
                val urlItem = Url(time, url)
                urlViewModel.insert(urlItem)
            }
        }
        connectBtn(url)
    }

    private fun checkUrl(url: String, urlList: List<Url>) {
        for (item in urlList) {
            if (item.url != url) {
                continue
            } else {
                item.time = System.currentTimeMillis()
                return
            }
        }
        val time = System.currentTimeMillis()
        val urlItem = Url(time, url)
        urlViewModel.insert(urlItem)
    }

    override fun onItemClick(url: Url) {
        val edittext = findViewById<EditText>(R.id.editTextTextPersonName)
        edittext.setText(url.url)
        val time = System.currentTimeMillis()
        val item = Url(time, url.url)
        urlViewModel.delete(url)
        urlViewModel.insert(item)
    }

    private fun connectBtn(urlAdress: String) {
        try {
            val gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder().baseUrl(urlAdress)
                .addConverterFactory(GsonConverterFactory.create(gson)).build()
            val api = retrofit.create(Api::class.java)
            val body = api.getImg().enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>, response: Response<ResponseBody>
                ) {
                    if (response.code() == 200) {
                        val bstream = response.body()?.byteStream()
                        val bMap = BitmapFactory.decodeStream(bstream)
                        nextScreen(urlAdress)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        applicationContext, "Connection Error, Please Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext, "Connection Error, Please Try Again ",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun nextScreen(url: String) {
        val intent = Intent(this, ControlView::class.java)
        intent.putExtra("url", url);
        startActivity(intent)
    }
}