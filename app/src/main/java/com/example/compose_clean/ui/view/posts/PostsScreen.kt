package com.example.compose_clean.ui.view.posts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.compose_clean.ui.view.login.SessionViewModel
import com.example.compose_clean.common.states.ResultState
import com.example.compose_clean.domain.model.Post

@Composable
fun PostsScreen(
    navController: NavController,
    postViewModel: PostViewModel = hiltViewModel(),
    authViewModel: SessionViewModel = hiltViewModel()
) {

    val state = postViewModel.state.collectAsState()
    when (state.value) {
//        is ViewState.Result.Data<*> -> PostList(postList = (state.value as ViewState.Result.Data<*>).data as List<Post>)
        is ResultState.Result.Error -> {}
        is ResultState.ProgressState.EmptyProgressState -> {}
        is ResultState.ProgressState.LoadedProgressState -> {}
        is ResultState.ProgressState.LoadingProgressState -> {}

    }
    PostList(postList = listOf(Post("Title1","Descrt1")))
}

@Composable
fun PostList(postList: List<Post>) {

    Scaffold(

    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(postList) { post ->
                PostItem(
                    post = post,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                        }
//                    onDeleteClick = {
//                        viewModel.onEvent(NotesEvent.DeleteNote(note))
//                        scope.launch {
//                            val result = scaffoldState.snackbarHostState.showSnackbar(
//                                message = "Note deleted",
//                                actionLabel = "Undo"
//                            )
//                            if (result == SnackbarResult.ActionPerformed) {
//                                viewModel.onEvent(NotesEvent.RestoreNote)
//                            }
//                        }
//                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

}