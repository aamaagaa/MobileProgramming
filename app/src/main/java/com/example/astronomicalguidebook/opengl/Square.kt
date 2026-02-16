package com.example.astronomicalguidebook.opengl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLUtils
import com.example.astronomicalguidebook.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

class Square(private val context: Context) {
    private val squareVertices = floatArrayOf(
        -1.0f, -1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        -1.0f,  1.0f, 0.0f,
        1.0f,  1.0f, 0.0f
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

    private var vertexBuffer: FloatBuffer
    private var textureBuffer: FloatBuffer
    private var indexBuffer: java.nio.ShortBuffer
    private var textures = IntArray(1)

    init {
        val vbb = ByteBuffer.allocateDirect(squareVertices.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer.put(squareVertices)
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
        gl.glGenTextures(1, textures, 0)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0])

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT.toFloat())

        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.galaxy)

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)

        bitmap.recycle()
    }

    fun draw(gl: GL10) {
        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0])

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer)

        gl.glDrawElements(
            GL10.GL_TRIANGLES,
            indices.size,
            GL10.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glDisable(GL10.GL_TEXTURE_2D)
    }
}