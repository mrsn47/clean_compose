package com.example.compose_clean.ui.view.restaurants.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.compose_clean.R
import com.example.compose_clean.data.db.model.RestaurantEntity
import com.example.compose_clean.ui.theme.Typography
import com.example.compose_clean.ui.theme.darkGray
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun RestaurantItem(
    restaurant: RestaurantEntity
) {

    Card(
        elevation = 30.dp,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlideImage(
                    imageModel = restaurant.mainImageDownloadUrl,
                    // Crop, Fit, Inside, FillHeight, FillWidth, None
                    contentScale = ContentScale.Fit,
                    // shows a placeholder while loading the image.
                    loading = {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            color = MaterialTheme.colors.secondary
                        )
                    },
                    failure = {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_restaurant_24),
                            tint = darkGray,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    circularReveal = CircularReveal(500)
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .fillMaxWidth()
                    .align(Alignment.Top)
            ) {
                Text(text = restaurant.name, style = Typography.h5)
                Text(text = restaurant.address, style = Typography.body2)
            }
        }
    }
}