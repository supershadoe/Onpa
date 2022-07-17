/*
 * SPDX-FileCopyrightText: 2020-2022 supershadoe <supershadoe@proton.me>
 * SPDX-License-Identifier: Apache-2.0
 */

package me.supershadoe.onpa

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.*
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates
import android.media.AudioManager.OnAudioFocusChangeListener as AFCListener

class MediaService: Service() {

    inner class LocalBinder: Binder() {
        fun getService(): MediaService = this@MediaService
    }

    private val binder: Binder = LocalBinder()

    private var audioFocusChangeListener = AFCListener {
        when(it) {
            AudioManager.AUDIOFOCUS_GAIN ->
                audioTrack.setVolume(1.0F)
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
                audioTrack.setVolume(0.1F)
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ->
                onClick()
        }
    }
    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()
    private lateinit var audioManager: AudioManager
    private var audioSessionID by Delegates.notNull<Int>()
    private lateinit var audioTrack: AudioTrack
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var wifiLock: WifiManager.WifiLock
    private lateinit var notificationManager: NotificationManager

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    /**
     * To create a new audio track for usage
     */
    private fun buildAudioTrack(sampleRate: Int, stereo: Boolean) {
        val stereoParam =
            if(stereo) AudioFormat.CHANNEL_OUT_STEREO
            else AudioFormat.CHANNEL_OUT_MONO

        val minBufSize = AudioTrack.getMinBufferSize(
            sampleRate, stereoParam, AudioFormat.ENCODING_PCM_16BIT
        )

        val audioFormat: AudioFormat = AudioFormat.Builder()
            .setChannelMask(stereoParam)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .build()

        val atBuilder = AudioTrack.Builder()
            .setAudioAttributes(audioAttributes)
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(minBufSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .setSessionId(audioSessionID)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            atBuilder.setPerformanceMode(
                AudioTrack.PERFORMANCE_MODE_LOW_LATENCY
            )

        audioTrack = atBuilder.build()
    }

    /**
     * Creates an AudioFocusRequest for playing the audio
     * @param toFocus(Boolean) Flag to decide whether audioFocus should be requested or given up.
     * @return true if FocusReq granted else false
     */
    private fun createAudioFocusReq(toFocus: Boolean): Boolean {
        return audioManager.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setAcceptsDelayedFocusGain(false)
                    .setWillPauseWhenDucked(true)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .build()
                audioFocusRequest.let {
                    if (toFocus) requestAudioFocus(it)
                    else abandonAudioFocusRequest(it)
                }
            } else {
                @Suppress("DEPRECATION")
                if(toFocus) requestAudioFocus(
                    audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
                )
                else abandonAudioFocus(audioFocusChangeListener)
            }
        }
            .let { it == AudioManager.AUDIOFOCUS_REQUEST_GRANTED }
    }

    private fun initVars() {
        (getSystemService(AUDIO_SERVICE) as AudioManager).let {
            audioManager = it
            audioSessionID = it.generateAudioSessionId()
        }
        wakeLock = (getSystemService(POWER_SERVICE) as PowerManager).run{
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "onpa::wakeLock")
        }

        // Some random memory leak issue so gotta get system service from
        // applicationContext for devices with less than Android 7
        wifiLock = (applicationContext.getSystemService(WIFI_SERVICE) as WifiManager)
            .run{
                createWifiLock(
                    WifiManager.WIFI_MODE_FULL_HIGH_PERF, "onpa::wifiLock"
                )
            }

        notificationManager = getSystemService(NOTIFICATION_SERVICE)
                as NotificationManager
    }

    internal fun startPlayback() {
        buildAudioTrack(48000, true)
        createAudioFocusReq(true).takeIf { it }
            ?.run {
                
            }
    }

    internal fun stopPlayback() {
        createAudioFocusReq(false)
    }

    /**
     * Executed when the quick setting tile is created
     */
    override fun onCreate() {
        super.onCreate()
        initVars()

        val contentIntent = Intent(this, OnpaAct::class.java).let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    "test", "Testing", NotificationManager.IMPORTANCE_LOW
                )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = NotificationCompat.Builder(this, "test")
            .setStyle(MediaStyle())
            .setContentTitle("Onpa")
            .setContentText("onpa 123")
            .setContentIntent(contentIntent)
            .build()
        startForeground(123, notification)
    }

    private fun onClick(): Nothing = TODO("Not implemented")

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_REDELIVER_INTENT
    }
}