package com.example.compose_clean.ui.composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun CcTopAppBar(
    backArrowClicked: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {},
        backgroundColor = MaterialTheme.colors.secondary,
        contentColor = Color.White,
        navigationIcon = backArrowClicked?.let {
            return@let {
                IconButton(onClick = { backArrowClicked() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = actions
    )
}