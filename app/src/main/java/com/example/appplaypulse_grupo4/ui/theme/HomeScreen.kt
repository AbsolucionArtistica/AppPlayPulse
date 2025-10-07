package com.example.appplaypulse_grupo4.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.appplaypulse_grupo4.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val gameList = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("App PlayPulse") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(text = "¡Bienvenido!")

            // Llamamos a las nuevas funciones:
            GameInputScreen(gameList)
            GameListScreen(gameList)

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo App",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

