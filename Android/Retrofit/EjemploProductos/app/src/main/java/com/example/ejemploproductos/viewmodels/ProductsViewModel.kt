package com.example.ejemploproductos.viewmodels

import androidx.lifecycle.ViewModel
import com.example.ejemploproductos.model.Product
import com.example.ejemploproductos.repositories.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.android.annotation.KoinViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

sealed class ProductUiState {
  object Loading : ProductUiState()
  data class Success(val products: List<Product>) : ProductUiState()
  data class Error(val message: String) : ProductUiState()
}

@KoinViewModel
class ProductsViewModel(private val productRepository: ProductRepository) : ViewModel() {
  private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
  val uiState: StateFlow<ProductUiState> = _uiState // Propiedad de solo lectura

  // Carga inicial
  init {
    getProducts()
  }

  fun deleteEquipo(id: Int) {
    viewModelScope.launch {
      try {
        productRepository.deleteProduct(id)
        _uiState.value =
          ProductUiState.Success((_uiState.value as ProductUiState.Success).products.filter { it.id != id })
      } catch (e: Exception) {
        _uiState.value = ProductUiState.Error(e.message ?: "Error desconocido")
      }
    }
  }

  private fun getProducts() {
    viewModelScope.launch {
      _uiState.value = ProductUiState.Loading

      try {
        val response = productRepository.getProducts()
        _uiState.value = ProductUiState.Success(response.products)
      } catch (e: Exception) {
        _uiState.value = ProductUiState.Error(e.message ?: "Error desconocido")
      }
    }
  }
}
