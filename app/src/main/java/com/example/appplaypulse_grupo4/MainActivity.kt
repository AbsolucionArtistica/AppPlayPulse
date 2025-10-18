package com.example.appplaypulse_grupo4

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appplaypulse_grupo4.ui.theme.AppPlayPulse_Grupo4Theme
import com.example.appplaypulse_grupo4.ui.theme.HomeScreen
import com.example.appplaypulse_grupo4.database.DatabaseHelper
import com.viewmodel.MainViewModel
import com.example.appplaypulse_grupo4.ui.components.AnimatedSideMenu
import com.example.appplaypulse_grupo4.ui.screens.FriendsMockupScreen

class MainActivity : ComponentActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database helper
        databaseHelper = DatabaseHelper(this)

        enableEdgeToEdge()
        setContent {
            AppPlayPulse_Grupo4Theme {
                val viewModel: MainViewModel = viewModel()
                var showFriends by remember { mutableStateOf(false) }

                val ctx = LocalContext.current

                Scaffold {
                    // Pass a callback to the side menu to toggle the friends screen
                    AnimatedSideMenu(onFriendsClick = {
                        showFriends = true
                        Toast.makeText(ctx, "Abriendo Amigos (mockup)", Toast.LENGTH_SHORT).show()
                    })

                    if (showFriends) {
                        FriendsMockupScreen(onClose = { showFriends = false })
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun FriendsMockupScreen(onClose: () -> Unit) {
    val ctx = LocalContext.current
    var input by remember { mutableStateOf("") }
    var showList by remember { mutableStateOf(false) }
    val prefsKey = "friends_list"

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Nombre del amigo") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (input.isNotBlank()) {
                saveFriend(ctx, input)
                Toast.makeText(ctx, "Amigo guardado", Toast.LENGTH_SHORT).show()
                input = ""
            } else {
                Toast.makeText(ctx, "Ingresa un nombre", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Agregar amigo")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { showList = !showList }) {
            Text(if (showList) "Ocultar amigos" else "Mostrar amigos")
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (showList) {
            val list = loadFriends(ctx)
            if (list.isEmpty()) {
                Text("No hay amigos guardados")
            } else {
                for (name in list) {
                    Text(name)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { onClose() }) {
            Text("Cerrar")
        }
    }
}

fun saveFriend(context: Context, name: String) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val existing = prefs.getString("friends_list", "") ?: ""
    val updated = if (existing.isBlank()) name else existing + "|" + name
    prefs.edit().putString("friends_list", updated).apply()
}

fun loadFriends(context: Context): List<String> {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val raw = prefs.getString("friends_list", "") ?: ""
    if (raw.isBlank()) return emptyList()
    return raw.split("|")
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppPlayPulse_Grupo4Theme {
        Greeting("Android")
    }
}