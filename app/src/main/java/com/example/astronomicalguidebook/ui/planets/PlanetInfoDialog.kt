package com.example.astronomicalguidebook.ui.planets

import android.opengl.GLSurfaceView
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.astronomicalguidebook.data.PlanetInfo
import com.example.astronomicalguidebook.opengl.WaterPlanet
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@Composable
fun PlanetInfoDialog(
    planetInfo: PlanetInfo,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (planetInfo.name == "Нептун") {
                    AndroidView(
                        factory = { ctx ->
                            GLSurfaceView(ctx).apply {
                                setEGLContextClientVersion(1)
                                layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )

                                val waterPlanet = WaterPlanet(ctx, 1.0f)

                                setRenderer(object : GLSurfaceView.Renderer {
                                    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
                                        gl.glClearColor(0.05f, 0.1f, 0.2f, 1.0f)
                                        gl.glEnable(GL10.GL_DEPTH_TEST)
                                        gl.glEnable(GL10.GL_LIGHTING)
                                        gl.glEnable(GL10.GL_LIGHT0)
                                        waterPlanet.loadTexture(gl)
                                    }

                                    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
                                        gl.glViewport(0, 0, width, height)

                                        gl.glMatrixMode(GL10.GL_PROJECTION)
                                        gl.glLoadIdentity()

                                        val aspect = width.toFloat() / height.toFloat()
                                        gl.glFrustumf(-aspect, aspect, -1f, 1f, 2f, 15f)

                                        gl.glMatrixMode(GL10.GL_MODELVIEW)
                                        gl.glLoadIdentity()
                                    }

                                    override fun onDrawFrame(gl: GL10) {
                                        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

                                        gl.glMatrixMode(GL10.GL_MODELVIEW)
                                        gl.glLoadIdentity()

                                        gl.glTranslatef(0f, -0.2f, -3.5f)
                                        gl.glRotatef(45f, 1f, 0f, 0f)
                                        gl.glRotatef(30f, 0f, 1f, 0f)

                                        waterPlanet.draw(gl)
                                    }
                                })

                                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                            }
                        },
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Image(
                        painter = painterResource(id = planetInfo.imageResId),
                        contentDescription = planetInfo.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = planetInfo.name,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = planetInfo.description,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Закрыть")
                }
            }
        }
    }
}