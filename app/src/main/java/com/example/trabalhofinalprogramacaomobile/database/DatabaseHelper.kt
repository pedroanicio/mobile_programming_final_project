package com.example.trabalhofinalprogramacaomobile.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "HabitoRastreador.db";
        const val DATABASE_VERSION = 1;

        const val TABLE_HABITOS = "habitos";
        const val TABLE_PROGRESSO = "progresso";
        const val TABLE_POMODORO = "pomodoro";

        // Tabela Habitos
        const val COLUMN_HABITO_ID = "id"
        const val COLUMN_HABITO_NOME = "nome"
        const val COLUMN_HABITO_TEMPO_META = "tempoMeta"
        const val COLUMN_HABITO_DATA_CRIACAO = "dataCriacao"

        // Tabela Progresso
        const val COLUMN_PROGRESSO_ID = "id"
        const val COLUMN_PROGRESSO_HABITO_ID = "habitoId"
        const val COLUMN_PROGRESSO_DATA = "data"
        const val COLUMN_PROGRESSO_TEMPO_ESTUDADO = "tempoEstudado"

        // Tabela Pomodoro
        const val COLUMN_POMODORO_ID = "id"
        const val COLUMN_POMODORO_DURACAO_FOCO = "duracaoFoco"
        const val COLUMN_POMODORO_DURACAO_PAUSA = "duracaoPausa"
        const val COLUMN_POMODORO_NUMERO_CICLOS = "numeroCiclos"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Criação das tabelas
        val createHabitosTable = """
            CREATE TABLE $TABLE_HABITOS (
                $COLUMN_HABITO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_HABITO_NOME TEXT NOT NULL,
                $COLUMN_HABITO_TEMPO_META INTEGER NOT NULL,
                $COLUMN_HABITO_DATA_CRIACAO TEXT NOT NULL
            )
        """.trimIndent()

        val createProgressoTable = """
            CREATE TABLE $TABLE_PROGRESSO (
                $COLUMN_PROGRESSO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PROGRESSO_HABITO_ID INTEGER NOT NULL,
                $COLUMN_PROGRESSO_DATA TEXT NOT NULL,
                $COLUMN_PROGRESSO_TEMPO_ESTUDADO INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_PROGRESSO_HABITO_ID) REFERENCES $TABLE_HABITOS($COLUMN_HABITO_ID)
            )
        """.trimIndent()

        val createPomodoroTable = """
            CREATE TABLE $TABLE_POMODORO (
                $COLUMN_POMODORO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_POMODORO_DURACAO_FOCO INTEGER NOT NULL,
                $COLUMN_POMODORO_DURACAO_PAUSA INTEGER NOT NULL,
                $COLUMN_POMODORO_NUMERO_CICLOS INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(createHabitosTable)
        db.execSQL(createProgressoTable)
        db.execSQL(createPomodoroTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HABITOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PROGRESSO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_POMODORO")
        onCreate(db)
    }

}