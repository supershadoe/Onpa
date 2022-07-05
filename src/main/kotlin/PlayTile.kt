/*
 * SPDX-FileCopyrightText: 2020-2022 supershadoe <supershadoe@proton.me>
 * SPDX-License-Identifier: Apache-2.0
 */

package me.supershadoe.onpa

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener as AFCListener
import android.net.wifi.WifiManager
import android.os.Build
import android.os.PowerManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

/**
 * Main service which handles the audio playback and the quick setting tile
 *
 * Some of my thoughts:
 * Settled on quick setting tile as it makes life easier for both me and user.
 * Drawbacks are app can't be used by users who own Android devices with android version <7.0
 * And quick setting tile maybe useless for people who use this app once in a blue moon.
 */
class PlayTile: TileService() {

    // Variable storing the current audio focus state
    private var isAudioFocused = false
    private var audioFocusChangeListener = AFCListener {
        when(it) {
            AudioManager.AUDIOFOCUS_GAIN ->
                playbackThread.audioTrack.setVolume(1.0f)
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
                playbackThread.audioTrack.setVolume(0.1f)
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ->
                onClick()
        }
    }
    // Instance of PlaybackThread class
    private lateinit var playbackThread: PlaybackThread
    // Declaring audioManager, wakeLock and wifiLock to use them in future
    private lateinit var audioManager: AudioManager
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var wifiLock: WifiManager.WifiLock

    /**
     * manageAudioFocus function: Creates an AudioFocusRequest for playing the audio
     * @param toFocus(Boolean) Flag to decide whether audioFocus should be requested or given up.
     */
    private fun createAudioFocusReq(toFocus: Boolean) {
        audioManager.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
                val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setAcceptsDelayedFocusGain(false)
                    .setWillPauseWhenDucked(true)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .build()
                audioFocusRequest?.let {
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
            ?.takeIf { it == AudioManager.AUDIOFOCUS_REQUEST_GRANTED }
            ?.also { isAudioFocused = toFocus }
    }

    /**
     * Executed when the quick setting tile is created
     */
    override fun onCreate() {
        super.onCreate()
        audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        wakeLock = (applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager)
                .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "onpa::wakeLock")
        wifiLock = (applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
                .createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "onpa::wifiLock")
        qsTile?.takeIf {
            it.label == getString(R.string.qs_label)
        }?.apply {
            state = Tile.STATE_INACTIVE
            updateTile()
        }
    }

    /**
     * Executed when the quick setting tile is tapped.
     * (or when audio focus is lost)
     */
    override fun onClick() {
        super.onClick()
        @SuppressLint("WakelockTimeout")
        when(qsTile?.state) {
            Tile.STATE_INACTIVE -> {
                createAudioFocusReq(true)
                playbackThread = PlaybackThread(audioManager)
                Thread(playbackThread).start()

                wakeLock.acquire()
                wifiLock.acquire()

                with(qsTile!!) {
                    state = Tile.STATE_ACTIVE
                    label = playbackThread.ipAddress
                    updateTile()
                }
            }
            Tile.STATE_ACTIVE -> {
                createAudioFocusReq(false)
                playbackThread.terminate()

                if(wakeLock.isHeld) wakeLock.release()
                if(wifiLock.isHeld) wifiLock.release()

                with(qsTile!!) {
                    state = Tile.STATE_INACTIVE
                    label = getString(R.string.qs_label)
                    updateTile()
                }
            }
        }
    }
}