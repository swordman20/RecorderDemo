package com.example.recorderdemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.Chronometer
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.io.IOException

private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class MainActivity : AppCompatActivity() {
    private var fileName: String = ""
    private var recordButton: RecordButton? = null
    private var recorder: MediaRecorder? = null

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var playButton: PlayButton? = null
    private var player: MediaPlayer? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
            start()
        }
    }

    private var mPause = true

    @RequiresApi(Build.VERSION_CODES.N)
    private fun audioPause(btn: Button) {
//        recorder?.apply {
//            if (mPause) {
//                pause()
//            }
//            resume()
//        }
        btn.text = when (mPause) {
            true -> "恢复录音"
            false -> "暂停录音"
        }
        if (mPause) recorder?.pause() else recorder?.resume()
        mPause = !mPause
//        mStartPlaying = !mStartPlaying
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    @SuppressLint("AppCompatCustomView")
    internal inner class RecordButton(ctx: Context) : Button(ctx) {

        var mStartRecording = true

        var clicker: OnClickListener = OnClickListener {
            onRecord(mStartRecording)
            text = when (mStartRecording) {
                true -> "停止录音"
                false -> "开始录音"
            }
            mStartRecording = !mStartRecording
        }

        init {
            text = "开始录音"
            setOnClickListener(clicker)
        }
    }

    @SuppressLint("AppCompatCustomView")
    internal inner class PlayButton(ctx: Context) : Button(ctx) {
        var mStartPlaying = true
        var clicker: OnClickListener = OnClickListener {
            onPlay(mStartPlaying)
            text = when (mStartPlaying) {
                true -> "停止播放"
                false -> "开始播放"
            }
            mStartPlaying = !mStartPlaying
        }

        init {
            text = "开始播放"
            setOnClickListener(clicker)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Record to the external cache directory for visibility
        fileName = "${externalCacheDir?.absolutePath}/${System.currentTimeMillis()}.mp3"

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

//        recordButton = RecordButton(this)
//        playButton = PlayButton(this)
//        val ll = LinearLayout(this).apply {
//            addView(
//                recordButton,
//                LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    0f
//                )
//            )
//            addView(
//                playButton,
//                LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    0f
//                )
//            )
//        }
//        setContentView(ll)

        var mStartRecording = true
        val startBtn = findViewById<Button>(R.id.button)
        startBtn.setOnClickListener {
            onRecord(mStartRecording)
            (it as Button).text = when (mStartRecording) {
                true -> "停止录音"
                false -> "开始录音"
            }
            mStartRecording = !mStartRecording
        }
        val stopBtn = findViewById<Button>(R.id.button2)
        var mStartPlaying = true
        stopBtn.setOnClickListener {
            onPlay(mStartPlaying)
            (it as Button).text = when (mStartPlaying) {
                true -> "停止播放"
                false -> "开始播放"

            }
            mStartPlaying = !mStartPlaying
        }
        val chronometer = findViewById<Chronometer>(R.id.chronometer)
        chronometer.text = "00:00:00"
        chronometer.format = "00:%s"
        findViewById<Button>(R.id.button3).setOnClickListener {
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()
        }

        var mRecordTime = 0L
        var isStop = true
        findViewById<Button>(R.id.button6).setOnClickListener {
            if (isStop) {
                mRecordTime = SystemClock.elapsedRealtime()
                (it as Button).text = "恢复计时"
                chronometer.stop()
            } else {
                (it as Button).text = "暂停计时"
                chronometer.base = chronometer.base + (SystemClock.elapsedRealtime() - mRecordTime)
                chronometer.start()

            }
            isStop = !isStop

        }
        findViewById<Button>(R.id.button4).setOnClickListener {
            chronometer.stop()
            chronometer.text = "00:00:00"
            isStop = true
        }


        findViewById<Button>(R.id.button5).setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                audioPause(it as Button)
            }
        }

    }

}