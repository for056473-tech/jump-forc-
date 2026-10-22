package com.example.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.GameItem
import com.example.ui.theme.DreamDarkSurfaceCard
import com.example.ui.theme.DreamError
import com.example.ui.theme.DreamTextPrimary
import com.example.ui.theme.DreamTextSecondary

@Composable
fun DeleteGameConfirmationDialog(
    game: GameItem,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = DreamDarkSurfaceCard,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = DreamError
            )
        },
        title = {
            Text(
                text = "إزالة اللعبة من المكتبة؟",
                color = DreamTextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "سيتم إلغاء تسجيل «${game.fileName}» من مكتبة DreamBox فقط. لن يتم حذف الملف الأصلي من هاتفك مطلقًا.",
                color = DreamTextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DreamError,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_delete_button")
            ) {
                Text("إزالة من المكتبة", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("cancel_delete_button")
            ) {
                Text("إلغاء", color = DreamTextSecondary)
            }
        }
    )
}

@Composable
fun ClearLibraryConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = DreamDarkSurfaceCard,
        icon = {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = null,
                tint = DreamError
            )
        },
        title = {
            Text(
                text = "مسح جميع ألعاب المكتبة؟",
                color = DreamTextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "سيتم إفراغ قائمة الألعاب المسجلة في DreamBox وإلغاء صلاحيات الـ URI المحفوظة. ملفاتك الأصلية على الهاتف لن تتأثر إطلاقًا.",
                color = DreamTextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DreamError,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_clear_library_button")
            ) {
                Text("تأكيد المسح", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("cancel_clear_library_button")
            ) {
                Text("إلغاء", color = DreamTextSecondary)
            }
        }
    )
}
