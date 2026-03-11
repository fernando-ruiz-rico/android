package com.example.ejemploproductos.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ejemploproductos.Link
import com.example.ejemploproductos.ProductsApplication
import com.example.ejemploproductos.db.products.Product
import com.example.ejemploproductos.db.products.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class DetalleProductoViewModel(
  private val route: Link.ProductDetail,
  private val repository: ProductRepository,
): ViewModel() {
  val product: StateFlow<Product?> = repository.getById(route.id)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
