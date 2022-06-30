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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

/**
 * Settings Activity
 */
class Settings : AppCompatActivity() {

    /**
     * onCreate function: Creates the activity view
     * @param savedInstanceState(Bundle?) restores saved data(saved when app goes to background)
     *                                    of an activity from bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sets dark/light theme based on the preferences
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        // Loads the view from layout XML
        setContentView(R.layout.activity_settings)

        // Replaces settingsFrame with PlayFragment
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settingsFrame, SettingsFragment())
                .commit()
    }
}