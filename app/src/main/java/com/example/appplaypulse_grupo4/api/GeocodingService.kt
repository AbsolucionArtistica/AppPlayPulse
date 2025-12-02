package com.example.appplaypulse_grupo4.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Cliente simple para georreferenciación usando el servicio público de Nominatim (OpenStreetMap).
 * No requiere API key. Retorna una dirección legible a partir de coordenadas.
 */
object GeocodingService {
    suspend fun reverseGeocode(lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
        val url =
            URL("https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=$lat&lon=$lon&accept-language=es")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            setRequestProperty("User-Agent", "AppPlayPulse-Geocoder")
            connectTimeout = 5000
            readTimeout = 5000
        }

        return@withContext try {
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val body = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(body)
                json.optString("display_name").takeIf { it.isNotBlank() }
            } else null
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }
}
