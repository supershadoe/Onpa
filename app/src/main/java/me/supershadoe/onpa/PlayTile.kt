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

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.PowerManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.preference.PreferenceManager

/**
 * Main service which handles the audio playback and the quick setting tile
 *
 * Some of my thoughts:
 * Settled on quick setting tile as it makes life easier for both me and user.
 * Drawbacks are app can't be used by users who own Android devices with android version <7.0
 * And quick setting tile maybe useless for people who use this app once in a blue moon.
 */
class PlayTile: TileService(), AudioManager.OnAudioFocusChangeListener {

    // Declaration of various variables
    // enum class to show different states of audio focus
    enum class AudioFocus {
        NoFocusNoDuck,
        NoFocusCanDuck,
        Focused
    }
    // Variable storing the current audio focus state
    private var audioFocus = AudioFocus.NoFocusNoDuck
    // Instance of PlaybackThread class
    private var playbackThread: PlaybackThread? = null
    // Declaring audioManager, wakeLock and wifiLock to use them in future
    private lateinit var audioManager: AudioManager
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var wifiLock: WifiManager.WifiLock

    /**
     * manageAudioFocus function: Used to create a AudioFocusRequest instance to get AudioFocus for
     *                            app and also to declare onAudioFocusChange() function as
     *                            AudioFocusChangeListener
     * @param getOrGiveUp(Boolean) AudioFocus is gained or given up if the value is true or false respectively
     */
    private fun manageAudioFocus(getOrGiveUp: Boolean) {
        var audioFocusRequest: AudioFocusRequest? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setAcceptsDelayedFocusGain(false)
                    .setWillPauseWhenDucked(true)
                    .setOnAudioFocusChangeListener(this)
                    .build()
        }
        if (getOrGiveUp)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Gain AudioFocus(For Android version >= 8.0 (Oreo)
                if (audioFocus != AudioFocus.Focused && audioFocusRequest != null &&
                        AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.requestAudioFocus(audioFocusRequest)) {
                    audioFocus = AudioFocus.Focused
                }
            } else {
                // Gain AudioFocus(For Android version < 8.0 (Oreo)
                if (audioFocus != AudioFocus.Focused &&
                        AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)) {
                    audioFocus = AudioFocus.Focused
                }
            }
        else
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Give up AudioFocus(For Android version >= 8.0 (Oreo)
                if (audioFocus == AudioFocus.Focused && audioFocusRequest != null &&
                        AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocusRequest(audioFocusRequest)) {
                    audioFocus = AudioFocus.NoFocusNoDuck
                }
            } else {
                // Give up AudioFocus(For Android version < 8.0 (Oreo)
                if (audioFocus == AudioFocus.Focused &&
                        AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocus(this)) {
                    audioFocus = AudioFocus.NoFocusNoDuck
                }
            }
    }

    /**
     * processPlayRequest function: Starts playback when the quick settings tile is tapped
     */
    @SuppressLint("WakelockTimeout")
    private fun processPlayRequest() {
        // Get preference values to set up stream
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val ipAddress = sharedPreferences.getString("ip_pref", "0.0.0.0")!!
        val port = sharedPreferences.getString("port_def", "8000")!!.toInt()
        val sampleRate = sharedPreferences.getString("sample_rate_pref", "48000")!!.toInt()
        val stereo = sharedPreferences.getBoolean("stereo_pref", true)
        // Gain audio focus
        manageAudioFocus(getOrGiveUp = true)
        // Initialize playback thread
        playbackThread = PlaybackThread(ipAddress, port, sampleRate, stereo, audioManager)
        // Start the thread
        Thread(playbackThread).start()
        // Acquire wake lock and wifi lock
        wakeLock.acquire()
        wifiLock.acquire()
        // Set volume according to AudioFocus
        setVolume(audioFocus)
        // Set quick setting tile state to active and update tile
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.label = ipAddress
        qsTile.updateTile()
    }

    /**
     * processStopRequest function: Stops playback when the quick settings tile is tapped
     */
    private fun processStopRequest() {
        // Give up audio focus
        manageAudioFocus(getOrGiveUp = false)
        // Terminate playback thread
        playbackThread?.terminate()
        playbackThread = null
        // Release wake lock and wifi lock if they are held
        if(wakeLock.isHeld) wakeLock.release()
        if(wifiLock.isHeld) wifiLock.release()
        // Set quick setting tile state to inactive and update tile
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.label = getString(R.string.qs_label)
        qsTile.updateTile()
    }

    /**
     * onCreate function: Executed when the quick setting tile is created
     */
    override fun onCreate() {
        super.onCreate()
        // Initializing audioManager, wakeLock and wifiLock
        audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        wakeLock = (applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager)
                .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "onpa::wakeLock")
        wifiLock = (applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
                .createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "onpa::wifiLock")
    }

    /**
     * onStartListening function: Executed when the quick setting tile comes in view and is ready to
     *                            listen for events(touch, etc.)
     */
    override fun onStartListening() {
        super.onStartListening()
        if(qsTile != null && qsTile.label == getString(R.string.qs_label)) {
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.updateTile()
        }
    }

    /**
     * onClick function: Executed when the quick setting tile is executed.
     * App decides whether to play/pause playback according to the state of the quick setting tile at that moment.
     */
    override fun onClick() {
        super.onClick()
        when(qsTile.state) {
            Tile.STATE_INACTIVE -> processPlayRequest()
            Tile.STATE_ACTIVE -> processStopRequest()
        }
    }

    /**
     * setVolume function: Sets the volume of audio track according to AudioFocus
     * @param audioFocus(AudioFocus) Tells what is the current AudioFocus status
     */
    private fun setVolume(audioFocus: AudioFocus) {
        when (audioFocus) {
            AudioFocus.NoFocusNoDuck -> processStopRequest()
            AudioFocus.NoFocusCanDuck -> playbackThread!!.audioTrack.setVolume(0.1f)
            else -> playbackThread!!.audioTrack.setVolume(1.0f)
        }
    }

    /**
     * onAudioFocusChange function: AudioFocusChangeListener which executes setVolume whenever
     *                              AudioFocus changes to set volume appropriate for the situation
     * @param focusChange(Int) The changed AudioFocus
     */
    override fun onAudioFocusChange(focusChange: Int) {
        when(focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> setVolume(AudioFocus.Focused)
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> setVolume(AudioFocus.NoFocusCanDuck)
            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ->
                setVolume(AudioFocus.NoFocusNoDuck)
        }
    }
}