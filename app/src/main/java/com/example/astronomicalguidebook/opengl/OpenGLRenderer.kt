package com.example.astronomicalguidebook.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var square: Square
    private lateinit var cube: Cube

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        gl.glEnable(GL10.GL_DEPTH_TEST)
        gl.glDepthFunc(GL10.GL_LEQUAL)

        square = Square(context)
        cube = Cube()

        square.loadTexture(gl)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        gl.glViewport(0, 0, width, height)

        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()

        val aspect = width.toFloat() / height.toFloat()

        val near = 1.0f
        val far = 100.0f
        val fovy = 45.0f

        val top = near * Math.tan(Math.toRadians(fovy / 2.0)).toFloat()
        val bottom = -top
        val left = bottom * aspect
        val right = top * aspect

        gl.glFrustumf(left, right, bottom, top, near, far)

        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
    }

    override fun onDrawFrame(gl: GL10) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

        gl.glPushMatrix()
        gl.glLoadIdentity()

        gl.glTranslatef(0.0f, 0.0f, -15.0f)
        gl.glScalef(10.0f, 10.0f, 1.0f)

        square.draw(gl)
        gl.glPopMatrix()

        gl.glPushMatrix()
        gl.glLoadIdentity()

        gl.glTranslatef(0.0f, 0.0f, -5.0f)

        gl.glRotatef(30.0f, 1.0f, 0.0f, 0.0f)
        gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f)
   
        cube.draw(gl)
        gl.glPopMatrix()
    }
}