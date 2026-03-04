package com.example.astronomicalguidebook.opengl

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10

class SelectorCube {

    private val vertices = floatArrayOf(
        -1.2f, -1.2f, -1.2f,
        1.2f, -1.2f, -1.2f,
        1.2f, 1.2f, -1.2f,
        -1.2f, 1.2f, -1.2f,

        -1.2f, -1.2f, 1.2f,
        1.2f, -1.2f, 1.2f,
        1.2f, 1.2f, 1.2f,
        -1.2f, 1.2f, 1.2f
    )

    private val indices = shortArrayOf(
        0, 1, 2, 0, 2, 3,
        4, 5, 6, 4, 6, 7,
        0, 1, 5, 0, 5, 4,
        3, 2, 6, 3, 6, 7,
        0, 3, 7, 0, 7, 4,
        1, 2, 6, 1, 6, 5
    )

    private var vertexBuffer: FloatBuffer
    private var indexBuffer: ShortBuffer

    init {
        val vbb = ByteBuffer.allocateDirect(vertices.size * 4)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)

        val ibb = ByteBuffer.allocateDirect(indices.size * 2)
        ibb.order(ByteOrder.nativeOrder())
        indexBuffer = ibb.asShortBuffer()
        indexBuffer.put(indices)
        indexBuffer.position(0)
    }

    fun draw(gl: GL10) {
        gl.glPushMatrix()

        gl.glEnable(GL10.GL_BLEND)
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)

        gl.glColor4f(1.0f, 1.0f, 1.0f, 0.3f)

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)

        gl.glDrawElements(GL10.GL_LINES, indices.size, GL10.GL_UNSIGNED_SHORT, indexBuffer)

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.size, GL10.GL_UNSIGNED_SHORT, indexBuffer)

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisable(GL10.GL_BLEND)

        gl.glPopMatrix()
    }
}
