package com.sayed.campusdock.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sayed.campusdock.API.RetrofitClient
import com.sayed.campusdock.Data.Marketplace.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MarketplaceUiState {
    object Loading : MarketplaceUiState()
    object Success : MarketplaceUiState()
    object Empty : MarketplaceUiState()
    data class Error(val message: String) : MarketplaceUiState()
}

class MarketplaceViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _uiState = MutableStateFlow<MarketplaceUiState>(MarketplaceUiState.Loading)
    val uiState: StateFlow<MarketplaceUiState> = _uiState.asStateFlow()

    private var currentPage = 0
    private val pageSize = 10
    private var isLastPage = false
    private var isLoading = false

    fun loadProducts(collegeId: String?, userId: String? = null) {
        if (isLoading || isLastPage) return

        viewModelScope.launch {
            isLoading = true
            if (currentPage == 0) _uiState.value = MarketplaceUiState.Loading

            try {
                val response = RetrofitClient.instance.getProducts(
                    page = currentPage,
                    size = pageSize,
                    collegeId = collegeId,
                    userId = userId
                )

                val mapped = response.content.map { dto ->
                    Product(
                        id = null, // backend ProductDto currently lacks id
                        name = dto.name,
                        price = "₹${dto.price.toInt()}",
                        imageUrl = dto.urls?.firstOrNull(),
                        sellerName = dto.userName,
                        description = dto.description
                    )
                }

                _products.value = _products.value + mapped
                isLastPage = response.last ?: (mapped.isEmpty())
                currentPage++

                _uiState.value = if (_products.value.isEmpty()) {
                    MarketplaceUiState.Empty
                } else {
                    MarketplaceUiState.Success
                }

                Log.d("MARKETPLACE_VM", "Loaded ${mapped.size} products, total ${_products.value.size}")
            } catch (e: Exception) {
                Log.e("MARKETPLACE_VM", "Failed to load products: ${e.message}")
                if (_products.value.isEmpty()) {
                    _uiState.value = MarketplaceUiState.Error(e.message ?: "Failed to load products")
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun refresh(collegeId: String?, userId: String? = null) {
        currentPage = 0
        isLastPage = false
        _products.value = emptyList()
        loadProducts(collegeId, userId)
    }
}
