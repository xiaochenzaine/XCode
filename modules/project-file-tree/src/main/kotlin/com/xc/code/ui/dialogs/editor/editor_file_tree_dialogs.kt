package com.xc.code.ui.dialogs.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xc.code.editor.model.editor_file_node
import com.xc.code.project_file_tree.project_file_tree_colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun editor_file_tree_delete_sheet(
    node: editor_file_node,
    colors: project_file_tree_colors,
    on_dismiss: () -> Unit,
    on_confirm: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = on_dismiss,
        containerColor = colors.dialog_bg,
        contentColor = colors.editor_text,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(stringResource(com.xc.code.project_file_tree.R.string.project_file_tree_delete_title, node.name), color = colors.editor_text, fontSize = 15.sp)
            val node_type_name = if (node.is_directory) stringResource(com.xc.code.project_file_tree.R.string.project_file_tree_type_folder) else stringResource(com.xc.code.project_file_tree.R.string.project_file_tree_type_file)
            Text(stringResource(com.xc.code.project_file_tree.R.string.project_file_tree_delete_message, node_type_name), color = colors.editor_hint, fontSize = 12.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true),
                            onClick = on_dismiss
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(com.xc.code.project_file_tree.R.string.project_file_tree_cancel), color = colors.editor_hint, fontSize = 13.sp)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.danger_bg)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true),
                            onClick = on_confirm
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(com.xc.code.project_file_tree.R.string.project_file_tree_delete), color = colors.danger, fontSize = 13.sp)
                }
            }
        }
    }
}

