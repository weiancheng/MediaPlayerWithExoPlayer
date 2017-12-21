package weian.cheng.mediaplayerwithexoplayer


/**
 * Created by weian on 2017/12/21.
 *
 */

class ExoPlayerEventListener {
    private interface IEventListener {
        fun onDurationChanged(duration: Int)
        fun onBufferPercentage(percent: Int)
        fun onCurrentTime(second: Int)
        fun onPlayerStateChanged(state: MusicPlayerState)
    }

    class PlayerEventListenerFunc {
        var onDurationChanged: ((duration: Int) -> Unit)? = null
        var onBufferPercentage: ((percent: Int) -> Unit)? = null
        var onCurrentTime: ((second: Int) -> Unit)? = null
        var onPlayerStateChanged: ((state: MusicPlayerState) -> Unit)? = null
    }

    class PlayerEventListener(func: PlayerEventListenerFunc.() -> Unit): IEventListener {

        private var func = PlayerEventListenerFunc().apply(func)

        override fun onDurationChanged(duration: Int) {
            func.onDurationChanged?.invoke(duration)
        }

        override fun onBufferPercentage(percent: Int) {
            func.onBufferPercentage?.invoke(percent)
        }

        override fun onCurrentTime(second: Int) {
            func.onCurrentTime?.invoke(second)
        }

        override fun onPlayerStateChanged(state: MusicPlayerState) {
            func.onPlayerStateChanged?.invoke(state)
        }
    }
}