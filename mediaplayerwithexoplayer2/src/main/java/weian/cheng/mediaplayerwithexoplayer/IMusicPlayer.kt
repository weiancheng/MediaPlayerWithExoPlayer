package weian.cheng.mediaplayerwithexoplayer

/**
 * Created by weian on 2017/12/10.
 */

interface IMusicPlayer {
    fun play(uri: String)
    fun play()
    fun stop()
    fun pause()
    fun resume()
    fun setRepeat(isRepeat: Boolean)
    fun seekTo(sec: Int)
    fun getPlayerState(): MusicPlayerState
}
