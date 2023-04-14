package ru.netology.travelmarkers.dto

data class Bookmark(
    val id: Long = 0L,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String = ""
)
