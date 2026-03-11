package com.example.ejemploproductos.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ejemploproductos.db.products.Product
import com.example.ejemploproductos.db.products.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import com.example.ejemploproductos.ProductsApplication
import kotlinx.coroutines.flow.StateFlow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ListaProductosViewModel(
  private val repository: ProductRepository, // ProductRepository lo inyecta automáticamente
  private val savedStateHandle: SavedStateHandle // SavedStateHandle lo inyecta automáticamente
) : ViewModel() {
  private val FILTRO_PRODUCTOS = "filtro_productos"
  val textoBusqueda: StateFlow<String> = savedStateHandle.getStateFlow(FILTRO_PRODUCTOS, "")

  val listaProductos: StateFlow<List<Product>> =
    repository.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun removeProduct(id: Int) {
    viewModelScope.launch {
      repository.deleteById(id)
    }
  }

  fun addProduct(product: Product) {
    viewModelScope.launch {
      repository.insert(product)
    }
  }
  fun actualizarFiltro(nuevoTexto: String) {
    savedStateHandle[FILTRO_PRODUCTOS] = nuevoTexto
  }
}
