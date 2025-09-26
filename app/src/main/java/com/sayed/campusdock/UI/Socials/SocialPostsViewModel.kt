package com.sayed.campusdock.UI.Socials

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.Data.Socials.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class SocialPostsViewModel : ViewModel() {

	private val _allPosts = MutableLiveData<List<Post>>(emptyList())
	val allPosts: LiveData<List<Post>> = _allPosts

	private val _trendingPosts = MutableLiveData<List<Post>>(emptyList())
	val trendingPosts: LiveData<List<Post>> = _trendingPosts

	private val _isLoading = MutableLiveData(false)
	val isLoading: LiveData<Boolean> = _isLoading

	private val _errorMessage = MutableLiveData<String?>(null)
	val errorMessage: LiveData<String?> = _errorMessage

	fun ensureAllPosts(collegeId: UUID) {
		if (_allPosts.value.isNullOrEmpty()) {
			refreshAllPosts(collegeId)
		}
	}

	fun ensureTrendingPosts(collegeId: UUID) {
		if (_trendingPosts.value.isNullOrEmpty()) {
			refreshTrendingPosts(collegeId)
		}
	}

	fun refreshAllPosts(collegeId: UUID) {
		_isLoading.postValue(true)
		_errorMessage.postValue(null)
		viewModelScope.launch(Dispatchers.IO) {
			try {
				val response = RetrofitClient.instance.getAllPostsByCollegeId(collegeId)
				if (response.isSuccessful) {
					_allPosts.postValue(response.body() ?: emptyList())
				} else {
					_errorMessage.postValue("Failed to fetch posts: ${response.code()}")
				}
			} catch (t: Throwable) {
				_errorMessage.postValue(t.message ?: "Unknown error")
			} finally {
				_isLoading.postValue(false)
			}
		}
	}

	fun refreshTrendingPosts(collegeId: UUID) {
		_isLoading.postValue(true)
		_errorMessage.postValue(null)
		viewModelScope.launch(Dispatchers.IO) {
			try {
				val response = RetrofitClient.instance.getAllTrendingPostsByCollegeId(collegeId)
				if (response.isSuccessful) {
					_trendingPosts.postValue(response.body() ?: emptyList())
				} else {
					_errorMessage.postValue("Failed to fetch trending: ${response.code()}")
				}
			} catch (t: Throwable) {
				_errorMessage.postValue(t.message ?: "Unknown error")
			} finally {
				_isLoading.postValue(false)
			}
		}
	}
}


