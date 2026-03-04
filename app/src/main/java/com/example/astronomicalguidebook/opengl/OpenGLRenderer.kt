package com.example.astronomicalguidebook.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.astronomicalguidebook.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private lateinit var backgroundSquare: Square
    private lateinit var sun: TexturedPlanet
    private val planets = mutableListOf<TexturedPlanet>()
    private lateinit var moon: TexturedPlanet
    private lateinit var selectorCube: SelectorCube

    private val orbitRadii = floatArrayOf(
        2.5f,  // Меркурий
        4.0f,  // Венера
        5.5f,  // Земля
        7.0f,  // Марс
        9.0f,  // Юпитер
        11.0f, // Сатурн
        13.0f, // Уран
        15.0f  // Нептун
    )

    private val eccentricity = floatArrayOf(
        0.1f, 0.05f, 0.1f, 0.15f, 0.2f, 0.3f, 0.25f, 0.2f
    )

    private val planetSizes = floatArrayOf(
        0.25f, 0.35f, 0.37f, 0.3f, 0.9f, 0.8f, 0.7f, 0.68f
    )

    private val textureIds = intArrayOf(
        R.drawable.mercury,
        R.drawable.venus,
        R.drawable.earth,
        R.drawable.mars,
        R.drawable.jupiter,
        R.drawable.saturn,
        R.drawable.uranus,
        R.drawable.neptune
    )

    private val sunSize = 1.8f
    private val moonSize = 0.15f

    private val orbitSpeedMultipliers = floatArrayOf(
        3.0f, 2.2f, 1.8f, 1.5f, 1.0f, 0.7f, 0.5f, 0.4f
    )

    private val rotationSpeedMultipliers = floatArrayOf(
        9.0f,  // Меркурий
        8.0f,  // Венера
        7.0f,  // Земля
        5.0f,  // Марс
        14.0f, // Юпитер
        8.0f,  // Сатурн
        6.0f,  // Уран
        6.0f   // Нептун
    )

    private var lastTime = 0L
    private var baseAngle = 0f

    private var selectedPlanetIndex = 0

    private var isInitialized = false
    private var pendingSelectedPlanetIndex = 0

    fun selectNextPlanet() {
        val newIndex = (selectedPlanetIndex + 1) % 9
        updateSelectedPlanet(newIndex)
        Log.d("Renderer", "Next planet selected: $newIndex")
    }

    fun selectPreviousPlanet() {
        val newIndex = (selectedPlanetIndex - 1 + 9) % 9
        updateSelectedPlanet(newIndex)
        Log.d("Renderer", "Previous planet selected: $newIndex")
    }

    private fun updateSelectedPlanet(newIndex: Int) {
        selectedPlanetIndex = newIndex
        if (!isInitialized) {
            pendingSelectedPlanetIndex = newIndex
        }
    }

    fun getSelectedPlanetName(): String {
        return when (selectedPlanetIndex) {
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

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        Log.d("Renderer", "onSurfaceCreated - Starting")

        backgroundSquare = Square(context)
        backgroundSquare.loadTexture(gl)

        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        gl.glEnable(GL10.GL_DEPTH_TEST)
        gl.glDepthFunc(GL10.GL_LEQUAL)

        sun = TexturedPlanet(context, sunSize, R.drawable.sun)

        planets.clear()
        for (i in textureIds.indices) {
            planets.add(TexturedPlanet(context, planetSizes[i], textureIds[i]))
        }

        moon = TexturedPlanet(context, moonSize, R.drawable.moon)

        selectorCube = SelectorCube()

        sun.loadTexture(gl)
        for (planet in planets) {
            planet.loadTexture(gl)
        }
        moon.loadTexture(gl)

        lastTime = System.currentTimeMillis()

        selectedPlanetIndex = pendingSelectedPlanetIndex
        isInitialized = true

        Log.d("Renderer", "onSurfaceCreated - Completed, selected planet: $selectedPlanetIndex")
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        Log.d("Renderer", "onSurfaceChanged")
        gl.glViewport(0, 0, width, height)

        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()

        val aspect = width.toFloat() / height.toFloat()
        val near = 1.0f
        val far = 100.0f
        val orthoSize = 12.0f

        if (width > height) {
            gl.glOrthof(-orthoSize * aspect, orthoSize * aspect, -orthoSize, orthoSize, near, far)
        } else {
            gl.glOrthof(-orthoSize, orthoSize, -orthoSize / aspect, orthoSize / aspect, near, far)
        }

        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
    }

    override fun onDrawFrame(gl: GL10) {
        val currentTime = System.currentTimeMillis()
        val deltaTime = (currentTime - lastTime) / 80000f
        lastTime = currentTime

        baseAngle += deltaTime

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()

        android.opengl.GLU.gluLookAt(gl,
            8.0f, 10.0f, 8.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f
        )

        gl.glPushMatrix()
        gl.glLoadIdentity()

        gl.glDepthMask(false)
        gl.glDisable(GL10.GL_DEPTH_TEST)

        gl.glTranslatef(0.0f, 0.0f, -50.0f)
        gl.glScalef(30.0f, 30.0f, 1.0f)

        backgroundSquare.draw(gl)

        gl.glEnable(GL10.GL_DEPTH_TEST)
        gl.glDepthMask(true)

        gl.glPopMatrix()

        gl.glPushMatrix()
        gl.glRotatef(baseAngle * 5 * rotationSpeedMultipliers[0], 0f, 1f, 0f)
        sun.draw(gl)
        gl.glPopMatrix()

        for (i in planets.indices) {
            gl.glPushMatrix()

            val a = orbitRadii[i]
            val ecc = eccentricity[i]
            val b = a * Math.sqrt(1.0 - ecc * ecc).toFloat()

            val orbitAngle = baseAngle * 40 * orbitSpeedMultipliers[i] + i * 45f

            val x = a * Math.cos(orbitAngle.toDouble()).toFloat()
            val z = b * Math.sin(orbitAngle.toDouble()).toFloat()

            gl.glTranslatef(x, 0f, z)

            val rotationAngle = baseAngle * 60 * rotationSpeedMultipliers[i]
            gl.glRotatef(rotationAngle, 0f, 1f, 0f)

            planets[i].draw(gl)
            gl.glPopMatrix()
        }

        for (i in planets.indices) {
            if (i == selectedPlanetIndex) {
                gl.glPushMatrix()

                val a = orbitRadii[i]
                val ecc = eccentricity[i]
                val b = a * Math.sqrt(1.0 - ecc * ecc).toFloat()

                val orbitAngle = baseAngle * 40 * orbitSpeedMultipliers[i] + i * 45f

                val x = a * Math.cos(orbitAngle.toDouble()).toFloat()
                val z = b * Math.sin(orbitAngle.toDouble()).toFloat()

                gl.glTranslatef(x, 0f, z)
                selectorCube.draw(gl)

                gl.glPopMatrix()
            }
        }

        gl.glPushMatrix()

        val earthRadius = orbitRadii[2]
        val earthEcc = eccentricity[2]
        val earthB = earthRadius * Math.sqrt(1.0 - earthEcc * earthEcc).toFloat()

        val earthSpeed = orbitSpeedMultipliers[2] * 40
        val earthAngle = baseAngle * earthSpeed + 2 * 45f

        val earthX = earthRadius * Math.cos(earthAngle.toDouble()).toFloat()
        val earthZ = earthB * Math.sin(earthAngle.toDouble()).toFloat()

        val moonOrbit = 1.2f
        val moonAngle = baseAngle * 120

        val moonX = earthX + moonOrbit * Math.cos(moonAngle.toDouble()).toFloat()
        val moonY = moonOrbit * Math.sin(moonAngle.toDouble()).toFloat()
        val moonZ = earthZ

        gl.glTranslatef(moonX, moonY, moonZ)

        val moonRotation = baseAngle * 40
        gl.glRotatef(moonRotation, 0f, 1f, 0f)

        moon.draw(gl)
        gl.glPopMatrix()

        if (selectedPlanetIndex == 8) {
            gl.glPushMatrix()

            gl.glTranslatef(moonX, moonY, moonZ)
            selectorCube.draw(gl)

            gl.glPopMatrix()
        }
    }
}