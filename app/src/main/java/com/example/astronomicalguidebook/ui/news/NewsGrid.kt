package com.example.astronomicalguidebook.ui.news

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.astronomicalguidebook.data.Advertisement
import com.example.astronomicalguidebook.data.News

@Composable
fun NewsGrid(
    newsList: List<Any>,
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
                when (val item = newsList[0]) {
                    is News -> NewsItem(
                        news = item,
                        onLikeClick = { onLikeClick(item.id) },
                        modifier = Modifier.weight(1f)
                    )
                    is Advertisement -> AdvertisementItem(
                        advertisement = item,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (newsList.size > 1) {
                when (val item = newsList[1]) {
                    is News -> NewsItem(
                        news = item,
                        onLikeClick = { onLikeClick(item.id) },
                        modifier = Modifier.weight(1f)
                    )
                    is Advertisement -> AdvertisementItem(
                        advertisement = item,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (newsList.size > 2) {
                when (val item = newsList[2]) {
                    is News -> NewsItem(
                        news = item,
                        onLikeClick = { onLikeClick(item.id) },
                        modifier = Modifier.weight(1f)
                    )
                    is Advertisement -> AdvertisementItem(
                        advertisement = item,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (newsList.size > 3) {
                when (val item = newsList[3]) {
                    is News -> NewsItem(
                        news = item,
                        onLikeClick = { onLikeClick(item.id) },
                        modifier = Modifier.weight(1f)
                    )
                    is Advertisement -> AdvertisementItem(
                        advertisement = item,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}