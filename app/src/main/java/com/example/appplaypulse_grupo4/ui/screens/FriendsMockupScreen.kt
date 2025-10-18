package com.example.appplaypulse_grupo4.ui.screens

import android.content.Context
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.telephony.TelephonyManager
import java.util.Locale
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import android.provider.ContactsContract
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat
import android.content.pm.PackageManager
import androidx.compose.ui.tooling.preview.Preview
import org.json.JSONObject
import androidx.compose.ui.unit.dp
import org.json.JSONArray


data class FriendData(val name: String, val phone: String)

fun normalizeName(input: String): String {
    return input.trim().split(" ").filter { it.isNotBlank() }
        .joinToString(" ") { it.lowercase().replaceFirstChar { ch -> ch.uppercase() } }
}

fun normalizePhone(raw: String, defaultRegion: String = "US"): String {
    val phoneUtil = PhoneNumberUtil.getInstance()
    return try {
        val number = phoneUtil.parse(raw, defaultRegion)
        if (phoneUtil.isValidNumber(number)) phoneUtil.format(number, PhoneNumberFormat.E164) else raw.filter { it.isDigit() }
    } catch (e: NumberParseException) {
        raw.filter { it.isDigit() }
    }
}

fun getDeviceRegion(context: Context): String {
    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    val iso = tm?.networkCountryIso
    return if (!iso.isNullOrBlank()) iso.uppercase(Locale.US) else Locale.getDefault().country
}

