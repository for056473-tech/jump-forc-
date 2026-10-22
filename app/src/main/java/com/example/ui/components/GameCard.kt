package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameItem
import com.example.ui.theme.DreamDarkSurfaceBorder
import com.example.ui.theme.DreamDarkSurfaceCard
import com.example.ui.theme.DreamNeonCyan
import com.example.ui.theme.DreamNeonEmerald
import com.example.ui.theme.DreamNeonViolet
import com.example.ui.theme.DreamTextMuted
import com.example.ui.theme.DreamTextPrimary
import com.example.ui.theme.DreamTextSecondary

@Composable
fun GameCard(
    game: GameItem,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onLaunchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val platformColor = try {
        Color(android.graphics.Color.parseColor(game.badgeColorHex))
    } catch (e: Exception) {
        DreamNeonViolet
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("game_card_${game.id}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DreamDarkSurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (game.isAvailable) DreamDarkSurfaceBorder else Color(0xFFEF4444).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            // Top Row: Platform badge + Quick Status pill + Delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Platform badge chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(platformColor.copy(alpha = 0.18f))
                        .border(1.dp, platformColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(platformColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = game.platformName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = platformColor
                        )
                    }
                }

                // Status chip & actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (game.isAvailable) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DreamNeonEmerald.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = DreamNeonEmerald,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "جاهز للربط",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = DreamNeonEmerald
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFEF4444).copy(alpha = 0.18f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "غير متوفر",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("delete_game_${game.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف اللعبة",
                            tint = DreamTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Game File Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    platformColor.copy(alpha = 0.25f),
                                    DreamNeonViolet.copy(alpha = 0.15f)
                                )
                            )
                        )
                        .border(1.dp, platformColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (game.fileExtension in listOf("zip", "7z")) Icons.Default.FolderZip else Icons.Default.Gamepad,
                        contentDescription = null,
                        tint = platformColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = game.cleanName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = DreamTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = game.fileName,
                        style = MaterialTheme.typography.bodySmall,
                        color = DreamTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom metadata info row: File Size, Extension tag, Launch button affordance
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Extension badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DreamDarkSurfaceBorder)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = ".${game.fileExtension.uppercase()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DreamNeonCyan
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = game.formattedSize,
                        style = MaterialTheme.typography.bodySmall,
                        color = DreamTextSecondary,
                        fontSize = 12.sp
                    )
                }

                // Quick Play/Details button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    DreamNeonViolet.copy(alpha = 0.3f),
                                    DreamNeonCyan.copy(alpha = 0.2f)
                                )
                            )
                        )
                        .border(1.dp, DreamNeonViolet.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .clickable(onClick = onLaunchClick)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "تشغيل أو تفاصيل",
                            tint = DreamNeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تشغيل",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DreamTextPrimary
                        )
                    }
                }
            }
        }
    }
}
