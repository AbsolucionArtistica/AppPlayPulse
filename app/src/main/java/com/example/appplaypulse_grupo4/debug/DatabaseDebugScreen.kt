package com.example.appplaypulse_grupo4.debug

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.viewmodel.MainViewModel

@Composable
fun DatabaseDebugScreen(
    viewModel: MainViewModel = viewModel()
) {
    var userCount by remember { mutableStateOf(0) }
    var averageScore by remember { mutableStateOf(0.0) }
    val allUsers by viewModel.topUsers.collectAsState()
    
    LaunchedEffect(Unit) {
        // You can add methods to get database stats here
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Database Debug Console",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Database Stats Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Database Statistics",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("Total Users: ${allUsers.size}")
                Text("Average Score: ${allUsers.map { it.highScore }.average().takeIf { !it.isNaN() } ?: 0.0}")
                Text("Highest Score: ${allUsers.maxOfOrNull { it.highScore } ?: 0}")
                Text("Database Location: /data/data/com.example.appplaypulse_grupo4/databases/app_database")
            }
        }
        
        // Quick Actions
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Button(
                    onClick = { 
                        // Add sample data
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text("Add Sample Users")
                }
                
                Button(
                    onClick = { 
                        // Clear all data
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Clear All Data")
                }
            }
        }
        
        // SQL Query Examples
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Common SQL Queries",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                val queries = listOf(
                    "SELECT * FROM users;",
                    "SELECT * FROM users ORDER BY highScore DESC;",
                    "SELECT username, highScore FROM users WHERE level > 2;",
                    "SELECT COUNT(*) FROM users;",
                    "SELECT AVG(highScore) FROM users;",
                    "SELECT * FROM users WHERE username LIKE '%Player%';"
                )
                
                LazyColumn {
                    items(queries) { query ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = query,
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
