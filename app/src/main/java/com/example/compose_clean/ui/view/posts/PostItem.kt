package com.example.compose_clean.ui.view.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose_clean.domain.model.Post
import com.example.compose_clean.ui.theme.Typography

@Composable
fun PostItem(
    post: Post,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    cutCornerSize: Dp = 30.dp,
) {

    Card(
        elevation = 2.dp,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp).fillMaxWidth().background(MaterialTheme.colors.background)
    ) {
        Row {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = post.title, style = Typography.body1)
                Text(text = post.description, style = Typography.body2)
            }
        }
    }
}