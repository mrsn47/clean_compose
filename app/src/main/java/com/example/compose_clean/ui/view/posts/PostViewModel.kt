package com.example.compose_clean.ui.view.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose_clean.common.states.ResultState
import com.example.compose_clean.domain.model.Post
import com.example.compose_clean.domain.usecase.PostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class PostViewModel @Inject constructor(
    private val postUseCase: PostUseCase
) : ViewModel() {

    //    private val _state = MutableStateFlow<State>(State.Result.Data<Post>())
    private val _state = MutableStateFlow<ResultState>(ResultState.ProgressState.LoadingProgressState)
    val state: StateFlow<ResultState> = _state

    fun refresh() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _state.value = ResultState.ProgressState.LoadingProgressState
                val post = Post("Title1", "Description1")
                val data = ResultState.Result.Data(post)
                _state.value = data
            }
        }
    }

    init {
        refresh()
    }
}


