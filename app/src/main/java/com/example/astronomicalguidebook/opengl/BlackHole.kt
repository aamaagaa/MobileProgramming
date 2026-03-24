package com.example.astronomicalguidebook.opengl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLUtils
import com.example.astronomicalguidebook.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10

class BlackHole(
    private val context: Context,
    private val width: Float = 2.0f,
    private val height: Float = 2.0f
) {
    private val vertices = floatArrayOf(
        -width/2, -height/2, 0.0f,
        width/2, -height/2, 0.0f,
        -width/2,  height/2, 0.0f,
        width/2,  height/2, 0.0f
    )

    private val textureCoords = floatArrayOf(
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f
    )

    private val indices = shortArrayOf(
        0, 1, 2,
        1, 3, 2
    )

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer
    private lateinit var indexBuffer: ShortBuffer
    private var textureId = 0

    private var positionX = -18f
    private val positionY = 2f
    private val positionZ = -12f
    private var speed = 0.1f

    init {
        setupBuffers()
    }

    private fun setupBuffers() {
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        val tbb = ByteBuffer.allocateDirect(textureCoords.size * 4)
        tbb.order(ByteOrder.nativeOrder())
        textureBuffer = tbb.asFloatBuffer()
        textureBuffer.put(textureCoords)
        textureBuffer.position(0)

        val ibb = ByteBuffer.allocateDirect(indices.size * 2)
        ibb.order(ByteOrder.nativeOrder())
        indexBuffer = ibb.asShortBuffer()
        indexBuffer.put(indices)
        indexBuffer.position(0)
    }

    fun loadTexture(gl: GL10) {
        val textures = IntArray(1)
        gl.glGenTextures(1, textures, 0)
        textureId = textures[0]

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId)

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE.toFloat())

        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.black_hole)
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
    }

    fun update() {
        positionX += speed

        if (positionX > 22f) {
            positionX = -22f
        }
    }

    fun draw(gl: GL10) {
        update()

        gl.glPushMatrix()
        gl.glTranslatef(positionX, positionY, positionZ)

        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId)

        gl.glEnable(GL10.GL_BLEND)
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer)

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.size, GL10.GL_UNSIGNED_SHORT, indexBuffer)

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

        gl.glDisable(GL10.GL_BLEND)
        gl.glDisable(GL10.GL_TEXTURE_2D)

        gl.glPopMatrix()
    }
}