package com.github.watabee.qiitacompose.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import coil.transform.CircleCropTransformation
import com.github.watabee.qiitacompose.data.UserData
import com.github.watabee.qiitacompose.ui.items.ItemsRouting
import com.github.watabee.qiitacompose.ui.items.ItemsScreen
import com.github.watabee.qiitacompose.ui.theme.QiitaFontFamily
import com.github.watabee.qiitacompose.ui.util.lifecycleAwareFlow
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun HomeScreen(scaffoldState: ScaffoldState = rememberScaffoldState(), homeRouting: HomeRouting, itemsRouting: ItemsRouting) {
    val viewModel: HomeViewModel = hiltNavGraphViewModel()
    val userData: UserData? by viewModel.userData.lifecycleAwareFlow().collectAsState(initial = null)

    HomeScreen(
        scaffoldState = scaffoldState,
        itemsRouting = itemsRouting,
        userData = userData,
        openLoginScreen = homeRouting.openLoginScreen
    )
}

@Composable
private fun HomeScreen(
    scaffoldState: ScaffoldState,
    itemsRouting: ItemsRouting,
    userData: UserData?,
    openLoginScreen: () -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Qiita", fontFamily = QiitaFontFamily.codecCold, fontWeight = FontWeight.Bold)
                },
                actions = {
                    if (userData != null) {
                        IconButton(onClick = { /* TODO */ }) {
                            val imageUrl = userData.imageUrl
                            if (!imageUrl.isNullOrBlank()) {
                                Image(
                                    painter = rememberCoilPainter(
                                        request = imageUrl,
                                        requestBuilder = {
                                            transformations(CircleCropTransformation())
                                        }
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp)
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_blank_user),
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    } else {
                        IconButton(onClick = openLoginScreen) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_blank_user),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            )
        },
        content = {
            ItemsScreen(itemsRouting)
        }
    )
}
