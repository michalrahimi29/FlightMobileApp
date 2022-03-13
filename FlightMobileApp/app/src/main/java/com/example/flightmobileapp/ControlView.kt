package com.example.flightmobileapp

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Math.abs

class ControlView : AppCompatActivity() {
    var aileron = 0.0
    var elevator = 0.0
    var rudderValue = 0.0
    var throttleValue = 0.0
    var lastThrottle = 0.0
    var lastRudder = 0.0
    var bool = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_view)
        bool = true
        getSimImg(intent.getStringExtra("url").toString())
        firstSlider()
        secondSlider()
        post()
        val joystick =
            findViewById<io.github.controlwear.virtual.joystick.android.JoystickView>(R.id.JoysticView)
        joystick.setOnMoveListener { angle: Int, strength: Int ->
            val length = strength
            val x = length * kotlin.math.cos(Math.toRadians(angle * 1.0)) / 100
            val y = length * kotlin.math.sin(Math.toRadians(angle * 1.0)) / 100
            if (isChanged(x, aileron) || isChanged(y, elevator)) {
                aileron = x
                elevator = y
                CoroutineScope(IO).launch {
                    post()
                }
            }
        }
    }

    private fun firstSlider() {
        val seekR = findViewById<SeekBar>(R.id.seekRudder)
        seekR?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekR: SeekBar, progress: Int, fromUser: Boolean) {
                rudderValue = (progress.toDouble() - 100) / 100
                if (isChanged(rudderValue, lastRudder)) {
                    lastRudder = rudderValue
                    CoroutineScope(IO).launch {
                        post()
                    }
                }
            }

            override fun onStartTrackingTouch(seekR: SeekBar) {}
            override fun onStopTrackingTouch(seekR: SeekBar) {}
        })

    }

    private fun secondSlider() {
        val seekT = findViewById<SeekBar>(R.id.seekThrottle)
        seekT?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekT: SeekBar, progress: Int, fromUser: Boolean) {
                throttleValue = progress.toDouble() / 100
                if (isChanged(throttleValue, lastThrottle)) {
                    lastThrottle = throttleValue
                    CoroutineScope(IO).launch {
                        post()
                    }
                }
            }

            override fun onStartTrackingTouch(seekT: SeekBar) {}
            override fun onStopTrackingTouch(seekT: SeekBar) {}
        })
    }

    private fun isChanged(num1: Double, num2: Double): Boolean {
        return kotlin.math.abs(num1) > kotlin.math.abs(num2) * 1.01
    }

    private fun post() {

        val json: String = "{\"aileron\": $aileron,\n \"rudder\": $lastRudder,\n \"elevator\":" +
                " $elevator,\n \"throttle\": $lastThrottle\n}"
        val rb: RequestBody = RequestBody.create(MediaType.parse("application/json"), json)
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(intent.getStringExtra("url").toString())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(Api::class.java)
        val body = api.post(rb).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                    applicationContext, t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() != 200) {
                    Toast.makeText(
                        applicationContext, "Problem In Sending Values",
                        Toast.LENGTH_SHORT
                    ).show()
                    Toast.makeText(
                        applicationContext, "You Can Return To The Connection Screen ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun getSimImg(url: String) {
        CoroutineScope(IO).launch {
            while (bool) {
                val gson = GsonBuilder().setLenient().create()
                val retrofit = Retrofit.Builder().baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(gson)).build()
                val api = retrofit.create(Api::class.java)
                val body = api.getImg().enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.code() != 200) {
                            Toast.makeText(
                                applicationContext, "Problem In Getting Images",
                                Toast.LENGTH_SHORT
                            ).show()
                            Toast.makeText(
                                applicationContext, "You Can Return To The Connection"
                                        + " Screen ", Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val bstream = response.body()?.byteStream()
                            val bMap = BitmapFactory.decodeStream(bstream)
                            runOnUiThread {
                                val imageView = findViewById<ImageView>(R.id.imageView)
                                imageView.setImageBitmap(bMap)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                    }
                })
                delay(1000)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!bool) {
            getSimImg(intent.getStringExtra("url").toString())
        }
    }

    override fun onResume() {
        super.onResume()
        if (!bool) {
            getSimImg(intent.getStringExtra("url").toString())
        }
    }

    override fun onPause() {
        super.onPause()
        if (bool) {
            bool = false
        }
    }

    override fun onStop() {
        super.onStop()
        bool = false
    }
}