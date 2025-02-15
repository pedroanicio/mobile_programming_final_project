package com.example.trabalhofinalprogramacaomobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trabalhofinalprogramacaomobile.R
import com.example.trabalhofinalprogramacaomobile.model.Habito

class HabitosAdapter(
    private val onItemClick: (Habito) -> Unit, // Clique no item para editar
    private val onStartClick: (Habito) -> Unit // Clique no botão Start
) : RecyclerView.Adapter<HabitosAdapter.HabitoViewHolder>() {

    private val habitos = mutableListOf<Habito>()
    private val progressoDiario = mutableMapOf<Int, Int>() // Mapa para armazenar o tempo estudado por hábito

    fun updateHabitos(newHabitos: List<Habito>, progresso: Map<Int, Int>) {
        habitos.clear()
        habitos.addAll(newHabitos)
        progressoDiario.clear()
        progressoDiario.putAll(progresso)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habito, parent, false)
        return HabitoViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitoViewHolder, position: Int) {
        val habito = habitos[position]
        val tempoEstudado = progressoDiario[habito.id] ?: 0
        holder.bind(habito, tempoEstudado, onItemClick, onStartClick)
    }

    override fun getItemCount(): Int = habitos.size


    inner class HabitoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            habito: Habito,
            tempoEstudado: Int,
            onItemClick: (Habito) -> Unit,
            onStartClick: (Habito) -> Unit
        ) {
            // Configura os dados do hábito
            itemView.findViewById<TextView>(R.id.tvNomeHabito).text = habito.nome
            itemView.findViewById<TextView>(R.id.tvTempoMeta).text = "Meta: ${habito.tempoMeta} minutos"
            itemView.findViewById<TextView>(R.id.tvTempoEstudo).text = "Estudado hoje: $tempoEstudado minutos"

            // Configura o clique no item (para editar/excluir)
            itemView.setOnClickListener {
                onItemClick(habito)
            }

            // Configura o clique no botão Start
            itemView.findViewById<ImageButton>(R.id.btnStart).setOnClickListener {
                onStartClick(habito)
            }
        }
    }
}