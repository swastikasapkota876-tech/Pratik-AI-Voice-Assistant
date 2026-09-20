package com.pratik.aiassistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 48, 36, 36)
            setBackgroundColor(0xFF090909.toInt())
        }

        val title = TextView(this).apply {
            text = "PRATIK AI"
            textSize = 30f
            setTextColor(0xFFFFFFFF.toInt())
        }

        val subtitle = TextView(this).apply {
            text = "Emotional multilingual voice assistant"
            textSize = 16f
            setTextColor(0xFFBBBBBB.toInt())
        }

        status = TextView(this).apply {
            text = "Status: Ready"
            textSize = 16f
            setTextColor(0xFF66FF99.toInt())
            setPadding(0, 40, 0, 20)
        }

        val mic = Button(this).apply {
            text = "START ASSISTANT"
            setOnClickListener {
                requestPermissions()
                ContextCompat.startForegroundService(
                    this@MainActivity,
                    Intent(this@MainActivity, AssistantService::class.java)
                )
                status.text = "Status: Listening service started"
            }
        }

        val accessibility = Button(this).apply {
            text = "ENABLE PHONE CONTROL"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }

        val info = TextView(this).apply {
            text = """
                Commands:
                • "Mother lai call gara"
                • "Facebook khola"
                • "Back ja"
                • "Home ja"
                • "Who created you?"

                Safety:
                Important-looking files are NEVER silently deleted.
                The assistant asks for confirmation first.
            """.trimIndent()
            textSize = 15f
            setTextColor(0xFFDDDDDD.toInt())
            setPadding(0, 30, 0, 0)
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(status)
        layout.addView(mic)
        layout.addView(accessibility)
        layout.addView(info)
        setContentView(layout)
    }

    private fun requestPermissions() {
        val needed = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE
        ).filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (needed.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, needed.toTypedArray(), 100)
        }
    }
}
