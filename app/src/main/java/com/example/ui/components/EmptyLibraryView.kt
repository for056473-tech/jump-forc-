package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DreamDarkSurfaceBorder
import com.example.ui.theme.DreamDarkSurfaceCard
import com.example.ui.theme.DreamNeonCyan
import com.example.ui.theme.DreamNeonViolet
import com.example.ui.theme.DreamNeonVioletLight
import com.example.ui.theme.DreamTextMuted
import com.example.ui.theme.DreamTextPrimary
import com.example.ui.theme.DreamTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmptyLibraryView(
    onAddGameClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Glowing futuristic gaming icon container
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            DreamNeonViolet.copy(alpha = 0.35f),
                            DreamNeonCyan.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
                .border(2.dp, DreamNeonViolet.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Gamepad,
                contentDescription = "أيقونة يد التحكم",
                tint = DreamNeonCyan,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mandatory empty library message
        Text(
            text = "مكتبتك فارغة، أضف ألعابك للبدء",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = DreamTextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "اختر ملفات الألعاب أو النسخ الاحتياطية (ROMs) من وحدة التخزين بجهازك باستخدام منتقي الملفات الآمن.",
            style = MaterialTheme.typography.bodyMedium,
            color = DreamTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Add Game Button
        Button(
            onClick = onAddGameClick,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(54.dp)
                .testTag("empty_add_game_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DreamNeonViolet,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "إضافة لعبة الآن",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Supported formats showcase
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(DreamDarkSurfaceCard)
                .border(1.dp, DreamDarkSurfaceBorder, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "الصيغ المدعومة للاستيراد:",
                    style = MaterialTheme.typography.labelLarge,
                    color = DreamNeonVioletLight,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val formats = listOf(
                        ".iso" to "PS1 / PSP",
                        ".chd" to "PS1",
                        ".bin/.cue" to "PS1",
                        ".gba" to "GBA",
                        ".nds" to "NDS",
                        ".nes" to "NES",
                        ".sfc" to "SNES",
                        ".n64" to "N64",
                        ".zip" to "أرشيف"
                    )
                    formats.forEach { (ext, name) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DreamNeonViolet.copy(alpha = 0.12f))
                                .border(1.dp, DreamNeonCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ext,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DreamNeonCyan
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "($name)",
                                    fontSize = 11.sp,
                                    color = DreamTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = DreamNeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "وصول آمن (SAF): لا يتم طلب إذن التخزين الشامل ولا نعدل ملفاتك.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DreamTextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
