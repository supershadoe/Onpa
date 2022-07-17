/*
 * SPDX-FileCopyrightText: 2010 Ivan Lyapunov
 * The threaded code for playback is almost based on Ivan's code for PulseDroid
 * (https://github.com/dront78/pulsedroid -> GPL3 compatible with Apache 2 so
 * no issues on using Apache I guess)
 *
 * SPDX-FileCopyrightText: 2020-2022 supershadoe <supershadoe@proton.me>
 * SPDX-License-Identifier: Apache-2.0 OR GPL-3.0
 */

package me.supershadoe.onpa

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.util.Log

import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.ConnectException
import java.net.Socket

/**
 * Class containing the Runnable to play audio
 * @param ipAddress(String) IP Address of server
 * @param port(Int) Port to connect to
 * @param sampleRate(Int) Sample Rate (in Hertz) of audio
 * @param stereo(Boolean) To say if the audio is mono or stereo
 * @param audioManager(AudioManager) Instance of AudioManager for use inside thread as thread can't
 *                                   access applicationContext(and thus can't get system service)
 */
internal class PlaybackThread(
    audioManager: AudioManager,
    val ipAddress: String = "192.168.1.100",
    private val port: Int = 8000,
    sampleRate: Int = 48000,
    stereo: Boolean = true): Runnable {

    // Variable to denote if the thread needs to be terminated
    private var terminate: Boolean = false

    //terminate function: Terminates the thread by setting value of terminate variable to false
    internal fun terminate() { terminate = true}

    // Set mono or stereo
    private val stereoParam = if(stereo) AudioFormat.CHANNEL_OUT_STEREO else AudioFormat.CHANNEL_OUT_MONO

    // Get minimum buffer size
    private val minBufSize = AudioTrack.getMinBufferSize(sampleRate,
            stereoParam,
            AudioFormat.ENCODING_PCM_16BIT)

    // Setting values of various variables to initialize audioTrack
    private val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
    private val audioFormat: AudioFormat = AudioFormat.Builder()
            .setChannelMask(stereoParam)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .build()
    private val audioTrackSessionId: Int = audioManager.generateAudioSessionId()

    // audioTrack is set to be internal as audioTrack is accessed from service to set volume of audio
    internal val audioTrack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(minBufSize)
                .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .setSessionId(audioTrackSessionId)
                .build()
    else
        AudioTrack(audioAttributes, audioFormat, minBufSize, AudioTrack.MODE_STREAM, audioTrackSessionId)

    /**
     * run function: Runnable function which is handled by different Thread
     * The function which processes the audio received from server and sends to AudioTrack
     */
    override fun run() {

        Log.i("Onpa", "Called PlaybackThread")
        try {
            val socket = Socket(ipAddress, port)
            Log.d("Onpa-pt", socket.toString())
            if (!terminate) {
                val audioBuffer = ByteArray(minBufSize * 8)
                with(audioTrack) {
                    val audioData = socket.takeIf { it.isConnected }
                        ?.getInputStream()

                    play()

                    while (!terminate) {
                        write(audioBuffer, 0, audioData!!.read(audioBuffer, 0, minBufSize * 8))
                    }

                    pause(); flush(); release()

                    socket.takeIf { it.isConnected }?.close()
                }
            }
        } catch (e: Exception) {
            when (e) {
                is ConnectException, // Connection refused
                is IOException, // Any IO Error
                is SecurityException,
                    // When security manager blocks connection to
                    // given IP or port
                is UnsupportedEncodingException
                    // When the data received is not in required encoding
                -> {
                    terminate()
                    e.printStackTrace()
                }
                else -> throw e
            }
        }
    }
}