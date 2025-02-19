package com.example.trabalhofinalprogramacaomobile.model

data class ProgressoDiario(
    val id: Int? = null,
    val habitoId: Int,
    val data: String, // yyyy-MM-dd
    var tempoEstudo: Int
)
