package com.example.astronomicalguidebook.ui.news

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.astronomicalguidebook.ui.theme.AstronomicalGuidebookTheme
import com.example.astronomicalguidebook.data.NewsData

@Composable
fun NewsScreen(
    viewModel: NewsViewModel = viewModel()
) {
    val displayedNews by viewModel.displayedNews.collectAsState()

    AstronomicalGuidebookTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = "НОВОСТИ",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                )

                Text(
                    text = "Новости обновляются каждые 5 секунд",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                )

                if (displayedNews.isNotEmpty()) {
                    NewsGrid(
                        newsList = displayedNews,
                        onLikeClick = { newsId ->
                            viewModel.likeNews(newsId)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(680.dp)
                            .padding(horizontal = 8.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Загрузка новостей...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Text(
                    text = "СТАВЬТЕ ЛАЙКИ 👍",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 24.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 16.dp)
                )

                Text(
                    text = "Всего новостей: ${NewsData.newsList.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}