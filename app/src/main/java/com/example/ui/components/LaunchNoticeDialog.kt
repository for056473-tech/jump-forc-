package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.LaunchResult
import com.example.ui.theme.DreamDarkSurfaceBorder
import com.example.ui.theme.DreamDarkSurfaceCard
import com.example.ui.theme.DreamNeonCyan
import com.example.ui.theme.DreamNeonViolet
import com.example.ui.theme.DreamTextMuted
import com.example.ui.theme.DreamTextPrimary
import com.example.ui.theme.DreamTextSecondary

@Composable
fun LaunchNoticeDialog(
    notice: LaunchResult.EngineNotInstalled,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = DreamDarkSurfaceCard,
        titleContentColor = DreamTextPrimary,
        textContentColor = DreamTextSecondary,
        icon = {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(DreamNeonViolet.copy(alpha = 0.2f))
                    .border(1.5.dp, DreamNeonCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = DreamNeonCyan,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = notice.message, // "لا يوجد محرك متوافق مثبت"
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = DreamTextPrimary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = notice.detailsAr,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DreamTextSecondary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Core & Platform Info Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F0C20))
                        .border(1.dp, DreamDarkSurfaceBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = DreamNeonViolet,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "النظام المطلوب:",
                                fontSize = 12.sp,
                                color = DreamTextMuted
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = notice.engineName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DreamTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "النواة المستهدفة (Core):",
                                fontSize = 12.sp,
                                color = DreamTextMuted
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = notice.suggestedCore,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DreamNeonCyan
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DreamNeonViolet,
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("dismiss_launch_notice_button")
            ) {
                Text("حسناً، فهمت", fontWeight = FontWeight.Bold)
            }
        }
    )
}
