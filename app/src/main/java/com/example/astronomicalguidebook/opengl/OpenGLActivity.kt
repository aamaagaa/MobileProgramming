package com.example.astronomicalguidebook

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.astronomicalguidebook.databinding.ActivityOpenglBinding
import com.example.astronomicalguidebook.opengl.OpenGLRenderer

class OpenGLActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOpenglBinding
    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: OpenGLRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOpenglBinding.inflate(layoutInflater)
        setContentView(binding.root)

        renderer = OpenGLRenderer(this)

        glSurfaceView = binding.glSurfaceView
        glSurfaceView.setEGLContextClientVersion(1)
        glSurfaceView.setRenderer(renderer)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        setupButtons()
    }

    private fun setupButtons() {
        binding.btnLeft.setOnClickListener {
            renderer.selectPreviousPlanet()
            updatePlanetName()
        }

        binding.btnRight.setOnClickListener {
            renderer.selectNextPlanet()
            updatePlanetName()
        }

        binding.btnInfo.setOnClickListener {
            val planetName = renderer.getSelectedPlanetName()
            Toast.makeText(this, "Выбрана планета: $planetName", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePlanetName() {
        binding.tvPlanetName.text = renderer.getSelectedPlanetName()
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
