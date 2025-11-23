    package com.example.appplaypulse_grupo4.ui.theme

    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.sp
    import androidx.compose.foundation.layout.Box

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopNavBar(title: String = "PlayPulse") {
        TopAppBar(
            title = {
                //  Centrado del texto
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF4DD0E1) //  Cyan claro
            )
        )
    }
