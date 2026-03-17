package com.example.astronomicalguidebook.opengl

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.astronomicalguidebook.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MoonActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var moonModel: MoonModel

    private var lastTime = 0L
    private var rotationAngle = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moon)

        moonModel = MoonModel(this)

        glSurfaceView = findViewById(R.id.moon_gl_surface_view)
        glSurfaceView.setEGLContextClientVersion(1)
        glSurfaceView.setRenderer(object : GLSurfaceView.Renderer {
            override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
                gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
                gl.glEnable(GL10.GL_DEPTH_TEST)

                gl.glEnable(GL10.GL_LIGHTING)
                gl.glEnable(GL10.GL_LIGHT0)

                moonModel.loadTexture(gl)
                lastTime = System.currentTimeMillis()
            }

            override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
                gl.glViewport(0, 0, width, height)

                val aspect = width.toFloat() / height.toFloat()
                gl.glMatrixMode(GL10.GL_PROJECTION)
                gl.glLoadIdentity()
                gl.glFrustumf(-aspect, aspect, -1f, 1f, 2f, 20f)

                gl.glMatrixMode(GL10.GL_MODELVIEW)
                gl.glLoadIdentity()
            }

            override fun onDrawFrame(gl: GL10) {
                val currentTime = System.currentTimeMillis()
                val deltaTime = (currentTime - lastTime) / 80000f
                lastTime = currentTime

                rotationAngle += deltaTime * 40f

                gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

                gl.glMatrixMode(GL10.GL_MODELVIEW)
                gl.glLoadIdentity()

                val lightPos = floatArrayOf(2.0f, 2.0f, 2.0f, 0.0f)
                val lightAmbient = floatArrayOf(0.3f, 0.3f, 0.3f, 1.0f)
                val lightDiffuse = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
                val lightSpecular = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)

                gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0)
                gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0)
                gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0)
                gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, lightSpecular, 0)

                gl.glTranslatef(0f, 0f, -8f)
                gl.glRotatef(rotationAngle * 10, 0f, 1f, 0f)
                gl.glRotatef(30f, 1f, 0f, 0f)

                moonModel.draw(gl)
            }
        })

        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        findViewById<Button>(R.id.btn_back).setOnClickListener {
            finish()
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
