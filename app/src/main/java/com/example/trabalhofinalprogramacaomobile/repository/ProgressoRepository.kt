package com.example.trabalhofinalprogramacaomobile.repository

import android.content.ContentValues
import android.content.Context
import com.example.trabalhofinalprogramacaomobile.database.DatabaseHelper
import com.example.trabalhofinalprogramacaomobile.model.ProgressoDiario

class ProgressoRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun adicionarProgressso(progresso: ProgressoDiario): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PROGRESSO_HABITO_ID, progresso.habitoId)
            put(DatabaseHelper.COLUMN_PROGRESSO_DATA, progresso.data)
            put(DatabaseHelper.COLUMN_PROGRESSO_TEMPO_ESTUDADO, progresso.tempoEstudo)
        }
        val result = db.insert(DatabaseHelper.TABLE_PROGRESSO, null, values)
        db.close()
        return result
    }

    fun getProgressoDiario(habitoId: Int, data: String): ProgressoDiario? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_PROGRESSO,
            null,
            "${DatabaseHelper.COLUMN_PROGRESSO_HABITO_ID} = ? AND ${DatabaseHelper.COLUMN_PROGRESSO_DATA} = ?",
            arrayOf(habitoId.toString(), data),
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROGRESSO_ID))
            val habitoId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROGRESSO_HABITO_ID))
            val data = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROGRESSO_DATA))
            val tempoEstudado = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROGRESSO_TEMPO_ESTUDADO))
            ProgressoDiario(id, habitoId, data, tempoEstudado)
        } else {
            null
        }
    }

    fun atualizarProgresso(progresso: ProgressoDiario): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PROGRESSO_TEMPO_ESTUDADO, progresso.tempoEstudo)
        }
        val result = db.update(
            DatabaseHelper.TABLE_PROGRESSO,
            values,
            "${DatabaseHelper.COLUMN_PROGRESSO_ID} = ?",
            arrayOf(progresso.id.toString())
        )
        db.close()
        return result
    }
}