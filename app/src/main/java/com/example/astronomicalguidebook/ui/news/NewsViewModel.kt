package com.example.astronomicalguidebook.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.astronomicalguidebook.data.News
import com.example.astronomicalguidebook.data.NewsData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    private val _newsLikes = mutableMapOf<Int, Int>()

    private val _displayedNews = MutableStateFlow<List<News>>(emptyList())
    val displayedNews: StateFlow<List<News>> = _displayedNews.asStateFlow()

    private val _isNewsDialogVisible = MutableStateFlow(true)
    val isNewsDialogVisible: StateFlow<Boolean> = _isNewsDialogVisible.asStateFlow()

    private val allNews = NewsData.newsList

    private var rotationJob: Job? = null

    init {
        initializeNews()
        startNewsRotation()
    }

    private fun initializeNews() {

        val randomNews = allNews.shuffled().take(4).map { news ->
            news.copy(likes = _newsLikes[news.id] ?: 0)
        }
        _displayedNews.value = randomNews
    }

    private fun startNewsRotation() {
        rotationJob = viewModelScope.launch {
            while (true) {
                delay(5000)
                rotateRandomNews()
            }
        }
    }

    private fun rotateRandomNews() {
        val currentNews = _displayedNews.value.toMutableList()

        val displayedNewsIds = currentNews.map { it.id }
        val availableNews = allNews.filter { it.id !in displayedNewsIds }

        if (availableNews.isNotEmpty()) {
            val positionToReplace = (0 until 4).random()

            val newNews = availableNews.random()

            val savedLikes = _newsLikes[newNews.id] ?: 0

            currentNews[positionToReplace] = newNews.copy(likes = savedLikes)

            _displayedNews.value = currentNews
        }
    }

    fun likeNews(newsId: Int) {
        val currentLikes = _newsLikes[newsId] ?: 0
        _newsLikes[newsId] = currentLikes + 1

        val updatedNews = _displayedNews.value.map { news ->
            if (news.id == newsId) {
                news.copy(likes = news.likes + 1)
            } else {
                news
            }
        }
        _displayedNews.value = updatedNews
    }

    fun closeNewsDialog() {
        _isNewsDialogVisible.value = false
    }

    fun showNewsDialog() {
        _isNewsDialogVisible.value = true
    }

    override fun onCleared() {
        super.onCleared()
        rotationJob?.cancel()
    }
}