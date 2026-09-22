package com.example.act4

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.act4.ui.theme.AccentYellow
import com.example.act4.ui.theme.BorderColor
import com.example.act4.ui.theme.CardBackground
import com.example.act4.ui.theme.DarkBackground
import com.example.act4.ui.theme.SecondarySurface
import com.example.act4.ui.theme.TextPrimary
import com.example.act4.ui.theme.TextSecondary
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import java.time.format.DateTimeFormatter
import java.util.Calendar

// paleta de colores
val ColorOptions = listOf(
    "#151515",
    "#1B2415",
    "#241D15",
    "#1E1E1E",
    "#222015"
)

val CategoryList = listOf("Trabajo", "Personal", "Idea", "Urgente")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(viewModel: NoteViewModel) {
    val notes by viewModel.allNotes.collectAsState()
    val rawNotes by viewModel.rawNotesList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val context = LocalContext.current

    var showBottomSheet by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<Note?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        containerColor = DarkBackground
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // encabezado de la app
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Badge con icono amarillo
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = DarkBackground,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (selectedCategory == "Archivadas") "Archivadas" else "Notas",
                            style = MaterialTheme.typography.titleLarge
                        )
                        val activeNotes = rawNotes.filter { !it.isArchived }
                        val archivedNotes = rawNotes.filter { it.isArchived }
                        val pinnedCount = activeNotes.count { it.isPinned }

                        val subtitleText = if (selectedCategory == "Archivadas") {
                            "${archivedNotes.size} notas archivadas"
                        } else {
                            "${activeNotes.size} notas · $pinnedCount fijadas"
                        }

                        Text(
                            text = subtitleText,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                // boton de nueva nota con texto e icono en negro bien visible
                Button(
                    onClick = {
                        editingNote = null
                        showBottomSheet = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentYellow,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Nueva nota",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // barra de busqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Buscar notas...", color = TextSecondary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = TextSecondary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SecondarySurface,
                    unfocusedContainerColor = SecondarySurface,
                    focusedBorderColor = AccentYellow,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // categorias (incluyendo la sección de Archivadas)
            val activeNotesList = rawNotes.filter { !it.isArchived }
            val archivedNotesList = rawNotes.filter { it.isArchived }
            val allFilterCategories = listOf("Todas") + CategoryList + listOf("Archivadas")

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allFilterCategories) { category ->
                    val count = when (category) {
                        "Todas" -> activeNotesList.size
                        "Archivadas" -> archivedNotesList.size
                        else -> activeNotesList.count { it.category.equals(category, ignoreCase = true) }
                    }

                    val isSelected = selectedCategory == category
                    val labelText = if (category == "Todas") "Todas" else "$category $count"

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) AccentYellow else SecondarySurface)
                            .clickable { viewModel.setSelectedCategory(category) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = labelText,
                            color = if (isSelected) DarkBackground else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // texto de sugerencia
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val hintText = if (selectedCategory == "Archivadas") {
                    "→ Desliza a la derecha para desarchivar"
                } else {
                    "→ Desliza a la derecha para archivar · toca "
                }
                Text(
                    text = hintText,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                if (selectedCategory != "Archivadas") {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AccentYellow,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = " para fijar",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // lista con gestos y animaciones (Archivar/Desarchivar / Eliminar)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(items = notes, key = { it.id }) { note ->
                    SwipeableNoteItem(
                        note = note,
                        onDelete = { viewModel.deleteNote(note) },
                        onArchiveToggle = {
                            if (note.isArchived) {
                                viewModel.unarchiveNote(note)
                            } else {
                                viewModel.archiveNote(note)
                            }
                        },
                        onTogglePin = { viewModel.togglePin(note) },
                        onEdit = {
                            editingNote = note
                            showBottomSheet = true
                        }
                    )
                }
            }
        }

        // modal Bottom Sheet de nueva nota y editar nota
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = CardBackground,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                NoteFormBottomSheet(
                    noteToEdit = editingNote,
                    onDismiss = { showBottomSheet = false },
                    onSave = { title, content, category, colorHex, reminderAt ->


                        if (editingNote != null) {
                            val updateNote = editingNote!!.copy(
                                    title = title,
                                    content = content,
                                    category = category,
                                    colorHex = colorHex,
                                    reminderAt = reminderAt
                            )

                            NotificationHelper.cancelReminder(
                                context,
                                editingNote!!.id
                            )

                            viewModel.updateNote(updateNote)

                            if (reminderAt != null) {
                                NotificationHelper.scheduleReminder(
                                    context = context,
                                    noteId = editingNote!!.id,
                                    title = title,
                                    reminderAt = reminderAt
                                )
                            }
                        } else {
                            val newNote = Note(
                                title = title,
                                content = content,
                                date = LocalDateTime.now(),
                                reminderAt = reminderAt,
                                category = category,
                                colorHex = colorHex
                            )
                            viewModel.insertNote(newNote) { noteId ->

                                if (reminderAt != null) {
                                    NotificationHelper.scheduleReminder(
                                        context = context,
                                        noteId = noteId,
                                        title = title,
                                        reminderAt = reminderAt
                                    )
                                }
                            }
                        }

                        showBottomSheet = false
                    }
                )
            }
        }
    }
}

