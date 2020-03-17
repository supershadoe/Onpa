package com.supershadoe.onpa

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class Onpa: AppCompatActivity() {

    // Initialize some variables for playback/recording
    private var playState = false
    private var playThread: Playback? = null
    private var recState = false
    private var recThread: Record? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Create activity
        super.onCreate(savedInstanceState)

        // Shared preferences
        val sharePref: SharedPreferences = getDefaultSharedPreferences(applicationContext)
        val dayNightPref: Int = sharePref.getString("dayNight_pref", "-1")!!.toInt()
        val port = sharePref.getString("port_def", "8000")!!

        // Set-up main activity
        AppCompatDelegate.setDefaultNightMode(dayNightPref)
        setContentView(R.layout.activity_main)
        val tBar = findViewById<Toolbar>(R.id.tBar)
        setSupportActionBar(tBar)
        val playCont = findViewById<FloatingActionButton>(R.id.Play)
        val recCont = findViewById<FloatingActionButton>(R.id.Record)

        // Check if all permissions in manifest are granted
        val perms = arrayOf(Manifest.permission.RECORD_AUDIO)
        ActivityCompat.requestPermissions(this, perms, 101)

        // Play button OCL
        playCont.setOnClickListener {
            if(!playState) {
                if (playThread != null) {
                    playThread!!.terminate()
                    playThread = null
                }

                val server = findViewById<EditText>(R.id.Ipaddr)
                if (TextUtils.isEmpty(server.text)) {
                    Snackbar.make(findViewById(R.id.constraintLayout), "Please fill in the IP Address.", Snackbar.LENGTH_LONG)
                            .setAction("FILL") { server.requestFocus() }.show()
                } else {
                    playState = true
                    playCont.setImageResource(R.drawable.stop)
                    val audioManager: AudioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    playThread = Playback(server.text.toString(), port, audioManager)
                    Thread(playThread).start()
                }
            } else {
                playState = false
                playCont.setImageResource(R.drawable.play)
                if (playThread != null) {
                    playThread!!.terminate()
                    playThread = null
                }
            }
        }

        // Mic button OCL
        recCont.setOnClickListener {
            if(!recState) {
                if (recThread != null) {
                    recThread!!.terminate()
                    recThread = null
                }

                val server = findViewById<EditText>(R.id.Ipaddr)

                if (TextUtils.isEmpty(server.text)) {
                    Snackbar.make(findViewById(R.id.constraintLayout), "Please fill out all the fields.", Snackbar.LENGTH_LONG)
                            .setAction("FILL") { server.requestFocus() }.show()
                } else {
                    recState = true
                    recCont.setImageResource(R.drawable.stop)
                    recThread = Record(server.text.toString(), port)
                    Thread(recThread).start()
                }
            } else {
                recState = false
                recCont.setImageResource(R.drawable.ic_mic_24px)
                if (recThread != null) {
                    recThread!!.terminate()
                    recThread = null
                }
            }
        }
    }

    // Action to perform when app is resumed from paused state(went to home or other app)
    override fun onResume() {
        super.onResume()
        //getDefaultSharedPreferences(applicationContext).registerOnSharedPreferenceChangeListener(sharePrefListener)
    }

    //Action to perform when app is paused
    override fun onPause() {
        super.onPause()
        //getDefaultSharedPreferences(applicationContext).unregisterOnSharedPreferenceChangeListener(sharePrefListener)
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