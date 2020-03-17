package com.supershadoe.onpa

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.BufferedOutputStream
import java.io.IOException
import java.net.Socket

internal class Record(private val mServer: String, Port: String) : Runnable {
    private var mTerminate = false
    private val mPort: Int = Port.toInt()
    fun terminate() {
        mTerminate = true
    }

    override fun run() {
        var sock: Socket? = null
        var audioData: BufferedOutputStream? = null
        try {
            sock = Socket(mServer, mPort)
        } catch (e: IOException) {
            terminate()
            e.printStackTrace()
        } catch (e: SecurityException) {
            terminate()
            e.printStackTrace()
        }
        if (!mTerminate) {
            try {
                assert(sock != null)
                audioData = BufferedOutputStream(sock!!.getOutputStream())
            } catch (e: IOException) {
                terminate()
                e.printStackTrace()
            }
        }
        val sampleRate = 48000
        val musicLength = AudioRecord.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT)
        val audioRecord = AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                sampleRate, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, musicLength)
        audioRecord.startRecording()
        val audioBuffer = ByteArray(musicLength * 8)
        while (!mTerminate) {
            try {
                val sizeWrite = audioData!!.write(audioBuffer, 0, musicLength * 8)
                var sizeRead = audioRecord.read(audioBuffer, 0, AudioRecord.READ_NON_BLOCKING)
                if (sizeRead == AudioRecord.ERROR_INVALID_OPERATION || sizeRead == AudioRecord.ERROR_BAD_VALUE) {
                    sizeRead = 0
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        audioRecord.stop()
        audioRecord.release()
        if (sock != null && sock.isConnected) {
            try {
                sock.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        sock = null
        audioData = null
    }
}