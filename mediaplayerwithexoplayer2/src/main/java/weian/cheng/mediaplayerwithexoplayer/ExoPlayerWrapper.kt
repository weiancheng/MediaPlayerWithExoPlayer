package weian.cheng.mediaplayerwithexoplayer

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_BUFFERING
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.google.android.exoplayer2.Player.STATE_IDLE
import com.google.android.exoplayer2.Player.STATE_READY
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
import java.lang.Exception

import weian.cheng.mediaplayerwithexoplayer.MusicPlayerState.Standby
import weian.cheng.mediaplayerwithexoplayer.MusicPlayerState.Pause
import weian.cheng.mediaplayerwithexoplayer.MusicPlayerState.Play

/**
 * Created by weian on 2017/11/28.
 *
 */

class ExoPlayerWrapper(context: Context): IMusicPlayer {

    private val TAG = "ExoPlayerWrapper"
    private var context: Context
    private lateinit var exoPlayer: SimpleExoPlayer
    private var isPlaying = false
    private lateinit var timer: PausableTimer
    private var playerState = Standby


    // listeners
    private var durationListener: (duration: Int) -> Unit = {}
    private var bufferPercentage: (percent: Int) -> Unit = {}
    private var currentTime: (time: Int) -> Unit = {}

    init {
        this.context = context
    }

    override fun play(uri: String) {
        if (playerState == Play) {
            // TODO: find out a appropriate exception or make one.
            throw Exception("now is playing")
        }

        initExoPlayer(uri)
        exoPlayer.playWhenReady = true
        playerState = Play
    }

    override fun play() {
        when (isPlaying) {
            true -> {
                timer.pause()
                playerState = Pause
            }

            false -> {
                timer.resume()
                playerState = Play
            }
        }

        if (isPlaying) {
            timer.pause()
            playerState = Pause
        } else {
            timer.resume()
            playerState = Play
        }
        exoPlayer.playWhenReady = !isPlaying
    }

    override fun stop() {
        exoPlayer.playWhenReady = false
        exoPlayer.release()
        timer.stop()
        playerState = Standby
    }

    override fun pause() {
        exoPlayer.playWhenReady = false
        timer.pause()
        playerState = Pause
    }

    override fun resume() {
        exoPlayer.playWhenReady = true
        timer.resume()
        playerState = Play
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

    private fun initExoPlayer(url: String) {
        Log.i(TAG, "initExoPlayer")
        val meter = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "LocalExoPlayer"), meter)
        val uri = Uri.parse(url)
        val extractorMediaSource = ExtractorMediaSource(uri, dataSourceFactory, DefaultExtractorsFactory(), null, null)
        val trackSelector = DefaultTrackSelector(meter)

        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
        exoPlayer.addListener(LocalPlayerEventListener(this, exoPlayer))
        exoPlayer.prepare(extractorMediaSource)
    }

    private class LocalPlayerEventListener(player: ExoPlayerWrapper,
                                           exoplayer: ExoPlayer): Player.EventListener {

        private var exoPlayer: ExoPlayer
        private var musicPlayer: ExoPlayerWrapper

        init {
            exoPlayer = exoplayer
            musicPlayer = player
        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            musicPlayer.isPlaying = playWhenReady
            if (playbackState == STATE_ENDED) {
                musicPlayer.playerState = Standby
            }
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            musicPlayer.bufferPercentage(exoPlayer.bufferedPercentage)
        }

        override fun onPositionDiscontinuity() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
            if (exoPlayer.duration > 0) {
                musicPlayer.durationListener(exoPlayer.duration.div(1000).toInt())
            }

            musicPlayer.timer = PausableTimer(exoPlayer.duration, 1)
            musicPlayer.timer.onTick = { millisUntilFinished ->
                musicPlayer.currentTime(millisUntilFinished.div(1000).toInt())
            }
            musicPlayer.timer.onFinish = {
                musicPlayer.currentTime(exoPlayer.duration.div(1000).toInt())
            }
            musicPlayer.timer.start()
        }
    }
}