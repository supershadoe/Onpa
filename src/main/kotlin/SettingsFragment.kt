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

import android.os.Bundle
import android.text.TextUtils

import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference

import com.google.android.material.snackbar.Snackbar

/**
 * Fragment which loads the settings from XML into the view
 */
class SettingsFragment : PreferenceFragmentCompat() {

    /**
     * onCreatePreferences function: Function which creates the settings from XML
     * @param savedInstanceState(Bundle?) restores saved data(saved when app goes to background)
     *                                    of an activity from bundle
     * @param rootKey(String?) the key(the thing which identifies each preference) of each preference
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load Preferences from root_preferences XML
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Set summary and onPreferenceChangeListener for ip_pref(IP Address of the computer)
        val ipPref = findPreference<EditTextPreference>("ip_pref")
        ipPref!!.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        ipPref.setOnPreferenceChangeListener { _, newValue ->
            newValue as String
            if(TextUtils.isEmpty(newValue)) {
                if (view != null) {
                    Snackbar.make(requireView(), getString(R.string.ip_pref) + " " + getString(R.string.editTextPref_empty), Snackbar.LENGTH_LONG)
                            .show()
                }
                false
            } else {
                Snackbar.make(requireView(), getString(R.string.pref_change_msg), Snackbar.LENGTH_LONG)
                        .show()
                true
            }
        }

        // Set summary and onPreferenceChangeListener for port_pref(The port to connect to)
        val portPref = findPreference<EditTextPreference>("port_pref")
        portPref!!.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        portPref.setOnPreferenceChangeListener { _, newValue ->
            newValue as String
            if(TextUtils.isEmpty(newValue)) {
                if (view != null) {
                    Snackbar.make(requireView(), getString(R.string.port_pref) + " " + getString(R.string.editTextPref_empty), Snackbar.LENGTH_LONG)
                            .show()
                }
                false
            } else {
                Snackbar.make(requireView(), getString(R.string.pref_change_msg), Snackbar.LENGTH_LONG)
                        .show()
                true
            }
        }

        /*
         * Set summary and onPreferenceChangeListener for sample_rate_pref(Sample rate of audio sent
         * by the server(computer)
         */
        val srPref = findPreference<EditTextPreference>("sample_rate_pref")
        srPref!!.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        srPref.setOnPreferenceChangeListener { _, newValue ->
            newValue as String
            if(TextUtils.isEmpty(newValue)) {
                if (view != null) {
                    Snackbar.make(requireView(), getString(R.string.sample_rate_pref) + " " + getString(R.string.editTextPref_empty), Snackbar.LENGTH_LONG)
                            .show()
                }
                false
            } else {
                Snackbar.make(requireView(), getString(R.string.pref_change_msg), Snackbar.LENGTH_LONG)
                        .show()
                true
            }
        }

        // Set summary and onPreferenceChangeListener for stereo_pref(Is the audio stereo or mono?)
        val stereoPref = findPreference<SwitchPreference>("stereo_pref")
        stereoPref!!.summary = if(stereoPref.isChecked)
            getString(R.string.stereo_pref_stereo)
        else
            getString(R.string.stereo_pref_mono)
        stereoPref.setOnPreferenceChangeListener { preference, newValue ->
            newValue as Boolean
            preference.summary = if(newValue)
                getString(R.string.stereo_pref_stereo)
            else
                getString(R.string.stereo_pref_mono)
            Snackbar.make(requireView(), getString(R.string.pref_change_msg), Snackbar.LENGTH_LONG)
                    .show()
            true
        }
    }

}