package com.example.appplaypulse_grupo4.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.json.JSONArray


@Composable
fun FriendsMockupScreen(onClose: () -> Unit) {
    val ctx = LocalContext.current
    var input by remember { mutableStateOf("") }
    var showList by remember { mutableStateOf(false) }
    var friends by remember { mutableStateOf(loadFriends(ctx).toMutableList()) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Nombre del amigo") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (input.isNotBlank()) {
                friends.add(input)
                saveFriends(ctx, friends)
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
            if (friends.isEmpty()) {
                Text("No hay amigos guardados")
            } else {
                LazyColumn {
                    items(friends) { name ->
                        Text(name)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { onClose() }) {
            Text("Cerrar")
        }
    }
}

// Persist friends as a JSON array string in SharedPreferences
fun saveFriends(context: Context, list: List<String>) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val arr = JSONArray()
    for (s in list) arr.put(s)
    prefs.edit().putString("friends_list_json", arr.toString()).apply()
}

fun loadFriends(context: Context): List<String> {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val raw = prefs.getString("friends_list_json", null) ?: return emptyList()
    return try {
        val arr = JSONArray(raw)
        List(arr.length()) { idx -> arr.optString(idx) }
    } catch (e: Exception) {
        emptyList()
    }
}

@Preview(showBackground = true)
@Composable
fun FriendsMockupPreview() {
    FriendsMockupScreen(onClose = {})
}
