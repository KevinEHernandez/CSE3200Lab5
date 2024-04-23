package com.example.k2022_03_09_radio

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

var Index = 0

val urls = arrayOf(
    "http://stream.whus.org:8000/whusfm",
    "http://onair.dancewave.online:8080/",
    "https://stream.realhardstyle.nl/",
    "https://kathy.torontocast.com:3060/",
    "https://kawaii-music.stream.laut.fm/kawaii-music"
)

val videoUrls = arrayOf(
    "https://static.videezy.com/system/resources/previews/000/024/935/original/4k-stop-road-panel-background-with-glith-effects.mp4",
    "https://static.videezy.com/system/resources/previews/000/044/404/original/Hot_Strokes_HD_BG.mp4",
    "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
)


class MainActivity : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var radioButton: Button
    private lateinit var leftVidButton: Button
    private lateinit var rightVidButton: Button
    private lateinit var videoButton: Button
    private lateinit var imageView: ImageView
    private lateinit var stationRecyclerView: RecyclerView
    private lateinit var stationAdapter: StationAdapter

    private lateinit var mediaPlayer: MediaPlayer
    private var radioOn: Boolean = false

    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer

    private var isVideoPlaying: Boolean = false
    private var currentVideoIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button)
        leftButton = findViewById(R.id.leftButton)
        rightButton = findViewById(R.id.rightButton)
        radioButton = findViewById(R.id.radioButton)
        leftVidButton = findViewById(R.id.leftVidButton)
        rightVidButton = findViewById(R.id.rightVidButton)
        videoButton = findViewById(R.id.videoButton)
        imageView = findViewById(R.id.imageView)
        stationRecyclerView = findViewById(R.id.stationRecyclerView)
        playerView = findViewById(R.id.player_view)

        setUpRadio()
        setUpVideoPlayer()

        button.setOnClickListener {
                toggleRadio()
        }

        leftButton.setOnClickListener {
                flipStation(-1)
        }

        rightButton.setOnClickListener {
            flipStation(1)
        }

        leftVidButton.setOnClickListener {
            previousVideo()
        }

        rightVidButton.setOnClickListener {
            nextVideo()
        }

        videoButton.setOnClickListener {
            toggleVideo()
        }

        stationRecyclerView.layoutManager = LinearLayoutManager(this)

        stationAdapter = StationAdapter(urls) { position ->
            flipStation(position - Index)
            toggleRadio()
        }
        stationRecyclerView.adapter = stationAdapter
    }

    private fun setUpRadio() {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(urls[Index])
            prepareAsync()
        }

        mediaPlayer.setOnPreparedListener {
            radioOn = false
            updateRadioButtonText()
        }
    }

    private fun setUpVideoPlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView.player = player
        val mediaItem = MediaItem.fromUri(videoUrls[currentVideoIndex])
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    private fun toggleRadio() {
        radioOn = !radioOn
        updateRadioButtonText()

        if (radioOn) {
            mediaPlayer.start()
        } else {
            mediaPlayer.pause()
        }
    }

    private fun toggleVideo() {
        if (isVideoPlaying) {
            player.pause()
        } else {
            player.play()
        }
        isVideoPlaying = !isVideoPlaying
    }

    private fun updateRadioButtonText() {
            button.text = if (radioOn) "Radio Off" else "Radio On"
    }

    private fun flipStation(offset: Int) {
        val newIndex = (Index + offset + urls.size) % urls.size
        mediaPlayer.reset()
        mediaPlayer.setDataSource(urls[newIndex])
        mediaPlayer.prepareAsync()
        radioButton.text = urls[newIndex]

        /*val drawableId = when (newIndex) {
            0 -> R.drawable.d1
            1 -> R.drawable.d2
            2 -> R.drawable.d3
            3 -> R.drawable.d4
            4 -> R.drawable.d5
            else -> R.drawable.d1
        }
        imageView.setImageResource(drawableId)*/

        if (radioOn) {
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
            }
        }

        Index = newIndex
    }

    private fun nextVideo() {
        currentVideoIndex = (currentVideoIndex + 1) % videoUrls.size
        val mediaItem = MediaItem.fromUri(videoUrls[currentVideoIndex])
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    private fun previousVideo() {
        currentVideoIndex = (currentVideoIndex - 1 + videoUrls.size) % videoUrls.size
        val mediaItem = MediaItem.fromUri(videoUrls[currentVideoIndex])
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer.release()
    }
}
