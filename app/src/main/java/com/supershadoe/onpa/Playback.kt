package com.supershadoe.onpa

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import java.io.BufferedInputStream
import java.io.IOException
import java.net.Socket

internal class Playback(private val mServer: String, Port: String, audioManager: AudioManager) : Runnable {
    private var mTerminate: Boolean = false
    private val mPort: Int = Port.toInt()
    private val mAudioManager: AudioManager = audioManager
    fun terminate() {
        mTerminate = true
    }

    override fun run() {
        var sock: Socket? = null
        var audioData: BufferedInputStream? = null
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
                audioData = BufferedInputStream(sock!!.getInputStream())
            } catch (e: IOException) {
                terminate()
                e.printStackTrace()
            }
        }
        val sampleRate = 48000
        val musicLength = AudioTrack.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT)
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        val audioFormat: AudioFormat = AudioFormat.Builder()
                .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRate)
                .build()
        val audioTrackSessionId: Int = mAudioManager.generateAudioSessionId()
        val audioTrack: AudioTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(musicLength)
                    .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .setSessionId(audioTrackSessionId)
                    .build()
        } else {
            AudioTrack(audioAttributes, audioFormat, musicLength, AudioTrack.MODE_STREAM, audioTrackSessionId)
        }
        audioTrack.flush()
        audioTrack.play()
        val audioBuffer = ByteArray(musicLength * 8)
        while (!mTerminate) {
            try {
                val sizeRead = audioData!!.read(audioBuffer, 0, musicLength * 8)
                var sizeWrite = audioTrack.write(audioBuffer, 0, sizeRead)
                if (sizeWrite == AudioTrack.ERROR_INVALID_OPERATION || sizeWrite == AudioTrack.ERROR_BAD_VALUE) {
                    sizeWrite = 0
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        audioTrack.pause()
        audioTrack.flush()
        audioTrack.release()
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