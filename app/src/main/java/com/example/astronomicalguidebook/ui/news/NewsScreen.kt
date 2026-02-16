package com.example.astronomicalguidebook.ui.news

import android.opengl.GLSurfaceView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.astronomicalguidebook.ui.theme.AstronomicalGuidebookTheme
import com.example.astronomicalguidebook.data.NewsData

@Composable
fun NewsScreen(
    viewModel: NewsViewModel = viewModel()
) {
    val displayedNews by viewModel.displayedNews.collectAsState()
    val isDialogVisible by viewModel.isNewsDialogVisible.collectAsState()
    val context = LocalContext.current

    val glSurfaceView = remember {
        GLSurfaceView(context).apply {
            setEGLContextClientVersion(1)
            setRenderer(OpenGLRenderer(context))
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }
    }

    AstronomicalGuidebookTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if (!isDialogVisible) {
                    AndroidView(
                        factory = { glSurfaceView },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                if (isDialogVisible) {
                    NewsDialog(
                        onDismissRequest = { viewModel.closeNewsDialog() }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "Новости и реклама",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = "• Контент обновляется каждые 5 секунд\n• СТАВЬТЕ ЛАЙКИ новостям 👍\n• Закройте это окно, чтобы начать",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            if (displayedNews.isNotEmpty()) {
                                NewsGrid(
                                    newsList = displayedNews,
                                    onLikeClick = { newsId ->
                                        viewModel.likeNews(newsId)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(500.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "О приложении",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = "Это учебное приложение \"Астрономический справочник\". " +
                                                "Делали: Михальчич Елизавета, " +
                                                "Ничитайло Елизавета",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}