package com.example.astronomicalguidebook.opengl

import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import com.example.astronomicalguidebook.R
import com.example.astronomicalguidebook.data.PlanetsData
import com.example.astronomicalguidebook.ui.planets.PlanetInfoDialog

class OpenGLActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: OpenGLRenderer
    private lateinit var tvPlanetName: TextView
    private lateinit var btnLeft: Button
    private lateinit var btnRight: Button
    private lateinit var btnInfo: Button

    private var showPlanetInfo by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_opengl)

        glSurfaceView = findViewById(R.id.gl_surface_view)
        tvPlanetName = findViewById(R.id.tv_planet_name)
        btnLeft = findViewById(R.id.btn_left)
        btnRight = findViewById(R.id.btn_right)
        btnInfo = findViewById(R.id.btn_info)

        renderer = OpenGLRenderer(this)

        glSurfaceView.setEGLContextClientVersion(1)
        glSurfaceView.setRenderer(renderer)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        setupButtons()
        setupComposeView()
    }

    private fun setupComposeView() {
        val composeView = ComposeView(this).apply {
            setContent {
                if (showPlanetInfo) {
                    val selectedIndex = renderer.getSelectedPlanetIndex()
                    val planetInfo = if (selectedIndex in 0..7) {
                        PlanetsData.getPlanetById(selectedIndex)
                    } else {
                        PlanetsData.getPlanetById(2)
                    }

                    PlanetInfoDialog(
                        planetInfo = planetInfo,
                        onDismiss = {
                            showPlanetInfo = false
                        }
                    )
                }
            }
        }

        addContentView(
            composeView,
            android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun setupButtons() {
        btnLeft.setOnClickListener {
            renderer.selectPreviousPlanet()
            updatePlanetName()
        }

        btnRight.setOnClickListener {
            renderer.selectNextPlanet()
            updatePlanetName()
        }

        btnInfo.setOnClickListener {
            val selectedIndex = renderer.getSelectedPlanetIndex()
            if (selectedIndex in 0..7) {
                showPlanetInfo = true
            } else if (selectedIndex == 8) {
                startActivity(Intent(this, MoonActivity::class.java))
            }
        }
    }

    private fun updatePlanetName() {
        val selectedIndex = renderer.getSelectedPlanetIndex()
        tvPlanetName.text = when (selectedIndex) {
            0 -> "Меркурий"
            1 -> "Венера"
            2 -> "Земля"
            3 -> "Марс"
            4 -> "Юпитер"
            5 -> "Сатурн"
            6 -> "Уран"
            7 -> "Нептун"
            8 -> "Луна"
            else -> "Неизвестно"
        }
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }
}
