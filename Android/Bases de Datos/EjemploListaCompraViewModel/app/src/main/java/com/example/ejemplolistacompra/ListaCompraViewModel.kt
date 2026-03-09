package com.example.ejemplolistacompra

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle

class ListaCompraViewModel(
  private val itemRepository: ItemRepository,
  private val savedStateHandle: SavedStateHandle // Estado guardado (giro pantalla, etc)
) : ViewModel() {
  val listaItems: StateFlow<List<Item>> = itemRepository.getAll()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun insert(item: Item) {
    viewModelScope.launch {
      itemRepository.insert(item)
    }
  }

  fun delete(item: Item) {
    viewModelScope.launch {
      itemRepository.delete(item)
    }
  }

  companion object {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
      initializer {
        val application = this[APPLICATION_KEY] as MyApplication
        val repository = application.itemRepository
        ListaCompraViewModel(repository, createSavedStateHandle())
      }
    }
  }
}
