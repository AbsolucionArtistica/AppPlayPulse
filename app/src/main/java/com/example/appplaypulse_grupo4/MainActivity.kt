package com.example.appplaypulse_grupo4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appplaypulse_grupo4.ui.theme.AppPlayPulse_Grupo4Theme
import com.example.appplaypulse_grupo4.ui.theme.HomeScreen
import com.example.appplaypulse_grupo4.database.DatabaseHelper
import com.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    
    private lateinit var databaseHelper: DatabaseHelper
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize database helper
        databaseHelper = DatabaseHelper(this)
        
        // Uncomment the line below to insert sample data on first run
        // databaseHelper.insertSampleData()
        
        enableEdgeToEdge()
        setContent {
            AppPlayPulse_Grupo4Theme {
                val viewModel: MainViewModel = viewModel()
                Greeting(
                    name = "Duoc, Vicente Candia, Agustin Bahamondes y Fernanda Figueroa",
                )
                HomeScreen()
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppPlayPulse_Grupo4Theme {
        Greeting("Android")
    }
}