package com.example.compose_clean.ui.composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.runtime.Composable

@Composable
fun CcBottomAppBar(
  backArrowClicked: (() -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {}
) {
  BottomAppBar(
    cutoutShape = CircleShape
  ) {

  }
}