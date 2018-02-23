package weian.cheng.mediaplayerwithexoplayer

import android.os.Environment
import android.util.Log
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import android.os.Environment.DIRECTORY_MUSIC

class DownloadHandler(private val url: String, filePath: String? = null, private val eventListener: ExoPlayerEventListener.PlayerEventListener? = null) : Thread() {
    private val tag = "DownloadHandler"
    private val bufferSize = 2048
    private val tempTrackName = "temp_track.mp3"
    private val tempTrackPath = Environment.getExternalStorageDirectory().toString() + "/" + DIRECTORY_MUSIC + "/" + tempTrackName
    private var totalSize: Int = -1
    private var path: String? = null
    private var file: File? = null
    private var listener: ExoPlayerEventListener.PlayerEventListener? = null

    init {
        when (filePath.isNullOrEmpty()) {
            true -> path = tempTrackPath
            false -> path = filePath
        }
        listener = eventListener
    }

    override fun run() {
        super.run()
        Runnable {
            if (!isRemoteAvailable()) {
                listener?.onDownloadTrack(false)
                Thread.currentThread().interrupt()
            }

            if (!createFile()) {
                listener?.onDownloadTrack(false)
                Thread.currentThread().interrupt()
            }

            if (!downloadFile()) {
                listener?.onDownloadTrack(false)
                Thread.currentThread().interrupt()
            } else {
                listener?.onDownloadTrack(true)
            }

        }.run()
    }

    private fun isRemoteAvailable(): Boolean {
        var result = false
        Log.i(tag, "url is $url")
        val httpURLConnection = URL(url).openConnection() as HttpURLConnection
        HttpURLConnection.setFollowRedirects(false)
        httpURLConnection.connect()
        httpURLConnection.requestMethod = "GET"
        if (httpURLConnection.responseCode == HttpURLConnection.HTTP_OK)
            result = true

        totalSize = httpURLConnection.contentLength
        httpURLConnection.disconnect()
        return result
    }

    private fun createFile(): Boolean {
        file = File(path)
        file?.takeIf { it.exists() }.let {
            it?.createNewFile()
        }

        return true
    }

    private fun downloadFile(): Boolean {
        val fileOutputStream = FileOutputStream(file)
        val inputStream = BufferedInputStream(URL(url).openStream())

        var downloadSize = 0

        val buffer = ByteArray(bufferSize)
        var bufferLength: Int
        while (true) {
            bufferLength = inputStream.read(buffer)
            if (bufferLength <= 0)
                break
            fileOutputStream.write(buffer, 0, bufferLength)
            downloadSize += bufferLength
        }

        fileOutputStream.flush()
        fileOutputStream.close()
        inputStream.close()
        if (downloadSize != totalSize) {
            Log.e(tag, "file was not complete")
            return false
        }
        return true
    }
}