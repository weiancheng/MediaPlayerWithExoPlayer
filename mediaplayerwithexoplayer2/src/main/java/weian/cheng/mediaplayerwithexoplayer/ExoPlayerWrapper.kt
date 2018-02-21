package weian.cheng.mediaplayerwithexoplayer

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import weian.cheng.mediaplayerwithexoplayer.ExoPlayerEventListener.PlayerEventListener
import weian.cheng.mediaplayerwithexoplayer.MusicPlayerState.Pause
import weian.cheng.mediaplayerwithexoplayer.MusicPlayerState.Play
import weian.cheng.mediaplayerwithexoplayer.MusicPlayerState.Standby
import java.lang.Exception

/**
 * Created by weian on 2017/11/28.
 *
 */

class ExoPlayerWrapper(private val context: Context): IMusicPlayer {

    private val tag = "ExoPlayerWrapper"
    private lateinit var exoPlayer: SimpleExoPlayer
    private var isPlaying = false
    private lateinit var timer: PausableTimer
    private var playerState = Standby

    private var listener: PlayerEventListener ?= null

    override fun play(uri: String): Boolean {
        if (!isNetworkAvailable())
            return false

        if (playerState == Play) {
            // TODO: find out a appropriate exception or make one.
            throw Exception("now is playing")
        }

        initExoPlayer(uri)
        exoPlayer.playWhenReady = true
        setPlayerState(Play)
        return true
    }

    override fun play(): Boolean {
        when (isPlaying) {
            true -> {
                timer.pause()
                setPlayerState(Pause)
            }

            false -> {
                timer.resume()
                setPlayerState(Play)
            }
        }

        if (isPlaying) {
            timer.pause()
            setPlayerState(Pause)
        } else {
            timer.resume()
            setPlayerState(Play)
        }
        exoPlayer.playWhenReady = !isPlaying
        return true
    }

    override fun stop() {
        exoPlayer.playWhenReady = false
        exoPlayer.stop()
        timer.stop()
        setPlayerState(Standby)
    }

    override fun pause() {
        exoPlayer.playWhenReady = false
        timer.pause()
        setPlayerState(Pause)
    }

    override fun resume() {
        exoPlayer.playWhenReady = true
        timer.resume()
        setPlayerState(Play)
    }

    override fun setRepeat(isRepeat: Boolean) {
        if (isRepeat)
            exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        else
            exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
    }

    override fun seekTo(sec: Int) {
        exoPlayer.seekTo(sec.times(1000).toLong())
    }

    override fun getPlayerState() = playerState

    override fun writeToFile(url: String, filePath: String): Boolean {
        val downloadThread = DownloadHandler(url, filePath, listener)
        downloadThread.start()
        return true
    }

    override fun setEventListener(listener: PlayerEventListener) {
        this.listener = listener
    }

    private fun setPlayerState(state: MusicPlayerState) {
        if (state != playerState) {
            playerState = state
            listener?.onPlayerStateChanged(state)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.activeNetworkInfo == null) {
            return false
        }
        return true
    }

    private fun initExoPlayer(url: String) {
        Log.i(tag, "initExoPlayer")
        val meter = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "LocalExoPlayer"), meter)
        val uri = Uri.parse(url)
        val extractorMediaSource = ExtractorMediaSource(uri, dataSourceFactory, DefaultExtractorsFactory(), null, null)
        val trackSelector = DefaultTrackSelector(meter)

        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
        exoPlayer.addListener(LocalPlayerEventListener(this, exoPlayer))
        exoPlayer.prepare(extractorMediaSource)
    }

    private class LocalPlayerEventListener(private val musicPlayer: ExoPlayerWrapper,
                                           private val exoPlayer: ExoPlayer): Player.EventListener {

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            musicPlayer.isPlaying = playWhenReady
            if (playbackState == STATE_ENDED) {
                musicPlayer.setPlayerState(Standby)
            }
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            musicPlayer.listener?.onBufferPercentage(exoPlayer.bufferedPercentage)
        }

        override fun onPositionDiscontinuity() {
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
            val millis = 1000
            val threshold = 1000_000  // Avoiding the bigger timeline comes, cause the running time is incorrect.

            if (exoPlayer.duration in 1..threshold) {
                musicPlayer.listener?.onDurationChanged(exoPlayer.duration.div(millis).toInt())

                musicPlayer.timer = PausableTimer(exoPlayer.duration.minus(exoPlayer.currentPosition), millis.toLong())
                musicPlayer.timer.onTick = { millisUntilFinished ->
                    val time = (exoPlayer.duration - millisUntilFinished).div(millis).toInt()
                    musicPlayer.listener?.onCurrentTime(time)
                }
                musicPlayer.timer.onFinish = {
                    musicPlayer.listener?.onCurrentTime(0)
                }
                musicPlayer.timer.start()
            }
        }
    }
}