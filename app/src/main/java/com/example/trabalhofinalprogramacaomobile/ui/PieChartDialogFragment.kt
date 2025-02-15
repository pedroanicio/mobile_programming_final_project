package com.example.trabalhofinalprogramacaomobile.ui

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trabalhofinalprogramacaomobile.R
import com.example.trabalhofinalprogramacaomobile.repository.HabitoRepository
import com.example.trabalhofinalprogramacaomobile.repository.ProgressoRepository
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PieChartDialogFragment : DialogFragment() {

    private lateinit var pieChart: PieChart
    private lateinit var habitoRepository: HabitoRepository
    private lateinit var progressoRepository: ProgressoRepository

    private var dataSelecionada: String? = null // Variável para armazenar a data selecionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataSelecionada = arguments?.getString("DATA_SELECIONADA") // Recupera a data dos argumentos
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_pie_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        habitoRepository = HabitoRepository(requireContext())
        progressoRepository = ProgressoRepository(requireContext())

        pieChart = view.findViewById(R.id.pieChartCompleto)
        configurarPieChart()
        updatePieChart()
    }

    private fun configurarPieChart() {
        pieChart.description.isEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.legend.isEnabled = true
        pieChart.setHoleColor(android.graphics.Color.TRANSPARENT)
        pieChart.setTransparentCircleColor(android.graphics.Color.TRANSPARENT)
        pieChart.setEntryLabelColor(android.graphics.Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)
    }

    private fun updatePieChart() {
        val habitos = habitoRepository.getHabitos()
        val dataParaBuscar = dataSelecionada ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val progressoDiarioMap = mutableMapOf<Int, Int>()

        habitos.forEach { habito ->
            val progressoDiario = progressoRepository.getProgressoDiario(habito.id ?: -1, dataParaBuscar)
            progressoDiarioMap[habito.id!!] = progressoDiario?.tempoEstudo ?: 0
        }

        val entries = habitos.map { habito ->
            PieEntry(progressoDiarioMap[habito.id!!]?.toFloat() ?: 0f, habito.nome)
        }

        val dataSet = PieDataSet(entries, "Progresso de Hábitos")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = android.graphics.Color.BLACK

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate()
    }

    companion object {
        fun newInstance(dataSelecionada: String): PieChartDialogFragment {
            val fragment = PieChartDialogFragment()
            val args = Bundle()
            args.putString("DATA_SELECIONADA", dataSelecionada)
            fragment.arguments = args
            return fragment
        }
    }
}
