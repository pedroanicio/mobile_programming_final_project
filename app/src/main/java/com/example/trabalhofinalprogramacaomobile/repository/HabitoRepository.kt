package com.example.trabalhofinalprogramacaomobile.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.trabalhofinalprogramacaomobile.database.DatabaseHelper
import com.example.trabalhofinalprogramacaomobile.model.Habito

class HabitoRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun adicionarHabito(habito: Habito): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_HABITO_NOME, habito.nome)
            put(DatabaseHelper.COLUMN_HABITO_TEMPO_META, habito.tempoMeta)
            put(DatabaseHelper.COLUMN_HABITO_DATA_CRIACAO, habito.dataCriacao)
        }
        val result = db.insert(DatabaseHelper.TABLE_HABITOS, null, values)
        db.close()
        return result;
    }

    fun getHabitos(): List<Habito> {
        val habitos = mutableListOf<Habito>()
        val db = dbHelper.readableDatabase

        db.query(DatabaseHelper.TABLE_HABITOS, null, null, null, null, null, null).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HABITO_ID))
                val nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HABITO_NOME))
                val tempoMeta = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HABITO_TEMPO_META))
                val dataCriacao = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HABITO_DATA_CRIACAO))
                habitos.add(Habito(id, nome, tempoMeta, dataCriacao))
            }
        }
        db.close()
        return habitos
    }

    fun updateHabito(habito: Habito): Int{
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_HABITO_NOME, habito.nome)
            put(DatabaseHelper.COLUMN_HABITO_TEMPO_META, habito.tempoMeta)
            put(DatabaseHelper.COLUMN_HABITO_DATA_CRIACAO, habito.dataCriacao)
        }
        val result = db.update(
            DatabaseHelper.TABLE_HABITOS,
            values,
            "${DatabaseHelper.COLUMN_HABITO_ID} = ?",
            arrayOf(habito.id.toString())
        )
        db.close()
        return result
    }

    fun deleteHabito(habitoId: Int): Int {
        val db = dbHelper.writableDatabase
        val result = db.delete(
            DatabaseHelper.TABLE_HABITOS,
            "${DatabaseHelper.COLUMN_HABITO_ID} = ?",
            arrayOf(habitoId.toString())
        )
        db.close()
        return result
    }

}