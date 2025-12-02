package com.example.appplaypulse_grupo4.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.widget.Toast
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.appplaypulse_grupo4.api.GeocodingService
import com.example.appplaypulse_grupo4.database.dto.FeedItem
import com.example.appplaypulse_grupo4.ui.theme.TopNavBar
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen(
    currentUsername: String,
    hasFriends: Boolean,
    posts: List<FeedItem>,
    onPublishPost: (String, String?, String?, String?) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToGames: () -> Unit,
    onNavigateToFriends: () -> Unit
    ) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var text by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var locationCoordinates by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var link by remember { mutableStateOf("") }
    var capturedImageUri by remember { mutableStateOf<String?>(null) }
    var isPublishing by remember { mutableStateOf(false) }

    var selectedImage by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImage = uri
            capturedImageUri = uri.toString()
            Toast.makeText(context, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val saved = saveBitmapToCache(context, bitmap)
            if (saved != null) {
                capturedImageUri = saved.toString()
                selectedImage = saved
                Toast.makeText(context, "Foto capturada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "No se pudo guardar la foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Permiso de camara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            val last = getLastKnownLocation(context)
            locationCoordinates = last?.let { it.latitude to it.longitude }
            location = locationCoordinates?.let { formatCoordinates(it.first, it.second) } ?: ""
            if (location.isBlank()) {
                Toast.makeText(context, "No se pudo obtener ubicacion", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Permiso de ubicacion denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = { TopNavBar(title = "Comunidad") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Hola, $currentUsername",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        InitialsAvatar(
                            name = currentUsername,
                            size = 40.dp
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        TextField(
                            value = text,
                            onValueChange = { text = it },
                            placeholder = { Text("Que esta pasando?") },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 56.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                disabledContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                    }

                    Text(
                        text = "Cualquier persona puede responder",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasPermission) {
                                    cameraLauncher.launch(null)
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.CameraAlt,
                                    contentDescription = "Tomar foto"
                                )
                            }

                            IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                                Icon(
                                    imageVector = Icons.Filled.Image,
                                    contentDescription = "Agregar imagen"
                                )
                            }

                            IconButton(onClick = {
                                val fine = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                                val coarse = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                                if (fine || coarse) {
                                    val last = getLastKnownLocation(context)
                                    locationCoordinates = last?.let { it.latitude to it.longitude }
                                    location = locationCoordinates?.let { formatCoordinates(it.first, it.second) } ?: ""
                                    if (location.isBlank()) {
                                        Toast.makeText(
                                            context,
                                            "No se pudo obtener ubicacion",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = "Agregar ubicacion"
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (text.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Escribe algo para publicar",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (link.isNotBlank() && !Patterns.WEB_URL.matcher(link).matches()) {
                                    Toast.makeText(
                                        context,
                                        "El enlace debe tener un formato valido",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    scope.launch {
                                        isPublishing = true
                                        try {
                                            val finalLocation = try {
                                                locationCoordinates?.let { coords ->
                                                    GeocodingService.reverseGeocode(
                                                        coords.first,
                                                        coords.second
                                                    )
                                                } ?: location.takeIf { it.isNotBlank() }
                                            } catch (_: Exception) {
                                                location.takeIf { it.isNotBlank() }
                                            }

                                            val cleanText = text.trim()
                                            val trimmedLink = link.trim()

                                            onPublishPost(
                                                cleanText,
                                                finalLocation,
                                                trimmedLink.takeIf { it.isNotBlank() },
                                                capturedImageUri
                                            )
                                            text = ""
                                            location = ""
                                            link = ""
                                            selectedImage = null
                                            capturedImageUri = null
                                            locationCoordinates = null
                                        } finally {
                                            isPublishing = false
                                        }
                                    }
                                }
                            },
                            enabled = text.isNotBlank() && !isPublishing,
                            shape = MaterialTheme.shapes.large
                        ) {
                            if (isPublishing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Publicando...")
                            } else {
                                Text("Postear")
                            }
                        }
                    }

                    if (selectedImage != null) {
                        AsyncImage(
                            model = selectedImage,
                            contentDescription = "Vista previa",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 220.dp)
                        )
                    }

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Ubicacion (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = link,
                        onValueChange = { link = it },
                        label = { Text("Link (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (posts.isEmpty()) {
                Text(
                    text = "Aun no hay actividad en tu comunidad.",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = if (hasFriends)
                        "Cuando tus amigos publiquen algo, aparecera aqui."
                    else
                        "Agrega amigos para ver sus publicaciones aqui.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Actividad reciente",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    posts.forEach { post ->
                        PostCard(item = post)
                    }
                }
            }
        }
    }
}

@Composable
private fun PostCard(item: FeedItem) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "@${item.username}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )

            item.imageUri?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Imagen adjunta",
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                )
            }

            Text(
                text = item.content,
                style = MaterialTheme.typography.bodyMedium
            )

            item.location?.let { loc ->
                if (loc.isNotBlank()) {
                    Text(
                        text = "\uD83D\uDCCD $loc",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            item.link?.let { url ->
                if (url.isNotBlank()) {
                    TextButton(
                        onClick = {
                            runCatching {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }.onFailure {
                                Toast.makeText(
                                    context,
                                    "No se pudo abrir el enlace",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = url,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { /* TODO: like */ }, contentPadding = PaddingValues(0.dp)) {
                    Text("Like")
                }
                TextButton(onClick = { /* TODO: responder */ }, contentPadding = PaddingValues(0.dp)) {
                    Text("Responder")
                }
            }
        }
    }
}

private fun saveBitmapToCache(context: Context, bitmap: android.graphics.Bitmap): Uri? {
    return try {
        val file = File(context.cacheDir, "post_${UUID.randomUUID()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
        }
        Uri.fromFile(file)
    } catch (_: Exception) {
        null
    }
}

private fun getLastKnownLocation(context: Context): Location? {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        ?: return null
    val providers = listOf(
        LocationManager.GPS_PROVIDER,
        LocationManager.NETWORK_PROVIDER,
        LocationManager.PASSIVE_PROVIDER
    )
    var bestLocation: Location? = null
    for (provider in providers) {
        val loc = try {
            locationManager.getLastKnownLocation(provider)
        } catch (_: SecurityException) {
            null
        }
        if (loc != null) {
            if (bestLocation == null || loc.accuracy < bestLocation!!.accuracy) {
                bestLocation = loc
            }
        }
    }
    return bestLocation
}

private fun formatCoordinates(lat: Double, lon: Double): String =
    String.format("%.5f, %.5f", lat, lon)
