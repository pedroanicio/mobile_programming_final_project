package com.example.trabalhofinalprogramacaomobile.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.trabalhofinalprogramacaomobile.R
import com.example.trabalhofinalprogramacaomobile.model.Habito
import com.example.trabalhofinalprogramacaomobile.repository.HabitoRepository

class EditHabitoDialogFragment(
    private val habito: Habito
) : DialogFragment() {

    interface HabitoListener {
        fun onHabitoEditado()
    }

    private lateinit var habitoRepository: HabitoRepository
    private var listener: HabitoListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HabitoListener) {
            listener = context
        }
        habitoRepository = HabitoRepository(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_edit_habito, null)

        val edtNome = view.findViewById<EditText>(R.id.edtNome)
        val edtTempoMeta = view.findViewById<EditText>(R.id.edtTempoMeta)
        val btnSalvar = view.findViewById<Button>(R.id.btnSalvar)
        val btnExcluir = view.findViewById<Button>(R.id.btnExcluir)

        edtNome.setText(habito.nome)
        edtTempoMeta.setText(habito.tempoMeta.toString())

        btnSalvar.setOnClickListener {
            val novoNome = edtNome.text.toString()
            val novoTempoMeta = edtTempoMeta.text.toString().toIntOrNull()

            if (novoNome.isNotBlank() && novoTempoMeta != null) {
                val habitoAtualizado = habito.copy(nome = novoNome, tempoMeta = novoTempoMeta)
                habitoRepository.updateHabito(habitoAtualizado)
                listener?.onHabitoEditado()
                Toast.makeText(requireContext(), "Hábito atualizado!", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }

        btnExcluir.setOnClickListener {
            habitoRepository.deleteHabito(habito.id ?: -1)
            listener?.onHabitoEditado()
            Toast.makeText(requireContext(), "Hábito excluído!", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        builder.setView(view)
        return builder.create()
    }
}