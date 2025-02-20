package com.example.trabalhofinalprogramacaomobile.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Chronometer
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.room.util.copy
import com.example.trabalhofinalprogramacaomobile.R
import com.example.trabalhofinalprogramacaomobile.database.DatabaseHelper
import com.example.trabalhofinalprogramacaomobile.model.ProgressoDiario
import com.example.trabalhofinalprogramacaomobile.repository.HabitoRepository
import com.example.trabalhofinalprogramacaomobile.repository.ProgressoRepository

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
    private lateinit var tvModo: TextView

    private lateinit var spinnerHabitos: Spinner
    private lateinit var getHabitos: HabitoRepository
    private lateinit var progressoRepository: ProgressoRepository
    private var habitoSelecionado: String? = null
    private var tempoEstudoDiario = mutableMapOf<String, Long>()

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro)

        chronometer = findViewById(R.id.chronometer)
        btnStart = findViewById(R.id.btnStart)
        btnPause = findViewById(R.id.btnPause)
        btnReset = findViewById(R.id.btnReset)
        edtTempoFoco = findViewById(R.id.edtTempoFoco)
        edtTempoPausa = findViewById(R.id.edtTempoPausa)
        tvModo = findViewById(R.id.tvModo)

        getHabitos = HabitoRepository(this)
        progressoRepository = ProgressoRepository(this)


        spinnerHabitos = findViewById(R.id.spinnerHabitos)

        carregarHabitos()

        btnStart.setOnClickListener { startTimer() }
        btnPause.setOnClickListener { pauseTimer() }
        btnReset.setOnClickListener { resetTimer() }
    }

    private fun carregarHabitos(){
        val listaHabitos = getHabitos.getHabitos()

        if (listaHabitos.isEmpty()) {
            Toast.makeText(this, "Nenhum hábito registrado.", Toast.LENGTH_SHORT).show()
            return
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaHabitos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerHabitos.adapter = adapter

        spinnerHabitos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                habitoSelecionado = listaHabitos[position].nome
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                habitoSelecionado = null
            }
        }
    }

    private fun startTimer() {
        if (!verificarPermissaoModoNaoPerturbe()) {
            solicitarPermissaoModoNaoPerturbe()
            Toast.makeText(this, "Por favor, conceda permissão para ativar o modo Não Perturbe.", Toast.LENGTH_LONG).show()
        } else {
            naoPerturbe(true)
        }

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
                    habitoSelecionado?.let { habitoNome ->
                        val habitoId = getHabitos.getIdDoHabito(habitoNome) // Você precisa implementar isso no repositório.
                        val dataAtual = obterDataAtual() // Função que retorna a data de hoje no formato adequado.

                        val progressoExistente = progressoRepository.getProgressoDiario(habitoId, dataAtual)

                        if (progressoExistente != null) {
                            // Atualiza o tempo total estudado
                            val novoTempo = progressoExistente.tempoEstudo + (tempoFoco / 1000 / 60).toInt()
                            val progressoAtualizado = progressoExistente.copy(tempoEstudo = novoTempo)
                            progressoRepository.atualizarProgresso(progressoAtualizado)
                        } else {
                            // Insere um novo registro
                            val novoProgresso = ProgressoDiario(0, habitoId, dataAtual, (tempoFoco / 1000 / 60).toInt())
                            progressoRepository.adicionarProgressso(novoProgresso)
                        }

                        Toast.makeText(this@PomodoroActivity, "Registrado ${tempoFoco / 1000 / 60} min para $habitoNome", Toast.LENGTH_SHORT).show()
                    }

                    // Fim do tempo de foco, iniciar pausa
                    isFoco = false
                    tvModo.text = "Modo: Pausa"
                    startTimer()
                    mostrarNotificacao("Hora da pausa!", "Descanse um pouco.")
                    Toast.makeText(this@PomodoroActivity, "Hora da pausa!", Toast.LENGTH_SHORT).show()
                } else {
                    // Fim da pausa, reiniciar foco
                    mostrarNotificacao("Hora do foco.", "Hora de estudar.")
                    isFoco = true
                    tvModo.text = "Modo: Foco"
                    startTimer()
                    Toast.makeText(this@PomodoroActivity, "Volte ao foco!", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
    private fun obterDataAtual(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        chronometer.text = "00:00"
        isFoco = true
        tvModo.text = "Modo: Foco"
        naoPerturbe(false)
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

    private fun naoPerturbe(ativar: Boolean) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ativar) {
                // Ativa o modo Do Not Disturb
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
            } else {
                // Desativa o modo Do Not Disturb (volta ao normal)
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
        }
    }

    private fun verificarPermissaoModoNaoPerturbe(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.isNotificationPolicyAccessGranted
        } else {
            true
        }
    }

    private fun solicitarPermissaoModoNaoPerturbe() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }
    }


}