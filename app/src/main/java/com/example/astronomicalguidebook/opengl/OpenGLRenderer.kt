package com.example.astronomicalguidebook.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class OpenGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var sun: Planet
    private val planets = mutableListOf<Planet>()
    private lateinit var moon: Planet

    // эллипсы
    private val orbitRadii = floatArrayOf(
        2.5f,  // Меркурий - базовая орбита
        3.5f,  // Венера
        4.5f,  // Земля
        5.5f,  // Марс
        6.5f,  // Юпитер
        7.5f,  // Сатурн
        8.5f,  // Уран
        9.5f   // Нептун
    )

    // Эксцентриситет для каждой планеты (0 = круг, чем больше, тем более вытянутый эллипс)
    private val eccentricity = floatArrayOf(
        0.1f,  // Меркурий
        0.05f, // Венера
        0.1f,  // Земля
        0.15f, // Марс
        0.2f,  // Юпитер
        0.3f,  // Сатурн
        0.25f, // Уран
        0.2f   // Нептун
    )

    // РАЗМЕРЫ планет относительные
    private val planetSizes = floatArrayOf(
        0.25f,  // Меркурий
        0.35f,  // Венера
        0.37f,  // Земля
        0.3f,   // Марс
        0.9f,   // Юпитер
        0.8f,   // Сатурн
        0.7f,   // Уран
        0.68f   // Нептун
    )

    private val sunSize = 1.8f
    private val moonSize = 0.15f

    // Скорости планет
    private val speedMultipliers = floatArrayOf(
        3.0f,  // Меркурий
        2.2f,  // Венера
        1.8f,  // Земля
        1.5f,  // Марс
        1.0f,  // Юпитер
        0.7f,  // Сатурн
        0.5f,  // Уран
        0.4f   // Нептун
    )

    private var lastTime = 0L
    private var baseAngle = 0f

    private val orbitLines = mutableListOf<FloatBuffer>()

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        Log.d("Renderer", "onSurfaceCreated")
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        gl.glEnable(GL10.GL_DEPTH_TEST)
        gl.glDepthFunc(GL10.GL_LEQUAL)

        sun = Planet(sunSize, 1.0f, 1.0f, 0.2f)

        // Цвета планет
        val colors = arrayOf(
            floatArrayOf(0.7f, 0.7f, 0.7f), // Меркурий
            floatArrayOf(1.0f, 0.8f, 0.5f), // Венера
            floatArrayOf(0.2f, 0.5f, 1.0f), // Земля
            floatArrayOf(1.0f, 0.3f, 0.2f), // Марс
            floatArrayOf(1.0f, 0.8f, 0.4f), // Юпитер
            floatArrayOf(0.9f, 0.7f, 0.3f), // Сатурн
            floatArrayOf(0.4f, 0.7f, 1.0f), // Уран
            floatArrayOf(0.2f, 0.3f, 1.0f)  // Нептун
        )

        repeat(8) { i ->
            val c = colors[i]
            planets.add(Planet(planetSizes[i], c[0], c[1], c[2]))
        }

        moon = Planet(moonSize, 0.8f, 0.8f, 0.8f)

        createOrbitLines(gl)

        lastTime = System.currentTimeMillis()
    }

    private fun createOrbitLines(gl: GL10) {
        val segments = 100

        for (planetIndex in orbitRadii.indices) {
            val vertices = mutableListOf<Float>()
            val radius = orbitRadii[planetIndex]
            val ecc = eccentricity[planetIndex]

            val a = radius
            val b = a * Math.sqrt(1.0 - ecc * ecc).toFloat()

            for (i in 0..segments) {
                val angle = 2 * Math.PI * i / segments
                val x = a * Math.cos(angle).toFloat()
                val z = b * Math.sin(angle).toFloat()

                vertices.add(x)
                vertices.add(0.0f)
                vertices.add(z)
            }

            val buffer = ByteBuffer.allocateDirect(vertices.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            buffer.put(vertices.toFloatArray())
            buffer.position(0)

            orbitLines.add(buffer)
        }
    }

    private fun drawOrbits(gl: GL10) {
        gl.glDisable(GL10.GL_LIGHTING)
        gl.glDisable(GL10.GL_TEXTURE_2D)

        gl.glColor4f(0.3f, 0.3f, 0.3f, 0.5f)

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)

        for (buffer in orbitLines) {
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, buffer)
            gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, buffer.capacity() / 3)
        }

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)

    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        Log.d("Renderer", "onSurfaceChanged")
        gl.glViewport(0, 0, width, height)

        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()

        val aspect = width.toFloat() / height.toFloat()
        val near = 1.0f
        val far = 100.0f

        val orthoSize = 9.0f

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
        val deltaTime = (currentTime - lastTime) / 50000f
        lastTime = currentTime

        baseAngle += deltaTime

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

        // Вид под наклоном
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()

        android.opengl.GLU.gluLookAt(gl,
            8.0f, 6.0f, 8.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f
        )

        drawOrbits(gl)

        gl.glPushMatrix()
        gl.glRotatef(baseAngle * 5, 0f, 1f, 0f)
        sun.draw(gl)
        gl.glPopMatrix()

        for (i in planets.indices) {
            gl.glPushMatrix()

            val a = orbitRadii[i]
            val ecc = eccentricity[i]
            val b = a * Math.sqrt(1.0 - ecc * ecc).toFloat()

            val planetAngle = baseAngle * 40 * speedMultipliers[i] + i * 45f

            val x = a * Math.cos(planetAngle.toDouble()).toFloat()
            val z = b * Math.sin(planetAngle.toDouble()).toFloat()

            gl.glTranslatef(x, 0f, z)
            gl.glRotatef(baseAngle * 30, 0f, 1f, 0f)

            planets[i].draw(gl)
            gl.glPopMatrix()
        }

        gl.glPushMatrix()

        val earthRadius = orbitRadii[2]
        val earthEcc = eccentricity[2]
        val earthB = earthRadius * Math.sqrt(1.0 - earthEcc * earthEcc).toFloat()

        val earthSpeed = speedMultipliers[2] * 40
        val earthAngle = baseAngle * earthSpeed + 2 * 45f

        val earthX = earthRadius * Math.cos(earthAngle.toDouble()).toFloat()
        val earthZ = earthB * Math.sin(earthAngle.toDouble()).toFloat()

        val moonOrbit = 1.2f
        val moonAngle = baseAngle * 120

        val moonX = earthX + moonOrbit * Math.cos(moonAngle.toDouble()).toFloat()
        val moonY = moonOrbit * Math.sin(moonAngle.toDouble()).toFloat()
        val moonZ = earthZ

        gl.glTranslatef(moonX, moonY, moonZ)
        gl.glRotatef(baseAngle * 20, 0f, 1f, 0f)

        moon.draw(gl)
        gl.glPopMatrix()
    }
}