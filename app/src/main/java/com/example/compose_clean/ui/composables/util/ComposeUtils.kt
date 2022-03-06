package com.example.compose_clean.ui.composables.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.example.compose_clean.ui.view.states.GenericError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

inline fun Modifier.noRippleClickable(crossinline onClick: ()->Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

@Composable
fun GenericError?.CreateSnackbar(scope: CoroutineScope, scaffoldState: ScaffoldState) {
    this?.let {
        LaunchedEffect(it.timestamp) {
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar(it.error)
            }
        }
    }
}

@Composable
fun Int.spToDp() = with(LocalDensity.current) {
    this@spToDp.sp.toDp()
}
