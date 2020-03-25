package com.supershadoe.onpa

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.PowerManager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class Onpa: AppCompatActivity() {

    @SuppressLint("WakelockTimeout")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Create activity
        super.onCreate(savedInstanceState)

        // Set-up main activity
        AppCompatDelegate.setDefaultNightMode(getDefaultSharedPreferences(applicationContext)
                .getString("dayNight_pref", "-1")!!.toInt())
        setContentView(R.layout.activity_main)
        val tBar = findViewById<Toolbar>(R.id.tBar)
        setSupportActionBar(tBar)

        // Initialize some variables for playback
        var playState = false
        var playThread: Playback? = null
        val playBut = findViewById<FloatingActionButton>(R.id.Play)

        // Declare wakelock
        val powerManager: PowerManager = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock: PowerManager.WakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Onpa::Wakelock")

        // Play button OCL
        playBut.setOnClickListener {
            if(!playState) {
                if (playThread != null) {
                    playThread!!.terminate()
                    playThread = null
                }

                val server = findViewById<EditText>(R.id.Ipaddr)
                val port: Int = getDefaultSharedPreferences(applicationContext).getString("port_def", "8000")!!.toInt()

                if (TextUtils.isEmpty(server.text)) {
                    Snackbar.make(findViewById(R.id.constraintLayout), getString(R.string.ip_sBar), Snackbar.LENGTH_LONG)
                            .setAction("FILL") { server.requestFocus() }.show()
                } else {
                    playState = true
                    playBut.setImageResource(R.drawable.stop)
                    val audioManager: AudioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    playThread = Playback(server.text.toString(), port, audioManager)
                    Thread(playThread).start()
                    wakeLock.acquire()
                }
            } else {
                playState = false
                playBut.setImageResource(R.drawable.play)
                if (playThread != null) {
                    playThread!!.terminate()
                    playThread = null
                    wakeLock.release()
                }
            }
        }
    }

    // Inflate menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Actions to perform when menu item is selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.settings) {
            startActivity(Intent(applicationContext, Settings::class.java))
            return true
        } else if(item.itemId == R.id.help) {
            startActivity(Intent(applicationContext, Help::class.java))
            return true
        }
        return false
    }

}