package com.example.compose_clean.ui.view.restaurants.list

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose_clean.R
import com.example.compose_clean.data.db.model.RestaurantEntity
import com.example.compose_clean.ui.theme.Typography
import com.example.compose_clean.ui.theme.bordeaux
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun RestaurantItem(
    restaurant: RestaurantEntity,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    cutCornerSize: Dp = 30.dp,
) {

    Card(
        elevation = 8.dp,
        modifier = Modifier
            .padding(vertical = 0.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
    ) {
        Row(
            modifier = Modifier
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
                    placeHolder = painterResource(R.drawable.ic_baseline_restaurant_24),
                    // shows an error ImageBitmap when the request failed.
                    error = painterResource(R.drawable.ic_baseline_error_outline_24),
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