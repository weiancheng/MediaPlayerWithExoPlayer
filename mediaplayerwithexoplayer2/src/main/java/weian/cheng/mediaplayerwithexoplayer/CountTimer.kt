package weian.cheng.mediaplayerwithexoplayer

import android.os.CountDownTimer

/**
 * Created by weian on 2017/11/30.
 *
 */

class CountTimer {

    private lateinit var timer: CountDownTimer

    fun start() {
    }

    fun stop() {
    }

    fun pause() {
    }

    fun resume() {
    }

    interface ICountTimerListener {
        fun countUp()
        fun countDown()
    }

    class ListenerFunc {
        var countUp: (time: Int) -> Unit = {}
        var countDown: (time: Int) -> Unit = {}
    }

    class CountTImerListener(func: ListenerFunc.() -> Unit): ICountTimerListener {
        override fun countUp() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun countDown() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}