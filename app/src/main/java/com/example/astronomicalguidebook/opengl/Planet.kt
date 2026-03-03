package com.example.astronomicalguidebook.opengl

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10

class Planet(
    private val size: Float,
    private val red: Float,
    private val green: Float,
    private val blue: Float
) {
    private val slices = 16
    private val stacks = 16

    private val vertices: FloatBuffer
    private val colors: FloatBuffer
    private val indices: ShortBuffer

    init {
        val vertexList = mutableListOf<Float>()
        val colorList = mutableListOf<Float>()
        val indexList = mutableListOf<Short>()

        for (i in 0..stacks) {
            val theta = i * Math.PI / stacks
            val sinTheta = Math.sin(theta).toFloat()
            val cosTheta = Math.cos(theta).toFloat()
            for (j in 0..slices) {
                val phi = j * 2 * Math.PI / slices
                val sinPhi = Math.sin(phi).toFloat()
                val cosPhi = Math.cos(phi).toFloat()

                vertexList.add(cosPhi * sinTheta)
                vertexList.add(cosTheta)
                vertexList.add(sinPhi * sinTheta)

                colorList.add(red)
                colorList.add(green)
                colorList.add(blue)
                colorList.add(1f)
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

        vertices = ByteBuffer.allocateDirect(vertexList.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(vertexList.toFloatArray())
            position(0)
        }
        colors = ByteBuffer.allocateDirect(colorList.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(colorList.toFloatArray())
            position(0)
        }
        indices = ByteBuffer.allocateDirect(indexList.size * 2).order(ByteOrder.nativeOrder()).asShortBuffer().apply {
            put(indexList.toShortArray())
            position(0)
        }
    }

    fun draw(gl: GL10) {
        gl.glScalef(size, size, size)

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices)
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colors)

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.capacity(), GL10.GL_UNSIGNED_SHORT, indices)

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY)
    }
}