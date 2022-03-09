package com.example.k2022_03_09_radio

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

var url = "http://stream.whus.org:8000/whusfm"

class MainActivity : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var mediaPlayer: MediaPlayer
    private var radioOn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button)

        setUpRadio()

        button.setOnClickListener{
            if (radioOn) {
                mediaPlayer.pause()
                button.setText("Radio On")
            } else {
                mediaPlayer.start()
                button.setText("Radio Off")
            }
            radioOn = ! radioOn
        }


    }

    private fun setUpRadio() {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(url)
            prepare()
        }
    }

}