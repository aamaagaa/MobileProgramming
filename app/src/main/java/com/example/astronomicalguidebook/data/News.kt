package com.example.astronomicalguidebook.data

import com.example.astronomicalguidebook.R

data class News(
    val id: Int,
    val title: String,
    val content: String,
    var likes: Int = 0
)

data class Advertisement(
    val id: Int,
    val title: String,
    val imageResId: Int,
)
object NewsData {
    val newsList = listOf(
        News(
            id = 1,
            title = "Обнаружена новая экзопланета",
            content = "Астрономы обнаружили планету Kepler-186f в зоне обитаемости звезды."
        ),
        News(
            id = 2,
            title = "Запуск нового телескопа",
            content = "NASA запустило новый космический телескоп для изучения темной материи."
        ),
        News(
            id = 3,
            title = "Марсианские овраги",
            content = "Ученые обнаружили следы жидкой воды на Марсе в прошлом."
        ),
        News(
            id = 4,
            title = "Солнечное затмение 2024",
            content = "В апреле 2024 года ожидается полное солнечное затмение."
        ),
        News(
            id = 5,
            title = "Астероид приближается",
            content = "К Земле приближается астероид диаметром 50 метров."
        ),
        News(
            id = 6,
            title = "Новые спутники Юпитера",
            content = "Открыты 12 новых спутников у планеты Юпитер."
        ),
        News(
            id = 7,
            title = "Космический туризм",
            content = "Компания SpaceX планирует новые туристические полеты на МКС."
        ),
        News(
            id = 8,
            title = "Лунная база",
            content = "NASA анонсировало планы по созданию лунной базы к 2030 году."
        ),
        News(
            id = 9,
            title = "Черные дыры",
            content = "Ученые зафиксировали слияние двух черных дыр."
        ),
        News(
            id = 10,
            title = "Северное сияние",
            content = "На этой неделе ожидается сильная магнитная буря."
        )
    )

    val advertisements = listOf(
        Advertisement(
            id = 11,
            title = "BURGER KING теперь и в космосе!",
            imageResId = R.drawable.bk,
        ),
        Advertisement(
            id = 12,
            title = "Кола не просто мировой уровень, а вселеннский!",
            imageResId = R.drawable.cola,
        ),
        Advertisement(
            id = 13,
            title = "СРОЧНО ИЩУ РАБОТУ!!",
            imageResId = R.drawable.job,
        ),
        Advertisement(
            id = 14,
            title = "Напиток от Ганвеста уже и на Солнце.",
            imageResId = R.drawable.pepe,
        )
    )
}