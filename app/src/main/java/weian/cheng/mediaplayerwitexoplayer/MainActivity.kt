package weian.cheng.mediaplayerwitexoplayer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import weian.cheng.mediaplayerwithexoplayer.ExoPlayerWrapper

class MainActivity:AppCompatActivity() {
    private val url = "https://soundsthatmatterblog.files.wordpress.com/2012/12/04-just-give-me-a-reason-feat-nate-ruess.mp3"
    private val local = "/storage/emulated/0/Download/ttest.mp3"

    private lateinit var player: ExoPlayerWrapper

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        player = ExoPlayerWrapper(this.applicationContext)
        player.play(url)
    }
}
