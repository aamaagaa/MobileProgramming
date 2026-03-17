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

class MoonModel(private val context: Context) {

    private val slices = 32
    private val stacks = 32

    private var vertexBuffer: FloatBuffer
    private var normalBuffer: FloatBuffer
    private var textureBuffer: FloatBuffer
    private var indexBuffer: ShortBuffer
    private var textureId = 0

    init {
        val vertexList = mutableListOf<Float>()
        val normalList = mutableListOf<Float>()
        val texCoordList = mutableListOf<Float>()
        val indexList = mutableListOf<Short>()

        for (i in 0..stacks) {
            val theta = i * Math.PI / stacks
            val sinTheta = Math.sin(theta).toFloat()
            val cosTheta = Math.cos(theta).toFloat()

            val v = i.toFloat() / stacks

            for (j in 0..slices) {
                val phi = j * 2 * Math.PI / slices
                val sinPhi = Math.sin(phi).toFloat()
                val cosPhi = Math.cos(phi).toFloat()

                val x = cosPhi * sinTheta
                val y = cosTheta
                val z = sinPhi * sinTheta

                vertexList.add(x)
                vertexList.add(y)
                vertexList.add(z)

                normalList.add(x)
                normalList.add(y)
                normalList.add(z)

                val u = j.toFloat() / slices
                texCoordList.add(u)
                texCoordList.add(v)
            }
        }

        for (i in 0 until stacks) {
            for (j in 0 until slices) {
                val first = (i * (slices + 1) + j).toShort()
                val second = (i * (slices + 1) + j + 1).toShort()
                val third = ((i + 1) * (slices + 1) + j).toShort()
                val fourth = ((i + 1) * (slices + 1) + j + 1).toShort()

                indexList.add(first)
                indexList.add(second)
                indexList.add(third)

                indexList.add(second)
                indexList.add(fourth)
                indexList.add(third)
            }
        }

        vertexBuffer = ByteBuffer.allocateDirect(vertexList.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(vertexList.toFloatArray())
        vertexBuffer.position(0)

        normalBuffer = ByteBuffer.allocateDirect(normalList.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        normalBuffer.put(normalList.toFloatArray())
        normalBuffer.position(0)

        textureBuffer = ByteBuffer.allocateDirect(texCoordList.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        textureBuffer.put(texCoordList.toFloatArray())
        textureBuffer.position(0)

        indexBuffer = ByteBuffer.allocateDirect(indexList.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
        indexBuffer.put(indexList.toShortArray())
        indexBuffer.position(0)
    }

    fun loadTexture(gl: GL10) {
        val textures = IntArray(1)
        gl.glGenTextures(1, textures, 0)
        textureId = textures[0]

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId)

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())

        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.moon)
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
    }

    fun draw(gl: GL10) {
        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId)

        val ambient = floatArrayOf(0.3f, 0.3f, 0.3f, 1.0f)
        val diffuse = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
        val specular = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
        val shininess = 96.0f

        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, ambient, 0)
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, diffuse, 0)
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, specular, 0)
        gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, shininess)

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer)

        gl.glDrawElements(
            GL10.GL_TRIANGLES,
            indexBuffer.capacity(),
            GL10.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY)
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

        gl.glDisable(GL10.GL_TEXTURE_2D)
    }
}
