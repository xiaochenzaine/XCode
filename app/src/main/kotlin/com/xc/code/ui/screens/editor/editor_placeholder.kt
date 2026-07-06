package com.xc.code.ui.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xc.code.R
import com.xc.code.ui.theme.app_theme_provider

@Composable
fun empty_editor_placeholder(
    text: String,
    modifier: Modifier = Modifier,
    on_browse_files: (() -> Unit)? = null
) {
    val colors = app_theme_provider.colors
    Box(
        modifier = modifier.background(colors.editor_bg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_code_tag),
                contentDescription = null,
                tint = colors.editor_capsule_icon,
                modifier = Modifier.size(88.dp)
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.app_name),
                color = colors.editor_text,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            if (on_browse_files != null) {
                Spacer(modifier = Modifier.height(30.dp))
                Surface(
                    modifier = Modifier
                        .height(56.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = on_browse_files),
                    shape = RoundedCornerShape(20.dp),
                    color = colors.editor_browse_files_button_bg,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 28.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = colors.editor_browse_files_button_content,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.editor_browse_files),
                            color = colors.editor_browse_files_button_content,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
