package com.xc.code.ui.dialogs.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.xc.code.R

@Composable
fun manage_storage_dialog(
    on_confirm: () -> Unit,
    on_deny: () -> Unit
) {
    Dialog(
        onDismissRequest = on_deny,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        permission_dialog_card(
            title = stringResource(R.string.permission_all_files_title),
            message = stringResource(R.string.permission_all_files_message),
            cancel_text = stringResource(R.string.common_exit),
            confirm_text = stringResource(R.string.permission_go_setting),
            on_cancel = on_deny,
            on_confirm = on_confirm
        )
    }
}

@Composable
fun permission_rationale_dialog(
    on_confirm: () -> Unit,
    on_deny: () -> Unit
) {
    Dialog(
        onDismissRequest = on_deny,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        permission_dialog_card(
            title = stringResource(R.string.permission_storage_title),
            message = stringResource(R.string.permission_storage_message),
            cancel_text = stringResource(R.string.permission_deny),
            confirm_text = stringResource(R.string.permission_allow),
            on_cancel = on_deny,
            on_confirm = on_confirm
        )
    }
}

@Composable
fun permission_denied_dialog(
    on_retry: () -> Unit,
    on_exit: () -> Unit
) {
    Dialog(
        onDismissRequest = on_exit,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        permission_dialog_card(
            title = stringResource(R.string.permission_denied_title),
            message = stringResource(R.string.permission_denied_message),
            cancel_text = stringResource(R.string.common_exit),
            confirm_text = stringResource(R.string.common_retry),
            on_cancel = on_exit,
            on_confirm = on_retry
        )
    }
}

@Composable
private fun permission_dialog_card(
    title: String,
    message: String,
    cancel_text: String,
    confirm_text: String,
    on_cancel: () -> Unit,
    on_confirm: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = on_cancel) {
                    Text(cancel_text, color = Color.Gray)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = on_confirm,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004DEA))
                ) {
                    Text(confirm_text, color = Color.White)
                }
            }
        }
    }
}
