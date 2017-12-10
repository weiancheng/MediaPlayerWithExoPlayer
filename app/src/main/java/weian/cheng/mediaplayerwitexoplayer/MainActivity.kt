package weian.cheng.mediaplayerwitexoplayer

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Button
import weian.cheng.mediaplayerwithexoplayer.ExoPlayerWrapper

class MainActivity:AppCompatActivity() {
    private val url = "https://soundsthatmatterblog.files.wordpress.com/2012/12/04-just-give-me-a-reason-feat-nate-ruess.mp3"
    private val local = "/storage/emulated/0/Download/ttest.mp3"

    private lateinit var player: ExoPlayerWrapper

    private val permissionsStorage: Array<String> = arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
    private val permissionsRequestCode = 1

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requirePermission()

        player = ExoPlayerWrapper(this.applicationContext)

        var play = findViewById<Button>(R.id.btn_play) as Button
        play.setOnClickListener { view ->
            player.play(url)
        }

        var stop = findViewById<Button>(R.id.btn_stop) as Button
        stop.setOnClickListener { view ->
            player.stop()
        }

        var next = findViewById<Button>(R.id.btn_next) as Button
        next.setOnClickListener { view ->
            if (player.getPlayerState() == ExoPlayerWrapper.PlayerState.Play) {
                player.stop()
            }
            player.play(local)
        }

        var previous = findViewById<Button>(R.id.btn_prev) as Button
        previous.setOnClickListener { view ->
            if (player.getPlayerState() == ExoPlayerWrapper.PlayerState.Play) {
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
