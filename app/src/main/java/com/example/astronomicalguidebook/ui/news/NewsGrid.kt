package com.example.astronomicalguidebook.ui.news

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.astronomicalguidebook.data.News

@Composable
fun NewsGrid(
    newsList: List<News>,
    onLikeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (newsList.size > 0) {
                NewsItem(
                    news = newsList[0],
                    onLikeClick = { onLikeClick(newsList[0].id) },
                    modifier = Modifier.weight(1f)
                )
            }

            if (newsList.size > 1) {
                NewsItem(
                    news = newsList[1],
                    onLikeClick = { onLikeClick(newsList[1].id) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (newsList.size > 2) {
                NewsItem(
                    news = newsList[2],
                    onLikeClick = { onLikeClick(newsList[2].id) },
                    modifier = Modifier.weight(1f)
                )
            }

            if (newsList.size > 3) {
                NewsItem(
                    news = newsList[3],
                    onLikeClick = { onLikeClick(newsList[3].id) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}