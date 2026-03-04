package com.example.astronomicalguidebook

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.astronomicalguidebook.ui.news.NewsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewsScreen(
                onOpenSolarSystem = {
                    startActivity(Intent(this, OpenGLActivity::class.java))
                }
            )
        }
    }
}