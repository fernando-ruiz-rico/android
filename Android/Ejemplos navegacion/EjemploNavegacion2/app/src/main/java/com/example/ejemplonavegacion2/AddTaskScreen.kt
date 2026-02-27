package com.example.ejemplonavegacion2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.ZoneId
import java.time.Instant

@Composable
fun AddTaskScreen(listaTareas: MutableList<Tarea> = mutableListOf(), goBack: () -> Unit) {
  val descState = rememberTextFieldState()
  var selectedDate by remember { mutableStateOf<Long?>(null) }
  var showDatePicker by remember { mutableStateOf(false) }
  val taskDate = remember {
    derivedStateOf {
      selectedDate?.let {
        Instant.ofEpochMilli(selectedDate!!).atZone(ZoneId.systemDefault()).toLocalDate()
      } ?: LocalDate.now()
    }
  }

  Scaffold() { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text("Añadir Tarea", style = MaterialTheme.typography.headlineMedium)
      HorizontalDivider()
      OutlinedTextField(
        state = descState,
        label = { Text("Descripción") },
        placeholder = { Text("Descripción de la tarea") })
      TextButton(onClick = { showDatePicker = true }) {
        Text("Seleccionar fecha")
      }
      selectedDate?.let { date ->
        Text(taskDate.value.toString())
      }
      if (showDatePicker) {
        DatePickerModal(onDateSelected = {
          selectedDate = it
          showDatePicker = false
        }, onDismiss = {
          showDatePicker = false
        })
      }
      HorizontalDivider()
      Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(onClick = {
          listaTareas.add(
            Tarea(
              descState.text.toString(),
              taskDate.value,
              false
            )
          )
          goBack()
        }) {
          Text("Guardar")
        }
        Button(
          onClick = {
            goBack()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Cancelar")
        }
      }
    }
  }
}

@Composable
fun DatePickerModal(
  onDateSelected: (Long?) -> Unit,
  onDismiss: () -> Unit
) {
  val datePickerState = rememberDatePickerState()

  DatePickerDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(onClick = {
        onDateSelected(datePickerState.selectedDateMillis)
        onDismiss()
      }) {
        Text("OK")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  ) {
    DatePicker(state = datePickerState)
  }
}
