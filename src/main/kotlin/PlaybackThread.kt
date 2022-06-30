/*
 *
 * Copyright (C) 2021 supershadoe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package me.supershadoe.onpa

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.util.Log

import java.io.IOException
import java.io.InputStream
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
        private val ipAddress: String,
        private val port: Int,
        sampleRate: Int,
        stereo: Boolean,
        audioManager: AudioManager): Runnable {

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
        // Declare sock(Socket) and audioData(InputStream)
        var sock: Socket? = null
        var audioData: InputStream? = null
        val audioBuffer = ByteArray(minBufSize * 8)
        Log.i("Onpa", "Called")
        try {
            // Try to create a socket to the server with the given IP Address using the provided port
            sock = Socket(ipAddress, port)
            Log.d("Onpa-pt", sock.toString())
        } catch (e: ConnectException) {
            // Connection refused
            Log.d("Onpa-pt", ipAddress)
            terminate()
            e.printStackTrace()
        } catch (e: IOException) {
            // Error while creating a socket
            Log.d("Onpa-pt", ipAddress)
            terminate()
            e.printStackTrace()
        } catch (e: SecurityException) {
            /*
             * Exception thrown when a security manager(if it exists) denies permission to connect to
             * given IP Address and port
             */
            terminate()
            e.printStackTrace()
        }
        if (!terminate) {
            try {
                // Try to get audio stream from the server
                audioData = sock!!.getInputStream()
            } catch (e: UnsupportedEncodingException){
                // Data is some unknown encoding
                terminate()
                e.printStackTrace()
            } catch (e: IOException) {
                // Some I/O error
                terminate()
                e.printStackTrace()
            }
        }

        // Flush the audio buffer so that playback won't continue from previous run(Just a precaution)
        audioTrack.flush()
        // Start playback
        audioTrack.play()
        // A blocking loop which runs till the thread is terminated using terminate()
        while (!terminate) {
            try {
                audioTrack.write(audioBuffer, 0,
                        audioData!!.read(audioBuffer, 0, minBufSize * 8))
            } catch (e: IOException) {
                // Some I/O error
                e.printStackTrace()
            }
        }
        /*
         * Pause, flush and release is used instead of stop and release as stop doesn't stop playback
         * at once.
         * And flush is done to clear the buffer so that the app doesn't play leftover audio from this
         * time when the app is run the next time
         */
        audioTrack.pause()
        audioTrack.flush()
        audioTrack.release()
        /*
         * Closing the socket if it's still connected to prevent the app from making multiple connections
         * to the server
         */
        if (sock != null && sock.isConnected) {
            try {
                // Close the socket
                sock.close()
            } catch (e: IOException) {
                // Some I/O exception
                e.printStackTrace()
            }
        }
    }
}