// elemento deslizable con soporte de Archivar / Desarchivar
@Composable
fun SwipeableNoteItem(
    note: Note,
    onDelete: () -> Unit,
    onArchiveToggle: () -> Unit,
    onTogglePin: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    AnimatedVisibility(
        visible = true,
        enter = slideInHorizontally { -it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        val isSwipingRight = offsetX.value > 0
        val bgColor = if (isSwipingRight) Color(0xFF0D2818) else Color.Red

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
        ) {
            if (isSwipingRight) {
                // Fondo verde con texto "→ Archivar" o "→ Desarchivar"
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = if (note.isArchived) "→  Desarchivar" else "→  Archivar",
                        color = Color(0xFF22C55E),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            } else {
                // Fondo rojo con icono de eliminar
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.White
                    )
                }
            }

            // tarjeta deslizable
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .pointerInput(note.id) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                coroutineScope.launch {
                                    val threshold = size.width * 0.3f
                                    if (offsetX.value > threshold) {
                                        // deslizar a la derecha -> Archivar / Desarchivar
                                        offsetX.animateTo(
                                            targetValue = size.width.toFloat(),
                                            animationSpec = tween(durationMillis = 200)
                                        )
                                        onArchiveToggle()
                                    } else if (offsetX.value < -threshold) {
                                        // deslizar a la izquierda -> Eliminar
                                        offsetX.animateTo(
                                            targetValue = -size.width.toFloat(),
                                            animationSpec = tween(durationMillis = 200)
                                        )
                                        onDelete()
                                    } else {
                                        offsetX.animateTo(0f, animationSpec = tween(200))
                                    }
                                }
                            },
                            onDragCancel = {
                                coroutineScope.launch { offsetX.animateTo(0f) }
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                coroutineScope.launch {
                                    val newOffset = offsetX.value + dragAmount
                                    offsetX.snapTo(newOffset)
                                }
                            }
                        )
                    }
            ) {
                NoteCardDesign(
                    note = note,
                    onTogglePin = onTogglePin,
                    onArchiveToggle = onArchiveToggle,
                    onEdit = onEdit,
                    onDelete = onDelete
                )
            }
        }
    }
}

