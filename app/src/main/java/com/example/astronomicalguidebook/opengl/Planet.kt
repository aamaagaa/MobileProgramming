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

class TexturedPlanet(
    private val context: Context,
    private val size: Float,
    private val textureResId: Int  // ID ресурса текстуры
) {
    // Параметры сферы
    private val slices = 32  // Больше сегментов для гладкой текстуры
    private val stacks = 32

    private var vertexBuffer: FloatBuffer
    private var textureBuffer: FloatBuffer
    private var indexBuffer: ShortBuffer
    private var textureId = 0

    init {
        // 1. Генерация вершин
        val vertexList = mutableListOf<Float>()
        val texCoordList = mutableListOf<Float>()
        val indexList = mutableListOf<Short>()

        for (i in 0..stacks) {
            val theta = i * Math.PI / stacks  // от 0 до PI
            val sinTheta = Math.sin(theta).toFloat()
            val cosTheta = Math.cos(theta).toFloat()

            // Текстурная координата V (вертикаль)
            val v = i.toFloat() / stacks

            for (j in 0..slices) {
                val phi = j * 2 * Math.PI / slices  // от 0 до 2PI
                val sinPhi = Math.sin(phi).toFloat()
                val cosPhi = Math.cos(phi).toFloat()

                // Позиция вершины
                val x = cosPhi * sinTheta
                val y = cosTheta
                val z = sinPhi * sinTheta

                vertexList.add(x)
                vertexList.add(y)
                vertexList.add(z)

                // Текстурные координаты (U, V)
                val u = j.toFloat() / slices
                texCoordList.add(u)  // U (горизонталь)
                texCoordList.add(v)  // V (вертикаль)
            }
        }

        // Индексы для треугольников
        for (i in 0 until stacks) {
            for (j in 0 until slices) {
                val first = (i * (slices + 1) + j).toShort()
                val second = (i * (slices + 1) + j + 1).toShort()
                val third = ((i + 1) * (slices + 1) + j).toShort()
                val fourth = ((i + 1) * (slices + 1) + j + 1).toShort()

                // Первый треугольник
                indexList.add(first)
                indexList.add(second)
                indexList.add(third)

                // Второй треугольник
                indexList.add(second)
                indexList.add(fourth)
                indexList.add(third)
            }
        }

        // Буфер вершин
        vertexBuffer = ByteBuffer.allocateDirect(vertexList.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer.put(vertexList.toFloatArray())
        vertexBuffer.position(0)

        // Буфер текстурных координат
        textureBuffer = ByteBuffer.allocateDirect(texCoordList.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        textureBuffer.put(texCoordList.toFloatArray())
        textureBuffer.position(0)

        // Буфер индексов
        indexBuffer = ByteBuffer.allocateDirect(indexList.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
        indexBuffer.put(indexList.toShortArray())
        indexBuffer.position(0)
    }

    fun loadTexture(gl: GL10) {
        // Генерируем ID текстуры
        val textures = IntArray(1)
        gl.glGenTextures(1, textures, 0)
        textureId = textures[0]

        // Биндим текстуру
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId)

        // Настройка параметров текстуры
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT.toFloat())
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT.toFloat())

        // Загружаем изображение
        val bitmap = BitmapFactory.decodeResource(context.resources, textureResId)
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
    }

    fun draw(gl: GL10) {
        gl.glPushMatrix()
        gl.glScalef(size, size, size)

        // Включаем текстурирование
        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId)

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer)

        gl.glDrawElements(
            GL10.GL_TRIANGLES,
            indexBuffer.capacity(),
            GL10.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
        gl.glDisable(GL10.GL_TEXTURE_2D)

        gl.glPopMatrix()
    }
}
