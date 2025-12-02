package com.example.appplaypulse_grupo4

import com.example.appplaypulse_grupo4.ui.screens.mapGame
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class GameMapperKotestTest : StringSpec({
    "maps fantasy keywords to FFXIV" {
        mapGame("ffxiv") shouldBe ("Final Fantasy XIV" to "finalfantasy")
    }

    "keeps unknown titles with generic image" {
        mapGame("Indie Surprise") shouldBe ("Indie Surprise" to "apex")
    }
})
