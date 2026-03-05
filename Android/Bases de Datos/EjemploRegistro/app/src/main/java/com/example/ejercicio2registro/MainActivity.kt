package com.example.ejercicio2registro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ejercicio2registro.ui.theme.Ejercicio2RegistroTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      Ejercicio2RegistroTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          MainScreen(Modifier.padding(innerPadding))
        }
      }
    }
  }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val db = AppDatabase.getDatabase(context)
  val userDao = db.userDao()
  val users = remember { mutableStateListOf<User>() }
  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(Unit) {
    users.addAll(userDao.getAll())
  }

  Column(modifier.fillMaxSize()) {
    FormularioRegistro(onAdd = { user ->
      coroutineScope.launch {
        val id = userDao.insert(user).toInt() // Insertamos en la base de datos
        users.add(user.copy(id = id))
      }
    })
    Spacer(Modifier.height(16.dp))
    UserList(users)
  }
}

@Composable
fun FormularioRegistro(modifier: Modifier = Modifier, onAdd: (User) -> Unit = {}) {
  val usernameState = rememberTextFieldState("")
  val passState = rememberTextFieldState("")
  var sexState by remember { mutableStateOf("Masculino") }

  Column(
    modifier = modifier
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text("Formulario de Registro", style = MaterialTheme.typography.headlineMedium)

    OutlinedTextField(
      state = usernameState,
      modifier = Modifier.fillMaxWidth(),
      lineLimits = TextFieldLineLimits.SingleLine,
      label = { Text("Nombre de usuario") })

    OutlinedSecureTextField(
      state = passState,
      modifier = Modifier.fillMaxWidth(),
      label = { Text("Contraseña") })

    SexButtons(sexState) { sexState = it }

    Button(onClick = {
      val user = User(username = usernameState.text.toString(), password = passState.text.toString(), sex = sexState)
      onAdd(user)
      // Reseteamos formulario
      usernameState.clearText()
      passState.clearText()
      sexState = "Masculino"
    }, modifier = Modifier.fillMaxWidth()) {
      Text("Registrarse")
    }
  }
}

@Composable
fun SexButtons(value: String, onChange: (String) -> Unit) {
  val sexOptions = listOf("Masculino", "Femenino", "Otro")
  SingleChoiceSegmentedButtonRow {
    sexOptions.forEachIndexed { index, label ->
      SegmentedButton(
        shape = SegmentedButtonDefaults.itemShape(
          index = index,
          count = sexOptions.size
        ),
        onClick = { onChange(label) },
        selected = label == value,
        label = { Text(label) }
      )
    }
  }
}

@Composable
fun UserList(users: List<User>) {
  LazyColumn() {
    items(users) { user ->
      ListItem(
        headlineContent = { Text("${user.username} - ${user.sex}") },
        supportingContent = { Text(user.password) })
    }
  }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  Ejercicio2RegistroTheme {
    FormularioRegistro()
  }
}
