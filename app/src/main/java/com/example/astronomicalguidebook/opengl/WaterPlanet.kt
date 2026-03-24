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
import kotlin.math.*

class WaterPlanet(
    private val context: Context,
    private val size: Float
) {
    private val N = 80
    private val K = 0.06f
    private val DT = 0.1f
    private val damping = 0.99f

    private data class WavePoint(
        var x: Float = 0f,
        var y: Float = 0f,
        var z: Float = 0f,
        var vz: Float = 0f
    )

    private val waveGrid = Array(N) { Array(N) { WavePoint() } }
    private var vertexArray = FloatArray(12 * N * N)
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var normalBuffer: FloatBuffer
    private lateinit var indexBuffer: ShortBuffer
    private lateinit var textureBuffer: FloatBuffer
    private var textureId = 0

    private val slices = N
    private val stacks = N
    private var timeAcc = 0f

    init {
        initWaveGrid()
        generateMesh()
    }

    private fun sqr(x: Float): Float = x * x

    private fun initWaveGrid() {
        for (i in 0 until N) {
            for (j in 0 until N) {
                waveGrid[i][j].apply {
                    x = j.toFloat() / N
                    y = i.toFloat() / N
                    z = 0f
                    vz = 0f
                }
            }
        }
    }

    private fun generateMesh() {
        val vertexList = mutableListOf<Float>()
        val texCoordList = mutableListOf<Float>()
        val indexList = mutableListOf<Short>()

        val step = 2.0f / N
        val offset = -1.0f

        for (i in 0..stacks) {
            val x = offset + i.toFloat() * step
            for (j in 0..slices) {
                val z = offset + j.toFloat() * step
                vertexList.add(x)
                vertexList.add(0f)
                vertexList.add(z)
                texCoordList.add(j.toFloat() / slices)
                texCoordList.add(i.toFloat() / stacks)
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

        val normalList = mutableListOf<Float>()
        for (i in 0..stacks) {
            for (j in 0..slices) {
                normalList.add(0f)
                normalList.add(1f)
                normalList.add(0f)
            }
        }

        normalBuffer = ByteBuffer.allocateDirect(normalList.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        normalBuffer.put(normalList.toFloatArray())
        normalBuffer.position(0)
    }

    private fun pushWave() {
        if (Math.random() * 500 > 10) {
            return
        }
        val x0 = (Math.random() * N / 2 + 1).toInt()
        val y0 = (Math.random() * N / 2 + 1).toInt()
        for (y in y0 - 5 until y0 + 5) {
            if (y < 1 || y >= N - 1) continue
            for (x in x0 - 5 until x0 + 5) {
                if (x < 1 || x >= N - 1) continue
                val d = sqrt(sqr((y - y0).toFloat()) + sqr((x - x0).toFloat()))
                waveGrid[x][y].z = 10.0f / N - d / N
            }
        }
    }

    private fun updateWaves() {
        val dx = intArrayOf(-1, 0, 1, 0)
        val dy = intArrayOf(0, 1, 0, -1)

        pushWave()

        for (y in 1 until N - 1) {
            for (x in 1 until N - 1) {
                val p0 = waveGrid[x][y]
                for (i in 0..3) {
                    val p1 = waveGrid[x + dx[i]][y + dy[i]]
                    val d = sqrt(sqr(p0.x - p1.x) + sqr(p0.y - p1.y) + sqr(p0.z - p1.z))
                    if (d > 0.001f) {
                        p0.vz += K * (p1.z - p0.z) / d * DT
                    }
                }
                p0.vz *= damping
            }
        }

        for (y in 1 until N - 1) {
            for (x in 1 until N - 1) {
                val p0 = waveGrid[x][y]
                p0.z += p0.vz
                if (p0.z > 0.15f) p0.z = 0.15f
                if (p0.z < -0.15f) p0.z = -0.15f
            }
        }
    }

    private fun updateVerticesAndNormals() {
        val step = 2.0f / N
        val offset = -1.0f
        val vertexList = mutableListOf<Float>()
        val normalList = mutableListOf<Float>()

        for (i in 0..stacks) {
            val x = offset + i.toFloat() * step
            for (j in 0..slices) {
                val z = offset + j.toFloat() * step

                val gu = (i.toFloat() / stacks * (N - 1)).toInt().coerceIn(0, N - 1)
                val gv = (j.toFloat() / slices * (N - 1)).toInt().coerceIn(0, N - 1)
                val y = waveGrid[gu][gv].z * 0.8f

                vertexList.add(x)
                vertexList.add(y)
                vertexList.add(z)

                val dx = 0.05f
                val dz = 0.05f
                val hx = (getWaveHeight(x + dx, z) - getWaveHeight(x - dx, z)) / (2 * dx)
                val hz = (getWaveHeight(x, z + dz) - getWaveHeight(x, z - dz)) / (2 * dz)

                var nx = -hx
                var ny = 1f
                var nz = -hz

                val len = sqrt(nx * nx + ny * ny + nz * nz)
                if (len > 0) {
                    nx /= len
                    ny /= len
                    nz /= len
                }

                normalList.add(nx)
                normalList.add(ny)
                normalList.add(nz)
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
    }

    private fun getWaveHeight(x: Float, z: Float): Float {
        val step = 2.0f / N
        val offset = -1.0f
        val i = ((x - offset) / step).toInt().coerceIn(0, N - 1)
        val j = ((z - offset) / step).toInt().coerceIn(0, N - 1)
        return waveGrid[i][j].z * 0.8f
    }

    fun loadTexture(gl: GL10) {
        val textures = IntArray(1)
        gl.glGenTextures(1, textures, 0)
        textureId = textures[0]

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId)

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT.toFloat())

        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.neptune)
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
    }

    fun draw(gl: GL10) {
        updateWaves()
        updateVerticesAndNormals()

        gl.glPushMatrix()
        gl.glScalef(size, size, size)

        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId)

        gl.glEnable(GL10.GL_LIGHTING)
        gl.glEnable(GL10.GL_LIGHT0)

        val lightPos = floatArrayOf(3.0f, 5.0f, 3.0f, 1.0f)
        val lightAmbient = floatArrayOf(0.2f, 0.2f, 0.3f, 1.0f)
        val lightDiffuse = floatArrayOf(0.5f, 0.7f, 1.0f, 1.0f)

        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0)

        val matAmbient = floatArrayOf(0.2f, 0.4f, 0.7f, 1.0f)
        val matDiffuse = floatArrayOf(0.3f, 0.6f, 0.9f, 1.0f)

        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0)
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0)

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer)

        gl.glDrawElements(GL10.GL_TRIANGLES, indexBuffer.capacity(), GL10.GL_UNSIGNED_SHORT, indexBuffer)

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY)
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

        gl.glDisable(GL10.GL_LIGHT0)
        gl.glDisable(GL10.GL_LIGHTING)
        gl.glDisable(GL10.GL_TEXTURE_2D)

        gl.glPopMatrix()
    }
}