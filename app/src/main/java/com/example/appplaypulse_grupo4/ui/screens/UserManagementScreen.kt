package com.example.appplaypulse_grupo4.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appplaypulse_grupo4.api.backend.ApiUser

@Composable
fun UserManagementScreen(
    users: List<ApiUser>,
    onRefresh: () -> Unit,
    onDeleteUser: (ApiUser) -> Unit,
    onClose: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gestion de usuarios",
                style = MaterialTheme.typography.headlineSmall
            )
            Button(onClick = onClose) {
                Text("Volver")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total: ${users.size}",
                style = MaterialTheme.typography.titleMedium
            )
            Button(onClick = onRefresh) {
                Text("Actualizar")
            }
        }

        Divider()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users) { user ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "${user.username} (${user.email})", style = MaterialTheme.typography.titleMedium)
                    Text(text = "${user.nombre} ${user.apellido} - Tel: ${user.phone}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = { onDeleteUser(user) }) {
                            Text("Eliminar")
                        }
                    }
                }
                Divider()
            }
        }
    }
}
