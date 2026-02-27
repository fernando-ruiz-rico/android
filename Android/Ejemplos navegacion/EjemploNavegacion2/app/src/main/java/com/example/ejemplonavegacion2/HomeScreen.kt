package com.example.ejemplonavegacion2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(listaTareas: MutableList<Tarea> = mutableListOf(), navigateAddTask: () -> Unit) {
  Scaffold(
    modifier = Modifier.fillMaxSize(),
    floatingActionButton = {
      FloatingActionButton(onClick = navigateAddTask, shape = CircleShape) {
        Icon(Icons.Default.Add, contentDescription = "Add")
      }
    }
  ) { innerPadding ->
    LazyColumn(modifier = Modifier.fillMaxWidth().padding(innerPadding)) {
      itemsIndexed(listaTareas) { index, tarea ->
        ListItem(
          modifier = Modifier.fillMaxWidth(),
          headlineContent = {
            Column() {
              Text(
                tarea.description,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (tarea.finished) TextDecoration.LineThrough else null
              )
              Text(
                tarea.date.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
              )
            }
          },
          trailingContent = {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
              Switch(
                checked = tarea.finished,
                onCheckedChange = { listaTareas[index] = tarea.copy(finished = it) })
              IconButton(onClick = { listaTareas.removeAt(index) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
              }
            }
          })
      }
    }
  }

}
