package com.example.astronomicalguidebook.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.astronomicalguidebook.data.Advertisement
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

    private val _displayedNews = MutableStateFlow<List<Any>>(emptyList())
    val displayedNews: StateFlow<List<Any>> = _displayedNews.asStateFlow()

    private val _isNewsDialogVisible = MutableStateFlow(true)
    val isNewsDialogVisible: StateFlow<Boolean> = _isNewsDialogVisible.asStateFlow()

    private val allNews = NewsData.newsList
    private val allAds = NewsData.advertisements

    private var rotationJob: Job? = null

    init {
        initializeNews()
        startNewsRotation()
    }

    private fun initializeNews() {
        val allItems = allNews + allAds
        val randomItems = allItems.shuffled().take(4).map { item ->
            when (item) {
                is News -> item.copy(likes = _newsLikes[item.id] ?: 0)
                else -> item
            }
        }
        _displayedNews.value = randomItems
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
        val currentItems = _displayedNews.value.toMutableList()

        val displayedIds = currentItems.mapNotNull { item ->
            when (item) {
                is News -> item.id
                is Advertisement -> item.id
                else -> null
            }
        }

        val allItems = allNews + allAds
        val availableItems = allItems.filter { item ->
            val itemId = when (item) {
                is News -> item.id
                is Advertisement -> item.id
                else -> -1
            }
            itemId !in displayedIds
        }

        if (availableItems.isNotEmpty()) {
            val positionToReplace = (0 until 4).random()
            val newItem = availableItems.random()

            val updatedItem = when (newItem) {
                is News -> newItem.copy(likes = _newsLikes[newItem.id] ?: 0)
                else -> newItem
            }

            currentItems[positionToReplace] = updatedItem
            _displayedNews.value = currentItems
        }
    }

    fun likeNews(newsId: Int) {
        val currentLikes = _newsLikes[newsId] ?: 0
        _newsLikes[newsId] = currentLikes + 1

        val updatedItems = _displayedNews.value.map { item ->
            if (item is News && item.id == newsId) {
                item.copy(likes = item.likes + 1)
            } else {
                item
            }
        }
        _displayedNews.value = updatedItems
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