package com.example.trabalhofinalprogramacaomobile.model

data class Habito (
    val id: Int? = null,
    val nome: String,
    val tempoMeta: Int,
    val dataCriacao: Long
) {
    override fun toString(): String {
        return nome
    }
}

