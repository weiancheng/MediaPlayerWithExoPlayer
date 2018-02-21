package weian.cheng.mediaplayerwitexoplayer

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Button
import weian.cheng.mediaplayerwithexoplayer.ExoPlayerEventListener
import weian.cheng.mediaplayerwithexoplayer.ExoPlayerWrapper
import weian.cheng.mediaplayerwithexoplayer.MusicPlayerState

class MainActivity:AppCompatActivity() {
    private val url = "https://soundsthatmatterblog.files.wordpress.com/2012/12/04-just-give-me-a-reason-feat-nate-ruess.mp3"
    private val local = "/storage/emulated/0/Download/ttest.mp3"

    private lateinit var player: ExoPlayerWrapper

    private val permissionsStorage: Array<String> = arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
    private val permissionsRequestCode = 1

    private var listener = ExoPlayerEventListener.PlayerEventListener {
        onDurationChanged = { duration ->
            Log.i("MainActivity", "onDurationChanged: $duration")
        }

        onCurrentTime = { sec ->
            Log.i("MainActivity", "onCurrentTime: $sec")
        }

        onBufferPercentage = { percent ->
            Log.i("MainActivity", "onBufferPercentage: $percent")
        }

        onPlayerStateChanged = { state ->
            Log.i("MainActivity", "onPlayerStateChanged: $state")
        }

        onDownloadTrack = { isSuccess ->
            Log.i("MainActivity", "onDownloadTrack: $isSuccess")
        }
    }

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requirePermission()

        player = ExoPlayerWrapper(this.applicationContext)

        player.setEventListener(listener)

        val play = findViewById<Button>(R.id.btn_play) as Button
        play.setOnClickListener { view ->
            player.play(url)
            if (player.writeToFile(url, local)) {
                Log.i("Weian", "play button, url is valid")
            } else {
                Log.i("Weian", "play button, url is not valid")
            }
            player.seekTo(220)
        }

        val stop = findViewById<Button>(R.id.btn_stop) as Button
        stop.setOnClickListener { view ->
            player.stop()
        }

        val next = findViewById<Button>(R.id.btn_next) as Button
        next.setOnClickListener { view ->
            if (player.getPlayerState() == MusicPlayerState.Play) {
                player.stop()
            }
            player.play(local)
        }

        val previous = findViewById<Button>(R.id.btn_prev) as Button
        previous.setOnClickListener { view ->
            if (player.getPlayerState() == MusicPlayerState.Play) {
                player.stop()
            }
            player.play(url)
        }
    }

    private fun requirePermission() {
        ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE).takeIf {
            it == PackageManager.PERMISSION_DENIED
        }?.let {
            ActivityCompat.requestPermissions(this,
                    permissionsStorage, permissionsRequestCode)
        }
    }
}
