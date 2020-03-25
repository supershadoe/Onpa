package com.supershadoe.onpa

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat

class Help : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Setting the view
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Displaying html
        findViewById<TextView>(R.id.help_content_title).text = HtmlCompat.fromHtml(getString(R.string.help_content_title), 0)
        findViewById<TextView>(R.id.help_content_body).text = HtmlCompat.fromHtml(getString(R.string.help_content_body), 0)
        findViewById<TextView>(R.id.help_content_license).text = HtmlCompat.fromHtml(getString(R.string.help_content_license), 0)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}