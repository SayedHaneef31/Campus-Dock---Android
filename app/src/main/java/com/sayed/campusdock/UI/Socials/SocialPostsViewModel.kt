package com.sayed.campusdock.UI.Socials

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.Data.Socials.Post
import com.sayed.campusdock.Data.Socials.VoteType
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

	fun voteOnPost(postId: UUID, userId: UUID, voteType: VoteType, collegeId: UUID? = null) {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				val voteTypeString = voteType.name // Convert to UPVOTE or DOWNVOTE string
				val response = RetrofitClient.instance.voteOnPost(postId, userId, voteTypeString)
				if (response.isSuccessful) {
					_errorMessage.postValue(null)
					// Don't refresh entire list - let adapter handle UI updates
					// Backend sync can happen in background later
				} else {
					_errorMessage.postValue("Failed to vote: ${response.code()}")
				}
			} catch (t: Throwable) {
				_errorMessage.postValue(t.message ?: "Unknown error while voting")
			}
		}
	}

	fun addPostToTop(post: Post) {
		val currentPosts = _allPosts.value ?: emptyList()
		val updatedPosts = listOf(post) + currentPosts
		_allPosts.postValue(updatedPosts)

		// Also add to trending posts
		val currentTrendingPosts = _trendingPosts.value ?: emptyList()
		val updatedTrendingPosts = listOf(post) + currentTrendingPosts
		_trendingPosts.postValue(updatedTrendingPosts)
	}

	fun addPostResponseToTop(postResponse: com.sayed.campusdock.Data.Socials.PostResponse) {
		// Convert PostResponse to Post
		val post = Post(
			id = postResponse.id,
			title = postResponse.title,
			content = postResponse.content,
			imageUrl = postResponse.imageUrl,
			authorName = postResponse.authorName,
			authorAnonymousName = postResponse.authorAnonymousName,
			authorId = postResponse.authorId ?: java.util.UUID.randomUUID(), // Use a placeholder UUID if null
			topicName = postResponse.topicName,
			isAnonymous = postResponse.isAnonymous,
			upvoteCount = postResponse.upvoteCount,
			downvoteCount = postResponse.downvoteCount,
			commentCount = postResponse.commentCount,
			createdAt = postResponse.createdAt ?: java.time.LocalDateTime.now() // Use current time if null
		)
		addPostToTop(post)
	}
}


