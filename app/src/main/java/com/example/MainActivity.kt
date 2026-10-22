package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ClearLibraryConfirmationDialog
import com.example.ui.components.DeleteGameConfirmationDialog
import com.example.ui.components.LaunchNoticeDialog
import com.example.ui.screens.GameDetailsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SupportedSystemsScreen
import com.example.ui.theme.DreamBoxTheme
import com.example.ui.theme.DreamDarkBackground
import com.example.ui.theme.DreamDarkSurfaceBorder
import com.example.ui.theme.DreamDarkSurfaceCard
import com.example.ui.theme.DreamNeonCyan
import com.example.ui.theme.DreamNeonViolet
import com.example.ui.theme.DreamTextMuted
import com.example.ui.theme.DreamTextPrimary
import com.example.ui.viewmodel.DreamBoxTab
import com.example.ui.viewmodel.DreamBoxViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: DreamBoxViewModel by viewModels {
        DreamBoxViewModel.provideFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DreamBoxTheme {
                // Mandatory Arabic RTL Layout Direction
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    DreamBoxApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun DreamBoxApp(viewModel: DreamBoxViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Display user messages via Snackbar
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    // Handle Back Press to return to Library
    BackHandler(enabled = uiState.activeTab != DreamBoxTab.LIBRARY) {
        viewModel.navigateBackToLibrary()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            DreamBoxBottomBar(
                activeTab = uiState.activeTab,
                hasSelectedGame = uiState.selectedGame != null,
                onTabSelect = viewModel::setActiveTab
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DreamDarkBackground)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = uiState.activeTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { tab ->
                when (tab) {
                    DreamBoxTab.LIBRARY -> {
                        HomeScreen(
                            viewModel = viewModel,
                            uiState = uiState,
                            onGameSelected = { game ->
                                viewModel.selectGame(game)
                            }
                        )
                    }

                    DreamBoxTab.DETAILS -> {
                        GameDetailsScreen(
                            game = uiState.selectedGame,
                            viewModel = viewModel,
                            onBack = viewModel::navigateBackToLibrary
                        )
                    }

                    DreamBoxTab.SYSTEMS -> {
                        SupportedSystemsScreen()
                    }

                    DreamBoxTab.SETTINGS -> {
                        SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    // Launch Notice Dialog (Notice: "لا يوجد محرك متوافق مثبت")
    uiState.launchNotice?.let { notice ->
        LaunchNoticeDialog(
            notice = notice,
            onDismiss = viewModel::dismissLaunchNotice
        )
    }

    // Delete Single Game Confirmation Dialog
    uiState.isDeleteConfirmationGame?.let { game ->
        DeleteGameConfirmationDialog(
            game = game,
            onConfirm = { viewModel.confirmDeleteGame(context) },
            onDismiss = viewModel::dismissDeleteGame
        )
    }

    // Clear All Library Confirmation Dialog
    if (uiState.isClearConfirmationOpen) {
        ClearLibraryConfirmationDialog(
            onConfirm = { viewModel.confirmClearLibrary(context) },
            onDismiss = viewModel::dismissClearLibrary
        )
    }
}

@Composable
private fun DreamBoxBottomBar(
    activeTab: DreamBoxTab,
    hasSelectedGame: Boolean,
    onTabSelect: (DreamBoxTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .background(DreamDarkSurfaceCard)
            .border(
                width = 1.dp,
                color = DreamDarkSurfaceBorder,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem(
                label = "المكتبة",
                icon = Icons.Default.Gamepad,
                isSelected = activeTab == DreamBoxTab.LIBRARY,
                onClick = { onTabSelect(DreamBoxTab.LIBRARY) },
                testTag = "nav_tab_library"
            )

            if (hasSelectedGame) {
                BottomBarItem(
                    label = "التفاصيل",
                    icon = Icons.Default.Info,
                    isSelected = activeTab == DreamBoxTab.DETAILS,
                    onClick = { onTabSelect(DreamBoxTab.DETAILS) },
                    testTag = "nav_tab_details"
                )
            }

            BottomBarItem(
                label = "الأنظمة",
                icon = Icons.Default.Extension,
                isSelected = activeTab == DreamBoxTab.SYSTEMS,
                onClick = { onTabSelect(DreamBoxTab.SYSTEMS) },
                testTag = "nav_tab_systems"
            )

            BottomBarItem(
                label = "الإعدادات",
                icon = Icons.Default.Settings,
                isSelected = activeTab == DreamBoxTab.SETTINGS,
                onClick = { onTabSelect(DreamBoxTab.SETTINGS) },
                testTag = "nav_tab_settings"
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) DreamNeonViolet.copy(alpha = 0.25f) else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) DreamNeonCyan else DreamTextMuted,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) DreamTextPrimary else DreamTextMuted
        )
    }
}