@Composable
fun FriendsMockupScreen(onClose: () -> Unit) {
    val ctx = LocalContext.current
    var input by remember { mutableStateOf("") }
    var showList by remember { mutableStateOf(false) }
    var friends = remember { mutableStateListOf<FriendData>().apply { addAll(loadFriends(ctx)) } }
    var showImportDialog by remember { mutableStateOf(false) }
    var contacts by remember { mutableStateOf<List<Pair<String,String>>>(emptyList()) }
    var selectedContacts by remember { mutableStateOf(setOf<Int>()) }

    var showPermissionRationale by remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                contacts = queryContacts(ctx)
                selectedContacts = setOf()
                showImportDialog = true
            } else {
                showPermissionRationale = true
            }
        }
    )

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Nombre del amigo") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (input.isNotBlank()) {
                val normalized = normalizeName(input)
                // avoid duplicate names
                if (friends.none { it.name.equals(normalized, ignoreCase = true) }) {
                    friends.add(FriendData(normalized, ""))
                    saveFriends(ctx, friends)
                    Toast.makeText(ctx, "Amigo guardado", Toast.LENGTH_SHORT).show()
                    input = ""
                } else {
                    Toast.makeText(ctx, "El amigo ya existe", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(ctx, "Ingresa un nombre", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Agregar amigo")
        }

        Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
            // request permission then load contacts
            when (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.READ_CONTACTS)) {
                PackageManager.PERMISSION_GRANTED -> {
                    contacts = queryContacts(ctx)
                    selectedContacts = setOf()
                    showImportDialog = true
                }
                else -> requestPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
            }
        }) {
            Text("Importar contactos")
        }

                if (showPermissionRationale) {
                    AlertDialog(onDismissRequest = { showPermissionRationale = false },
                        title = { Text("Permiso de contactos requerido") },
                        text = { Text("La aplicación necesita permiso para leer contactos si quieres importarlos. Puedes habilitarlo en ajustes.") },
                        confirmButton = {
                            Button(onClick = {
                                // open app settings
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", ctx.packageName, null)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                ctx.startActivity(intent)
                                showPermissionRationale = false
                            }) { Text("Abrir ajustes") }
                        },
                        dismissButton = {
                            Button(onClick = { showPermissionRationale = false }) { Text("Cancelar") }
                        }
                    )
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
                    items(friends) { friend ->
                        Text(friend.name)
                    }
                }
            }
        }

        if (showImportDialog) {
            AlertDialog(
                onDismissRequest = { showImportDialog = false },
                confirmButton = {
                    Button(onClick = {
                        // add selected contacts, dedupe by phone
                        val existingPhones = friends.map { it.phone }.toMutableSet()
                        for (index in selectedContacts) {
                            val (display, phone) = contacts.getOrNull(index) ?: continue
                            val normalizedPhone = normalizePhone(phone)
                            if (normalizedPhone.isBlank()) continue
                            if (existingPhones.contains(normalizedPhone)) continue
                            val normalized = normalizeName(display)
                            friends.add(FriendData(normalized, normalizedPhone))
                            existingPhones.add(normalizedPhone)
                        }
                        saveFriends(ctx, friends)
                        showImportDialog = false
                    }) {
                        Text("Agregar seleccionados")
                    }
                },
                dismissButton = {
                    Button(onClick = { showImportDialog = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Seleccionar contactos") },
                text = {
                    if (contacts.isEmpty()) {
                        Column { Text("No se encontraron contactos") }
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {
                            itemsIndexed(contacts) { idx, pair ->
                                val (display, phone) = pair
                                val isSelected = selectedContacts.contains(idx)
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .toggleable(value = isSelected, onValueChange = { on ->
                                        selectedContacts = if (on) selectedContacts + idx else selectedContacts - idx
                                    })) {
                                    Checkbox(checked = isSelected, onCheckedChange = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = display)
                                        Text(text = phone, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { onClose() }) {
            Text("Cerrar")
        }
    }
}

// Persist friends as a JSON array string in SharedPreferences
fun saveFriends(context: Context, list: List<FriendData>) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val arr = JSONArray()
    for (s in list) {
        val obj = JSONObject()
        obj.put("name", s.name)
        obj.put("phone", s.phone)
        arr.put(obj)
    }
    prefs.edit().putString("friends_list_json", arr.toString()).apply()
}

fun loadFriends(context: Context): List<FriendData> {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val raw = prefs.getString("friends_list_json", null) ?: return emptyList()
    return try {
        val arr = JSONArray(raw)
        if (arr.length() == 0) return emptyList()
        // detect if the stored array is plain strings (legacy) or objects
        val first = arr.opt(0)
        return if (first is String) {
            // migrate to FriendData with empty phone and persist migrated form
            val migrated = List(arr.length()) { idx ->
                val name = arr.optString(idx)
                FriendData(name, "")
            }
            try {
                saveFriends(context, migrated)
            } catch (_: Exception) { }
            migrated
        } else {
            List(arr.length()) { idx ->
                val obj = arr.optJSONObject(idx) ?: JSONObject()
                FriendData(obj.optString("name", ""), obj.optString("phone", ""))
            }
        }
    } catch (e: Exception) {
        emptyList()
    }
}

fun queryContacts(context: Context): List<Pair<String,String>> {
    val cr = context.contentResolver
    val result = mutableListOf<Pair<String,String>>()
    try {
        val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        cursor?.use { c ->
            val idIndex = c.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val hasPhoneIndex = c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
            while (c.moveToNext()) {
                val id = if (idIndex >= 0) c.getString(idIndex) else null
                val name = if (nameIndex >= 0) c.getString(nameIndex) else null
                val hasPhone = if (hasPhoneIndex >= 0) c.getInt(hasPhoneIndex) else 0
                if (id == null || name.isNullOrBlank()) continue
                if (hasPhone > 0) {
                    val pCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(id), null)
                    pCursor?.use { pc ->
                        // collect first non-empty phone
                        val phoneIndex = pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        var found = false
                        while (pc.moveToNext() && !found) {
                            val phone = if (phoneIndex >= 0) pc.getString(phoneIndex) else null
                            if (!phone.isNullOrBlank()) {
                                result.add(name to phone)
                                found = true
                            }
                        }
                    }
                }
            }
        }
    } catch (e: Exception) {
        // best-effort: return what we collected so far, avoid crashing
    }
    return result
}

@Preview(showBackground = true)
@Composable
fun FriendsMockupPreview() {
    FriendsMockupScreen(onClose = {})
}
