package com.example.astronomicalguidebook.data

import com.example.astronomicalguidebook.R

object PlanetsData {
    val planets = listOf(
        PlanetInfo(0, "Меркурий", "Самая близкая к Солнцу планета. Поверхность покрыта кратерами, напоминает Луну.", R.drawable.mercury_info),
        PlanetInfo(1, "Венера", "Вторая планета от Солнца. Имеет плотную атмосферу из углекислого газа.", R.drawable.venus_info),
        PlanetInfo(2, "Земля", "Третья планета от Солнца. Единственная известная планета с жизнью.", R.drawable.earth_info),
        PlanetInfo(3, "Марс", "Красная планета. Имеет самый высокий вулкан в Солнечной системе.", R.drawable.mars_info),
        PlanetInfo(4, "Юпитер", "Самая большая планета. Имеет Большое красное пятно - гигантский шторм.", R.drawable.jupiter_info),
        PlanetInfo(5, "Сатурн", "Знаменит своими кольцами из льда и пыли.", R.drawable.saturn_info),
        PlanetInfo(6, "Уран", "Ледяной гигант. Вращается на боку из-за наклона оси.", R.drawable.uranus_info),
        PlanetInfo(7, "Нептун", "Самая ветреная планета. Имеет темные пятна в атмосфере.", R.drawable.neptune_info)
    )

    fun getPlanetById(id: Int): PlanetInfo = planets[id]
}
