package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.EngineRegistry
import com.example.engine.SystemPlatformInfo
import com.example.ui.theme.DreamDarkBackground
import com.example.ui.theme.DreamDarkSurfaceBorder
import com.example.ui.theme.DreamDarkSurfaceCard
import com.example.ui.theme.DreamNeonCyan
import com.example.ui.theme.DreamNeonViolet
import com.example.ui.theme.DreamTextMuted
import com.example.ui.theme.DreamTextPrimary
import com.example.ui.theme.DreamTextSecondary

@Composable
fun SupportedSystemsScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DreamDarkBackground)
    ) {
        // Top Header
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Gamepad,
                contentDescription = null,
                tint = DreamNeonCyan,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "دليل الأنظمة والصيغ",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    ),
                    color = DreamTextPrimary
                )
                Text(
                    text = "صيغ ملفات الألعاب والنوى المدعومة في DreamBox",
                    style = MaterialTheme.typography.bodySmall,
                    color = DreamTextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Informative note
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(DreamNeonViolet.copy(alpha = 0.12f))
                .border(1.dp, DreamNeonViolet.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = DreamNeonCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "تدعم DreamBox التعرف التلقائي على هذه الامتدادات عند اختيارك لأي ملف. في الإصدارات التالية، سيتم استدعاء النوى المقابلة مباشرة.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DreamTextSecondary,
                    lineHeight = 18.sp
                )
            }
        }

        // List of platforms
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = EngineRegistry.supportedPlatforms,
                key = { it.code }
            ) { platform ->
                PlatformInfoCard(platform)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlatformInfoCard(platform: SystemPlatformInfo) {
    val platformColor = try {
        Color(android.graphics.Color.parseColor(platform.badgeColorHex))
    } catch (e: Exception) {
        DreamNeonCyan
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DreamDarkSurfaceCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DreamDarkSurfaceBorder, RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(platformColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = platform.nameAr,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DreamTextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(platformColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = platform.code,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = platformColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = platform.nameEn,
                style = MaterialTheme.typography.bodySmall,
                color = DreamTextMuted,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = platform.descriptionAr,
                style = MaterialTheme.typography.bodySmall,
                color = DreamTextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Extensions flow row
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                platform.extensions.forEach { ext ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DreamDarkBackground)
                            .border(1.dp, DreamDarkSurfaceBorder, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = ".$ext",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DreamNeonCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "النواة المتوافقة: ",
                    fontSize = 11.sp,
                    color = DreamTextMuted
                )
                Text(
                    text = platform.targetCore,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DreamNeonViolet
                )
            }
        }
    }
}
