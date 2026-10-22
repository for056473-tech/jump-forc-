package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DreamDarkBackground
import com.example.ui.theme.DreamDarkSurfaceBorder
import com.example.ui.theme.DreamDarkSurfaceCard
import com.example.ui.theme.DreamError
import com.example.ui.theme.DreamNeonCyan
import com.example.ui.theme.DreamNeonEmerald
import com.example.ui.theme.DreamNeonViolet
import com.example.ui.theme.DreamTextMuted
import com.example.ui.theme.DreamTextPrimary
import com.example.ui.theme.DreamTextSecondary
import com.example.ui.viewmodel.DreamBoxViewModel

@Composable
fun SettingsScreen(
    viewModel: DreamBoxViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

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
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = DreamNeonCyan,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "الإعدادات ومعلومات التطبيق",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    ),
                    color = DreamTextPrimary
                )
                Text(
                    text = "معلومات الإصدار وسياسة الوصول والبيانات",
                    style = MaterialTheme.typography.bodySmall,
                    color = DreamTextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Identity Card
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
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(DreamNeonViolet, DreamNeonCyan)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Extension,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "DreamBox Emulator",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DreamTextPrimary
                            )
                            Text(
                                text = "الإصدار 1.0 (إصدار البنية الأساسية للمكتبة)",
                                style = MaterialTheme.typography.bodySmall,
                                color = DreamNeonCyan,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "واجهة أمامية (Frontend) حديثة وأصلية مصممة باستخدام Jetpack Compose لإدارة وتنظيم ملفات الألعاب والـ ROMs المحلية بطريقة أنيقة وآمنة.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DreamTextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }

            // Storage Policy & Security (Requirement #11)
            SettingsInfoSection(
                icon = Icons.Default.Security,
                title = "سياسة التخزين والخصوصية",
                badgeText = "توافق كامل مع Google Play",
                badgeColor = DreamNeonEmerald,
                description = "يستخدم DreamBox إطار عمل التخزين الآمن من Google (Storage Access Framework - SAF).\n\n• لا نطلب أبدًا إذن الوصول الشامل لجميع الملفات (MANAGE_EXTERNAL_STORAGE).\n• لا نقوم بتعديل أو حذف أو كتابة أي بيانات داخل ملفات ألعابك.\n• يحتفظ التطبيق فقط بصلاحية قراءة الملفات التي تختارها أنت يدويًا عبر منتقي ملفات النظام."
            )

            // Future Emulation Architecture (Requirement #16)
            SettingsInfoSection(
                icon = Icons.Default.Build,
                title = "بنية محرك الألعاب المستقبلي",
                badgeText = "جاهز للتكامل",
                badgeColor = DreamNeonCyan,
                description = "تمت هندسة التطبيق وفق بنية معمارية منفصلة تمامًا (MVVM + Repository + EmulatorEngine Interface).\n\nعند إضافة نوى تشغيل مثل نوى Libretro (Cores) في الإصدارات القادمة، ستعمل فورًا مع جميع الألعاب المسجلة في مكتبتك دون الحاجة لإعادة بنائها أو إعادة استيرادها."
            )

            // Clear Data Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DreamDarkSurfaceCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DreamError.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            tint = DreamError,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "إدارة بيانات المكتبة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DreamTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "يمكنك تفريغ قائمة الألعاب المسجلة في التطبيق وإلغاء صلاحيات الـ URI المحفوظة بنقرة واحدة. ملفاتك على هاتفك ستبقى سليمة كما هي.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DreamTextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.promptClearLibrary() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("clear_library_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DreamError.copy(alpha = 0.18f),
                            contentColor = DreamError
                        )
                    ) {
                        Text(
                            text = "مسح جميع ألعاب المكتبة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun SettingsInfoSection(
    icon: ImageVector,
    title: String,
    badgeText: String,
    badgeColor: Color,
    description: String
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DreamDarkSurfaceCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DreamDarkSurfaceBorder, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = DreamNeonViolet,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DreamTextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = DreamTextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}
