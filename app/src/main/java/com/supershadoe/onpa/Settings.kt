package com.supershadoe.onpa

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar

class Settings : AppCompatActivity() {

    // Create settings activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Load preferences
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            // Default port preference
            // Find port preference
            val portDef = findPreference<EditTextPreference>("port_def")
            // Set summary
            portDef!!.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            // Set OCL
            portDef.setOnPreferenceChangeListener { _, newValue ->
                newValue as String
                if(TextUtils.isEmpty(newValue)) {
                    if (view != null) {
                        Snackbar.make(view!!, getString(R.string.port_empty_sBar), Snackbar.LENGTH_LONG)
                                .show()
                    }
                    false
                } else {
                    Snackbar.make(view!!, getString(R.string.port_change_sBar), Snackbar.LENGTH_LONG)
                            .show()
                    true
                }
            }

            // DayNight settings
            // find DayNight preference
            val dayNightPref = findPreference<ListPreference>("dayNight_pref")
            // Set OCL
            dayNightPref!!.setOnPreferenceChangeListener { _, newValue ->
                newValue as String
                AppCompatDelegate.setDefaultNightMode(newValue.toInt())
                true
            }
        }
    }

    // Return to previous activity on pressing back button
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}