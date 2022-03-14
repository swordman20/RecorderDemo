package com.example.recorderdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.recorderlibrary.AudioUiActivity

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        findViewById<Button>(R.id.button7).setOnClickListener {
            startActivity(Intent(this, AudioUiActivity::class.java))
        }
    }
}