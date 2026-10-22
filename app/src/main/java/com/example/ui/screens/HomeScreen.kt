package com.example.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.EngineRegistry
import com.example.model.GameItem
import com.example.ui.components.EmptyLibraryView
import com.example.ui.components.GameCard
import com.example.ui.theme.DreamDarkBackground
import com.example.ui.theme.DreamDarkSurfaceBorder
import com.example.ui.theme.DreamDarkSurfaceCard
import com.example.ui.theme.DreamDarkSurfaceElevated
import com.example.ui.theme.DreamNeonCyan
import com.example.ui.theme.DreamNeonViolet
import com.example.ui.theme.DreamNeonVioletLight
import com.example.ui.theme.DreamTextMuted
import com.example.ui.theme.DreamTextPrimary
import com.example.ui.theme.DreamTextSecondary
import com.example.ui.viewmodel.DreamBoxUiState
import com.example.ui.viewmodel.DreamBoxViewModel

@Composable
fun HomeScreen(
    viewModel: DreamBoxViewModel,
    uiState: DreamBoxUiState,
    onGameSelected: (GameItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // SAF Document Picker
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onGamePicked(context, uri)
        }
    }

    val launchPicker = {
        // Open document with openable MIME types
        openDocumentLauncher.launch(
            arrayOf(
                "*/*"
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DreamDarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header: Title "DreamBox Emulator" & actions
            DreamBoxTopBar(
                onRefreshClick = { viewModel.verifyAllFiles(context) },
                onAddClick = launchPicker,
                isLoading = uiState.isLoading
            )

            // Content
            if (uiState.games.isEmpty()) {
                EmptyLibraryView(
                    onAddGameClick = launchPicker,
                    modifier = Modifier.weight(1f)
                )
            } else {
                // Search field
                SearchAndFilterSection(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = viewModel::setSearchQuery,
                    selectedPlatformCode = uiState.selectedPlatformCode,
                    onPlatformSelect = viewModel::setPlatformFilter,
                    totalCount = uiState.games.size,
                    filteredCount = uiState.filteredGames.size,
                    onClearSearch = {
                        viewModel.setSearchQuery("")
                        focusManager.clearFocus()
                    }
                )

                // List of Games
                if (uiState.filteredGames.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = DreamTextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا توجد نتائج تطابق بحثك",
                                style = MaterialTheme.typography.titleMedium,
                                color = DreamTextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "جرّب تغيير كلمة البحث أو اختيار منصة أخرى",
                                style = MaterialTheme.typography.bodySmall,
                                color = DreamTextMuted
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("games_list"),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 96.dp // padding for FAB and bottom navigation
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.filteredGames,
                            key = { it.id }
                        ) { game ->
                            GameCard(
                                game = game,
                                onClick = { onGameSelected(game) },
                                onDeleteClick = { viewModel.promptDeleteGame(game) },
                                onLaunchClick = {
                                    viewModel.selectGame(game)
                                    viewModel.tryLaunchGame(game)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Button to Add Game
        AnimatedVisibility(
            visible = uiState.games.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
        ) {
            ExtendedFloatingActionButton(
                onClick = launchPicker,
                modifier = Modifier.testTag("fab_add_game"),
                containerColor = DreamNeonViolet,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "إضافة لعبة"
                    )
                },
                text = {
                    Text(
                        text = "إضافة لعبة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            )
        }
    }
}

@Composable
private fun DreamBoxTopBar(
    onRefreshClick: () -> Unit,
    onAddClick: () -> Unit,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DreamDarkSurfaceCard)
            .border(
                width = 1.dp,
                color = DreamDarkSurfaceBorder,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(DreamNeonViolet, DreamNeonCyan)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Gamepad,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "DreamBox Emulator",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = DreamTextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(DreamNeonCyan)
                    )
                }
                Text(
                    text = "واجهة مكتبة الألعاب والـ ROMs",
                    style = MaterialTheme.typography.bodySmall,
                    color = DreamNeonVioletLight,
                    fontSize = 12.sp
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = DreamNeonCyan,
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(
                    onClick = onRefreshClick,
                    modifier = Modifier.testTag("refresh_games_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "تحديث فحص الملفات",
                        tint = DreamNeonCyan
                    )
                }
            }

            IconButton(
                onClick = onAddClick,
                modifier = Modifier.testTag("top_add_game_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة لعبة",
                    tint = DreamNeonVioletLight
                )
            }
        }
    }
}

@Composable
private fun SearchAndFilterSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedPlatformCode: String?,
    onPlatformSelect: (String?) -> Unit,
    totalCount: Int,
    filteredCount: Int,
    onClearSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Search text field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_games_input"),
            placeholder = {
                Text(
                    text = "ابحث بالاسم أو المنصة أو الصيغة...",
                    color = DreamTextMuted,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = DreamNeonVioletLight
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = onClearSearch) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "مسح البحث",
                            tint = DreamTextSecondary
                        )
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DreamNeonViolet,
                unfocusedBorderColor = DreamDarkSurfaceBorder,
                focusedContainerColor = DreamDarkSurfaceElevated,
                unfocusedContainerColor = DreamDarkSurfaceCard,
                focusedTextColor = DreamTextPrimary,
                unfocusedTextColor = DreamTextPrimary
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { /* done */ })
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Platform filter chip row
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "All" chip
            FilterPlatformChip(
                label = "الكل ($totalCount)",
                isSelected = selectedPlatformCode == null,
                color = DreamNeonViolet,
                onClick = { onPlatformSelect(null) },
                tag = "filter_chip_all"
            )

            // Dynamic platform chips from registry
            EngineRegistry.supportedPlatforms.forEach { platform ->
                FilterPlatformChip(
                    label = platform.code,
                    isSelected = selectedPlatformCode == platform.code,
                    color = try {
                        Color(android.graphics.Color.parseColor(platform.badgeColorHex))
                    } catch (e: Exception) {
                        DreamNeonCyan
                    },
                    onClick = { onPlatformSelect(platform.code) },
                    tag = "filter_chip_${platform.code}"
                )
            }
        }
    }
}

@Composable
private fun FilterPlatformChip(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    tag: String
) {
    Box(
        modifier = Modifier
            .testTag(tag)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) color.copy(alpha = 0.25f) else DreamDarkSurfaceCard
            )
            .border(
                width = 1.dp,
                color = if (isSelected) color else DreamDarkSurfaceBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) DreamTextPrimary else DreamTextSecondary
        )
    }
}
