package com.example.ejemploproductos.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ejemploproductos.ProductsApplication
import com.example.ejemploproductos.db.products.Product
import com.example.ejemploproductos.db.products.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DetalleProductoViewModel(
  private val productId: Int,
  private val repository: ProductRepository,
  private val savedStateHandle: SavedStateHandle
): ViewModel() {
  val product: StateFlow<Product?> = repository.getById(productId)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  companion object {
    fun Factory(application: ProductsApplication, id: Int): ViewModelProvider.Factory = viewModelFactory {
      initializer {
        val repository = application.productRepository
        DetalleProductoViewModel(id, repository, createSavedStateHandle())
      }
    }
  }
}
