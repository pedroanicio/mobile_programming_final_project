package com.example.trabalhofinalprogramacaomobile.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.trabalhofinalprogramacaomobile.R
import com.example.trabalhofinalprogramacaomobile.model.Habito
import com.example.trabalhofinalprogramacaomobile.repository.HabitoRepository

class AddHabitoDialogFragment : DialogFragment() {

    interface HabitoListener {
        fun onHabitoAdicionado(habito: Habito)
    }

    private var listener: HabitoListener? = null
    private lateinit var habitoRepository: HabitoRepository

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HabitoListener) {
            listener = context
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_add_habito, null)

        val edtNome = view.findViewById<EditText>(R.id.edtNome)
        val edtTempoMeta = view.findViewById<EditText>(R.id.edtTempoMeta)
        val btnSalvar = view.findViewById<Button>(R.id.btnSalvar)

        habitoRepository = HabitoRepository(requireContext())

        btnSalvar.setOnClickListener {
            val nome = edtNome.text.toString()
            val tempoMeta = edtTempoMeta.text.toString().toIntOrNull()

            if (nome.isNotBlank() && tempoMeta != null) {
                val novoHabito = Habito(
                    nome = nome,
                    tempoMeta = tempoMeta,
                    dataCriacao = System.currentTimeMillis()
                )

                habitoRepository.adicionarHabito(novoHabito)
                listener?.onHabitoAdicionado(novoHabito)

                Toast.makeText(requireContext(), "Hábito adicionado!", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setView(view)
        return builder.create()
    }
}