// diseño de la card de Figma
@Composable
fun NoteCardDesign(
    note: Note,
    onTogglePin: () -> Unit,
    onArchiveToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val cardBgColor = parseColor(note.colorHex)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            if (note.isPinned && !note.isArchived) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(AccentYellow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Fijada",
                        tint = DarkBackground,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Column {
                // titulo
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(end = if (note.isPinned && !note.isArchived) 28.dp else 0.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // nota (contenido)
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // fila inferior
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // badge de categoría
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SecondarySurface)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (note.isArchived) "Archivada" else note.category,
                                color = AccentYellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // tiempo transcurrido
                        Text(
                            text = formatTimeAgo(note.date),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }

                    // botones de acción
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (note.isArchived) {
                            // Botón para desarchivar directamente
                            IconButton(onClick = onArchiveToggle, modifier = Modifier.size(28.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Desarchivar",
                                    tint = AccentYellow,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else {
                            IconButton(onClick = onTogglePin, modifier = Modifier.size(28.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Fijar",
                                    tint = if (note.isPinned) AccentYellow else TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// bottomsheet para crear/editar
@Composable
fun NoteFormBottomSheet(
    noteToEdit: Note?,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        content: String,
        category: String,
        colorHex: String,
        reminderAt: LocalDateTime?
            ) -> Unit
) {
    var title by remember { mutableStateOf(noteToEdit?.title ?: "") }
    var content by remember { mutableStateOf(noteToEdit?.content ?: "") }
    var selectedCategory by remember { mutableStateOf(noteToEdit?.category ?: "Personal") }
    var selectedColorHex by remember { mutableStateOf(noteToEdit?.colorHex ?: ColorOptions.first()) }
    var reminderAt by remember { mutableStateOf(noteToEdit?.reminderAt) }
    val context = LocalContext.current
    val reminderFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // titulo y boton de cerrar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (noteToEdit != null) "Editar nota" else "Nueva nota",
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = TextSecondary
                )
            }
        }

        // titulo
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("Título de la nota...", color = TextSecondary) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BorderColor,
                unfocusedBorderColor = BorderColor,
                focusedContainerColor = SecondarySurface,
                unfocusedContainerColor = SecondarySurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // campo contenido
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            placeholder = { Text("Escribe tu nota aquí...", color = TextSecondary) },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BorderColor,
                unfocusedBorderColor = BorderColor,
                focusedContainerColor = SecondarySurface,
                unfocusedContainerColor = SecondarySurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // selector de categorias
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryList.forEach { category ->
                val isSelected = selectedCategory == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SecondarySurface)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) AccentYellow else BorderColor,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) AccentYellow else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // selector de fondos
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fondo",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(end = 12.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ColorOptions.forEach { hex ->
                    val color = parseColor(hex)
                    val isSelected = selectedColorHex == hex

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) AccentYellow else BorderColor,
                                shape = CircleShape
                            )
                            .clickable { selectedColorHex = hex }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val calendar = Calendar.getInstance()

                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->

                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                reminderAt = LocalDateTime.of(
                                    year,
                                    month + 1,
                                    dayOfMonth,
                                    hour,
                                    minute
                                )
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (reminderAt == null)
                    "🔔Agregar recordatorio"
                else
                    "🔔${reminderAt!!.format(reminderFormatter)}",
                color = Color.Black
            )
        }

        if (reminderAt != null) {
            TextButton(
                onClick = {
                    reminderAt = null
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Quitar recordatorio",
                    color = Color.Black)
            }
        }


        // agregar y cancelar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondarySurface,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar")
            }

            val isValid = title.isNotBlank() || content.isNotBlank()
            Button(
                onClick = {
                    if (isValid) {
                        onSave(title, content, selectedCategory, selectedColorHex, reminderAt)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentYellow,
                    contentColor = Color.Black,
                    disabledContainerColor = SecondarySurface,
                    disabledContentColor = TextSecondary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (noteToEdit != null) "Guardar" else "Agregar nota",
                    color = if (isValid) Color.Black else TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// función aux para convertir string hexadecimal a color de compose
fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Exception) {
        CardBackground
    }
}

// función aux para formatear la fecha a tiempo transcurrido
fun formatTimeAgo(dateTime: LocalDateTime): String {
    val duration = Duration.between(dateTime, LocalDateTime.now())
    val minutes = duration.toMinutes()
    val hours = duration.toHours()
    val days = duration.toDays()

    return when {
        minutes < 1 -> "ahora"
        minutes < 60 -> "hace ${minutes}m"
        hours < 24 -> "hace ${hours}h"
        else -> "hace ${days}d"
    }
}
