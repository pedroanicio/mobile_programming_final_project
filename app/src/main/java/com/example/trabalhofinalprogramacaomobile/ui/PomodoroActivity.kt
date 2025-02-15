package com.example.trabalhofinalprogramacaomobile.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.Chronometer
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.trabalhofinalprogramacaomobile.R

class PomodoroActivity : AppCompatActivity() {

    private lateinit var chronometer: Chronometer
    private lateinit var btnStart: Button
    private lateinit var btnPause: Button
    private lateinit var btnReset: Button

    private lateinit var edtTempoFoco: EditText
    private lateinit var edtTempoPausa: EditText

    private var tempoFoco: Long = 25 * 60 * 1000 // 25 minutos em milissegundos
    private var tempoPausa: Long = 5 * 60 * 1000 // 5 minutos em milissegundos
    private var countDownTimer: CountDownTimer? = null
    private var isFoco: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro)

        chronometer = findViewById(R.id.chronometer)
        btnStart = findViewById(R.id.btnStart)
        btnPause = findViewById(R.id.btnPause)
        btnReset = findViewById(R.id.btnReset)
        edtTempoFoco = findViewById(R.id.edtTempoFoco)
        edtTempoPausa = findViewById(R.id.edtTempoPausa)

        btnStart.setOnClickListener { startTimer() }
        btnPause.setOnClickListener { pauseTimer() }
        btnReset.setOnClickListener { resetTimer() }
    }


    private fun startTimer() {
        val tempoFocoInput = edtTempoFoco.text.toString().toLongOrNull()
        val tempoPausaInput = edtTempoPausa.text.toString().toLongOrNull()

        if (tempoFocoInput != null && tempoPausaInput != null) {
            tempoFoco = tempoFocoInput * 60 * 1000
            tempoPausa = tempoPausaInput * 60 * 1000
        }

        if (countDownTimer != null) {
            countDownTimer?.cancel()
        }

        countDownTimer = object : CountDownTimer(if (isFoco) tempoFoco else tempoPausa, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutos = millisUntilFinished / 1000 / 60
                val segundos = millisUntilFinished / 1000 % 60
                chronometer.text = String.format("%02d:%02d", minutos, segundos)
            }

            override fun onFinish() {
                if (isFoco) {
                    // Fim do tempo de foco, iniciar pausa
                    isFoco = false
                    startTimer()
                    mostrarNotificacao("Hora da pausa!", "Descanse um pouco.")
                    Toast.makeText(this@PomodoroActivity, "Hora da pausa!", Toast.LENGTH_SHORT).show()
                } else {
                    // Fim da pausa, reiniciar foco
                    mostrarNotificacao("Hora do foco.", "Hora de estudar.")
                    isFoco = true
                    startTimer()
                    Toast.makeText(this@PomodoroActivity, "Volte ao foco!", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        chronometer.text = "00:00"
        isFoco = true
    }

    private fun mostrarNotificacao(titulo: String, mensagem: String){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "pomodoro_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId, "Pomodoro", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        notificationManager.notify(1, notification)
    }

}