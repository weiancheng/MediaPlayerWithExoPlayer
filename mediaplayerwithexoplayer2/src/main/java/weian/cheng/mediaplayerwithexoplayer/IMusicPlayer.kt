package weian.cheng.mediaplayerwithexoplayer

/**
 * Created by weian on 2017/12/10.
 *
 * The interface for the music player.
 */

interface IMusicPlayer {
    /**
     * Start playing a music.
     * This function will play the music which is specified with an URI.
     * If playing is failed, the function returns false.
     */
    fun play(uri: String): Boolean

    /**
     * This function is also about play the music, but when the music is playing,
     * executing this function will pause the music.
     * If the music is pausing, executing this function will resume the music.
     * If playing is failed, the function returns false.
     */
    fun play(): Boolean

    /**
     * stop playing the music.
     */
    fun stop()

    /**
     * pause a music. If no music is played, nothing to do.
     */
    fun pause()

    /**
     * resume the playing of the music.
     */
    fun resume()

    /**
     * set the repeat mode: normal play, repeat one music, repeat the whole playlist
     */
    fun setRepeat(isRepeat: Boolean)

    /**
     * seek the play time when the music is playing
     */
    fun seekTo(sec: Int)

    /**
     * The function is used to get the current state of the music player.
     * Standby: the music player is waiting for the music.
     * Play: the music player is playing.
     * Pause: the music player is pausing.
     */
    fun getPlayerState(): MusicPlayerState

    /**
     * The function is used to write the media file to local storage if the music player get the complete file.
     * Return true is that writing file successful.
     * Return false is that writing file unsuccessful.
     */
    fun writeToFile(url: String, filePath: String? = null): Boolean

    /**
     * The function is used to set up an event listener which monitor the activity of music player.
     *
     */
    fun setEventListener(listener: ExoPlayerEventListener.PlayerEventListener)
}
