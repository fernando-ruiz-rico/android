package com.example.ejercicio2registro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ejercicio2registro.ui.theme.Ejercicio2RegistroTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      Ejercicio2RegistroTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          FormularioRegistro(
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

data class User(var username: String, var password: String, var sex: String)

@Composable
fun FormularioRegistro(modifier: Modifier = Modifier) {
  val usernameState = rememberTextFieldState("")
  val passState = rememberTextFieldState("")
  var sexState by remember { mutableStateOf("Masculino") }
  var user by remember { mutableStateOf(null as User?) }

  Column(
    modifier = modifier
      .padding(16.dp)
      .fillMaxSize(),
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
      user = User(usernameState.text.toString(), passState.text.toString(), sexState)
    }, modifier = Modifier.fillMaxWidth()) {
      Text("Registrarse")
    }

    user?.let { user ->
      UserInfo(user)
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
fun UserInfo(user: User) {
  Card {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Text("Usuario registrado", style = MaterialTheme.typography.titleMedium)
      Text("Nombre de usuario: ${user.username}")
      Text("Contraseña: ${user.password}")
      Text("Sexo: ${user.sex}")
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
