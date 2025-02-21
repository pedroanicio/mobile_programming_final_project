package com.example.trabalhofinalprogramacaomobile.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalhofinalprogramacaomobile.R
import com.example.trabalhofinalprogramacaomobile.repository.HabitoRepository
import com.example.trabalhofinalprogramacaomobile.adapters.HabitosAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.trabalhofinalprogramacaomobile.model.Habito
import com.example.trabalhofinalprogramacaomobile.model.ProgressoDiario
import com.example.trabalhofinalprogramacaomobile.repository.ProgressoRepository
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HabitosActivity : AppCompatActivity(),
    AddHabitoDialogFragment.HabitoListener,
    EditHabitoDialogFragment.HabitoListener {


    private lateinit var habitoRepository: HabitoRepository
    private lateinit var progressoRepository: ProgressoRepository
    private lateinit var adapter: HabitosAdapter
    private lateinit var recyclerHabitos: RecyclerView
    private lateinit var fabAddHabito: FloatingActionButton
    private lateinit var barChart: BarChart
    private lateinit var pieChartMinimalista: PieChart

    private lateinit var btnSelecionarData: Button
    private lateinit var tvDataAtual: TextView

    private lateinit var btnPomodoro: ImageButton


    private var temporizador: CountDownTimer? = null
    private var habitoAtual: Habito? = null
    private var progressoAtual: ProgressoDiario? = null
    private var dataAtual: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    private var isPausado: Boolean = false // Estado de pausa

    // Mapa para armazenar o tempo decorrido de cada hábito
    private val tempoDecorridoMap = mutableMapOf<Int, Long>()

    // Mapa para armazenar o progresso de todos os hábitos
    private val progressoDiarioMap = mutableMapOf<Int, Int>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habitos)

        recyclerHabitos = findViewById(R.id.recyclerHabitos)
        fabAddHabito = findViewById(R.id.fabAddHabito)
        barChart = findViewById(R.id.barChartFrequencia)
        pieChartMinimalista = findViewById(R.id.pieChartMinimalista)
        btnSelecionarData = findViewById(R.id.btnSelecionarData)
        tvDataAtual = findViewById(R.id.tvDataAtual)
        btnPomodoro = findViewById(R.id.pomodoro)



        habitoRepository = HabitoRepository( this)
        progressoRepository = ProgressoRepository(this)

        adapter = HabitosAdapter(
            onItemClick = { habito ->
                val dialog = EditHabitoDialogFragment(habito)
                dialog.show(supportFragmentManager, "EditHabitoDialog")
                atualizarGrafico()
                setupPieChartMinimalista()
            },
            onStartClick = { habito ->
                val formatoData = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val hoje = formatoData.format(Date())

                if (dataAtual < hoje) {
                    // Bloqueia a adição de tempo em datas passadas
                    Toast.makeText(this, "Não é possível adicionar tempo a dias anteriores!", Toast.LENGTH_SHORT).show()
                    return@HabitosAdapter
                }

                if (habitoAtual == habito && !isPausado) {
                    pausarTemporizador()
                    atualizarGrafico()
                    setupPieChartMinimalista()
                } else {
                    iniciarTemporizador(habito)
                    atualizarGrafico()
                    setupPieChartMinimalista()
                }
                atualizarGrafico()
                setupPieChartMinimalista()
            }
        )
        atualizarGrafico()
        setupPieChartMinimalista()

        recyclerHabitos.adapter = adapter
        recyclerHabitos.layoutManager = LinearLayoutManager(this)

        configurarGrafico()
        loadHabitos()

        fabAddHabito.setOnClickListener {
            val dialog = AddHabitoDialogFragment()
            dialog.show(supportFragmentManager, "AddHabitoDialog")
            atualizarGrafico()
            setupPieChartMinimalista()
        }
        atualizarGrafico()
        setupPieChartMinimalista()

        pieChartMinimalista.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val dialog = PieChartDialogFragment.newInstance(dataAtual)
                dialog.show(supportFragmentManager, "PieChartDialog")
            }
            true
        }
        // configurar botão de seleção de data
        btnSelecionarData.setOnClickListener {
            mostrarSeletorData()
        }

        btnPomodoro.setOnClickListener {
            val intent = Intent(this, PomodoroActivity::class.java)
            startActivity(intent)
        }
        atualizarDataAtual()
    }
    private fun atualizarDataAtual() {
        val formatoExibicao = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        tvDataAtual.text = "Data: ${formatoExibicao.format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dataAtual))}"
    }

    private fun mostrarSeletorData(){
        val calendario = Calendar.getInstance()
        val ano = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, anoSelecionado, mesSelecionado, diaSelecionado ->
                // Atualiza a data atual e recarrega os dados
                dataAtual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Calendar.getInstance().apply {
                        set(anoSelecionado, mesSelecionado, diaSelecionado)
                    }.time)
                atualizarDataAtual()
                loadHabitos()
                atualizarGrafico()
                setupPieChartMinimalista()
            },
            ano,
            mes,
            dia
        )
        datePickerDialog.show()
    }

    private fun configurarGrafico() {
        barChart.description.isEnabled = false
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
    }

    private fun iniciarTemporizador(habito: Habito) {
        if (habitoAtual != habito) {
            pararTemporizador()
        }

        habitoAtual = habito
        progressoAtual = progressoRepository.getProgressoDiario(habito.id ?: -1, dataAtual)
            ?: ProgressoDiario(habitoId = habito.id ?: -1, data = dataAtual, tempoEstudo = 0)

        val tempoDecorrido = tempoDecorridoMap[habito.id!!] ?: 0

        temporizador = object : CountDownTimer(Long.MAX_VALUE - tempoDecorrido, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tempoDecorridoMap[habito.id!!] = (tempoDecorridoMap[habito.id!!] ?: 0) + 1000
                progressoAtual?.tempoEstudo = (tempoDecorridoMap[habito.id!!]!! / 1000).toInt()
                progressoDiarioMap[habito.id!!] = progressoAtual?.tempoEstudo ?: 0
                adapter.updateHabitos(habitoRepository.getHabitos(), progressoDiarioMap)
                runOnUiThread {
                    atualizarGrafico()
                    setupPieChartMinimalista()
                }
            }

            override fun onFinish() {}
        }.start()

        isPausado = false

        runOnUiThread {
            atualizarGrafico()
            setupPieChartMinimalista()
        }
    }

    private fun pausarTemporizador() {
        temporizador?.cancel()
        isPausado = true
        
        progressoAtual?.let { progresso ->
            if (progresso.id == null) {
                progressoRepository.adicionarProgressso(progresso)
            } else {
                progressoRepository.atualizarProgresso(progresso)
            }
        }
        atualizarGrafico()
        setupPieChartMinimalista()
    }

    private fun pararTemporizador() {
        temporizador?.cancel()
        progressoAtual?.let { progresso ->
            if (progresso.id == null) {
                progressoRepository.adicionarProgressso(progresso)
            } else {
                progressoRepository.atualizarProgresso(progresso)
            }
        }
        habitoAtual = null
        progressoAtual = null
        isPausado = false

        Handler(Looper.getMainLooper()).post {
            atualizarGrafico()
            setupPieChartMinimalista()
            barChart.invalidate()
            pieChartMinimalista.invalidate()
        }
    }

    private fun loadHabitos() {
        val habitos = habitoRepository.getHabitos()
        progressoDiarioMap.clear()
        tempoDecorridoMap.clear()
        habitos.forEach { habito ->
            val progressoDiario = progressoRepository.getProgressoDiario(habito.id ?: -1, dataAtual)
            progressoDiarioMap[habito.id!!] = progressoDiario?.tempoEstudo ?: 0
            tempoDecorridoMap[habito.id!!] = (progressoDiario?.tempoEstudo ?: 0) * 1000L
        }
        adapter.updateHabitos(habitos, progressoDiarioMap)
        atualizarGrafico()
        setupPieChartMinimalista()
    }
    private fun atualizarGrafico() {
        val calendario = Calendar.getInstance()
        val formatoData = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val diasSemana = mutableListOf<String>()
        val progressoPorDia = mutableMapOf<String, Float>()

        // Obter os últimos 7 dias e armazenar no mapa
        for (i in 0..6) {
            val data = formatoData.format(calendario.time)
            diasSemana.add(data)
            progressoPorDia[data] = 0f // Inicializa com 0 caso não tenha progresso registrado
            calendario.add(Calendar.DAY_OF_YEAR, -1)
        }

        // Buscar progresso do banco e somar por dia
        val habitos = habitoRepository.getHabitos()
        for (habito in habitos) {
            for (data in diasSemana) {
                val progressoDiario = progressoRepository.getProgressoDiario(habito.id ?: -1, data)
                progressoPorDia[data] = progressoPorDia.getOrDefault(data, 0f) + (progressoDiario?.tempoEstudo ?: 0).toFloat()
            }
        }

        // Criar entradas do gráfico
        val entries = diasSemana.reversed().mapIndexed { index, data ->
            BarEntry(index.toFloat(), progressoPorDia[data] ?: 0f)
        }

        val dataSet = BarDataSet(entries, "Tempo de Estudo (min)")
        dataSet.color = ColorTemplate.MATERIAL_COLORS[0]

        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.invalidate() // Força a renderização do gráfico

        // Configurar eixo X com os dias da semana
        val diasAbreviados = diasSemana.reversed().map { data ->
            val dataFormatada = formatoData.parse(data)
            SimpleDateFormat("E", Locale.getDefault()).format(dataFormatada) // Exibe "Seg", "Ter", etc.
        }

        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(diasAbreviados)
        barChart.xAxis.labelCount = 7
        barChart.xAxis.granularity = 1f

        barChart.invalidate()
    }

    private fun setupPieChartMinimalista() {
        val habitos = habitoRepository.getHabitos()
        val pieEntries = habitos.map { habito ->
            val progresso = progressoRepository.getProgressoDiario(habito.id ?: -1, dataAtual)
            PieEntry(progresso?.tempoEstudo?.toFloat() ?: 0f, "")
        }

        val dataSet = PieDataSet(pieEntries, "")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val data = PieData(dataSet)

        pieChartMinimalista.data = data
        pieChartMinimalista.description.isEnabled = false
        pieChartMinimalista.legend.isEnabled = false
        pieChartMinimalista.invalidate()
    }

    override fun onPause() {
        super.onPause()
        pararTemporizador()
    }

    override fun onHabitoAdicionado(habito: Habito) {
        loadHabitos()
        atualizarGrafico()
        setupPieChartMinimalista()
    }

    override fun onHabitoEditado() {
        loadHabitos()
        atualizarGrafico()
        setupPieChartMinimalista()
    }
    override fun onResume() {
        super.onResume()
        loadHabitos() // Recarrega os hábitos e atualiza a interface
        atualizarGrafico() // Atualiza o gráfico
        setupPieChartMinimalista() // Atualiza o gráfico de pizza minimalista
    }
}
