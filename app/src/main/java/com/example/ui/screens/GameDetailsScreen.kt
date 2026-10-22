package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.EngineRegistry
import com.example.model.GameItem
import com.example.ui.theme.DreamDarkBackground
import com.example.ui.theme.DreamDarkSurfaceBorder
import com.example.ui.theme.DreamDarkSurfaceCard
import com.example.ui.theme.DreamDarkSurfaceElevated
import com.example.ui.theme.DreamError
import com.example.ui.theme.DreamNeonCyan
import com.example.ui.theme.DreamNeonEmerald
import com.example.ui.theme.DreamNeonViolet
import com.example.ui.theme.DreamTextMuted
import com.example.ui.theme.DreamTextPrimary
import com.example.ui.theme.DreamTextSecondary
import com.example.ui.viewmodel.DreamBoxViewModel

@Composable
fun GameDetailsScreen(
    game: GameItem?,
    viewModel: DreamBoxViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    if (game == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(DreamDarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("لم يتم تحديد لعبة", color = DreamTextSecondary)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onBack) {
                    Text("العودة إلى المكتبة")
                }
            }
        }
        return
    }

    val platformInfo = EngineRegistry.inferPlatform(game.fileExtension)
    val platformColor = try {
        Color(android.graphics.Color.parseColor(platformInfo.badgeColorHex))
    } catch (e: Exception) {
        DreamNeonViolet
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DreamDarkBackground)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DreamDarkSurfaceCard)
                .border(
                    width = 1.dp,
                    color = DreamDarkSurfaceBorder,
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("details_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرجوع للمكتبة",
                        tint = DreamNeonCyan
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تفاصيل ملف اللعبة",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = DreamTextPrimary
                )
            }

            IconButton(
                onClick = { viewModel.promptDeleteGame(game) },
                modifier = Modifier.testTag("details_delete_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف من المكتبة",
                    tint = DreamError
                )
            }
        }

        // Body Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // Hero Platform Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                platformColor.copy(alpha = 0.25f),
                                DreamDarkSurfaceElevated
                            )
                        )
                    )
                    .border(1.5.dp, platformColor.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Platform Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(platformColor.copy(alpha = 0.25f))
                                .border(1.dp, platformColor, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = game.platformName,
                                color = platformColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        // Availability Badge
                        if (game.isAvailable) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DreamNeonEmerald.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = DreamNeonEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "صلاحية الوصول نشطة",
                                    color = DreamNeonEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DreamError.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = DreamError,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "الملف غير متوفر حالياً",
                                    color = DreamError,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = game.cleanName,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = DreamTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = game.fileName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DreamTextMuted,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Launch Button Section (requirement: disabled with clear notice)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DreamDarkSurfaceCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DreamDarkSurfaceBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(DreamNeonCyan)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تشغيل اللعبة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DreamTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "محرك التشغيل غير متصل حاليًا في النسخة الأولى. انقر للاطلاع على النواة المتوافقة وتفاصيل بنية المحاكاة.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DreamTextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.tryLaunchGame(game) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("launch_game_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DreamNeonViolet.copy(alpha = 0.85f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تشغيل (لا يوجد محرك مثبت)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Detailed Specifications Grid
            Text(
                text = "بيانات الملف والتخزين الآمن",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DreamNeonCyan,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SpecItemCard(
                    icon = Icons.Default.Storage,
                    label = "حجم الملف الفعلي",
                    value = "${game.formattedSize} (${game.fileSizeBytes} بايت)"
                )

                SpecItemCard(
                    icon = Icons.Default.Description,
                    label = "امتداد الملف والصيغة",
                    value = ".${game.fileExtension.uppercase()} (نظام ${game.platformName})"
                )

                SpecItemCard(
                    icon = Icons.Default.Gamepad,
                    label = "النواة المستهدفة (Future Core)",
                    value = platformInfo.targetCore
                )

                SpecItemCard(
                    icon = Icons.Default.CalendarToday,
                    label = "تاريخ الإضافة للمكتبة",
                    value = game.formattedDate
                )

                SpecItemCard(
                    icon = Icons.Default.FolderOpen,
                    label = "معرّف التخزين الآمن (SAF URI)",
                    value = game.uriString,
                    isSmallText = true
                )

                SpecItemCard(
                    icon = Icons.Default.Info,
                    label = "حالة سلامة الوصول",
                    value = game.statusMessage
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons: Re-verify & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.verifySingleGame(context, game) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("verify_file_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(listOf(DreamNeonCyan, DreamNeonViolet))
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = DreamNeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "إعادة فحص الملف",
                        color = DreamTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = { viewModel.promptDeleteGame(game) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("delete_from_library_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DreamError.copy(alpha = 0.2f),
                        contentColor = DreamError
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "إزالة من المكتبة",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SpecItemCard(
    icon: ImageVector,
    label: String,
    value: String,
    isSmallText: Boolean = false
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DreamDarkSurfaceCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DreamDarkSurfaceBorder, RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DreamNeonViolet.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DreamNeonViolet,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = DreamTextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = if (isSmallText) 11.sp else 13.sp
                    ),
                    color = DreamTextPrimary,
                    lineHeight = if (isSmallText) 16.sp else 18.sp
                )
            }
        }
    }
}
