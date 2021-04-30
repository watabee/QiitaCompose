package com.github.watabee.qiitacompose.ui.user

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import coil.transform.CircleCropTransformation
import com.github.watabee.qiitacompose.api.response.Tag
import com.github.watabee.qiitacompose.api.response.User
import com.github.watabee.qiitacompose.ui.common.ErrorScreen
import com.github.watabee.qiitacompose.ui.common.LoadingScreen
import com.github.watabee.qiitacompose.ui.theme.QiitaTheme
import com.github.watabee.qiitacompose.ui.theme.tagBackground
import com.github.watabee.qiitacompose.ui.util.lifecycleAwareFlow
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.flow.collect

private val LocalUserRouting = compositionLocalOf<UserRouting> {
    error("CompositionLocal LocalUserRouting not present")
}

@Composable
fun UserScreen(user: User, userRouting: UserRouting) {
    val context = LocalContext.current
    val viewModel: UserViewModel = hiltNavGraphViewModel()
    val state by viewModel.state.lifecycleAwareFlow().collectAsState(UserViewModel.State(isLoading = true))
    val dispatchAction = viewModel.dispatchAction
    val event = viewModel.event

    LaunchedEffect(user.id) {
        dispatchAction(UserViewModel.Action.GetUserInfo(user.id))
    }

    LaunchedEffect(event) {
        event.collect {
            handleEvent(context, it)
        }
    }

    when {
        state.isLoading -> {
            LoadingScreen()
        }
        state.getUserInfoError -> {
            ErrorScreen(onRetryButtonClicked = { dispatchAction(UserViewModel.Action.GetUserInfo(user.id)) })
        }
        else -> {
            CompositionLocalProvider(LocalUserRouting provides userRouting) {
                UserProfileScreen(user, state.isFollowingUser, state.followingTags)
            }
        }
    }
}

private fun handleEvent(context: Context, event: UserViewModel.Event) {
    when (event) {
        UserViewModel.Event.ShowFollowUserError -> {
            Toast.makeText(context, context.getString(R.string.user_follow_user_error_message), Toast.LENGTH_SHORT).show()
        }
        UserViewModel.Event.ShowUnfollowUserError -> {
            Toast.makeText(context, context.getString(R.string.user_unfollow_user_error_message), Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
private fun UserProfileScreen(user: User, isFollowingUser: Boolean, followingTags: List<Tag>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.requiredHeight(28.dp))
        Image(
            painter = rememberCoilPainter(
                request = user.profileImageUrl,
                requestBuilder = {
                    transformations(CircleCropTransformation())
                }
            ),
            contentDescription = null,
            modifier = Modifier.requiredSize(72.dp),
        )
        Spacer(modifier = Modifier.requiredHeight(16.dp))

        val username = user.name
        if (!username.isNullOrBlank()) {
            Text(text = username, fontWeight = FontWeight.W700, style = MaterialTheme.typography.subtitle2)
            Spacer(modifier = Modifier.requiredHeight(4.dp))
        }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(text = "@${user.id}", style = MaterialTheme.typography.body1)
        }
        SnsIcons(
            githubLoginName = user.githubLoginName,
            twitterScreenName = user.twitterScreenName,
            facebookId = user.facebookId,
            linkedinId = user.linkedinId
        )
        Spacer(modifier = Modifier.requiredHeight(16.dp))
        Divider(modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(modifier = Modifier.requiredHeight(8.dp))
        CounterList(itemsCount = user.itemsCount, followeesCount = user.followeesCount, followersCount = user.followersCount)

        val description = user.description
        if (!description.isNullOrBlank()) {
            Text(
                text = description,
                style = MaterialTheme.typography.body1,
                lineHeight = 24.sp,
                modifier = Modifier.padding(top = 24.dp),
                fontWeight = FontWeight.W400

            )
        }

        Spacer(modifier = Modifier.requiredHeight(24.dp))
        FollowButton(userId = user.id, isFollowingUser = isFollowingUser)

        FollowingTags(followingTags = followingTags)
    }
}

@Composable
private fun SnsIcons(githubLoginName: String?, twitterScreenName: String?, facebookId: String?, linkedinId: String?) {
    if (githubLoginName.isNullOrBlank() && twitterScreenName.isNullOrBlank() && facebookId.isNullOrBlank() && linkedinId.isNullOrBlank()) {
        return
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (!githubLoginName.isNullOrBlank()) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painter = painterResource(id = R.drawable.ic_user_github), contentDescription = null, tint = Color(0xFF333333))
            }
        }
        if (!twitterScreenName.isNullOrBlank()) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painter = painterResource(id = R.drawable.ic_user_twitter), contentDescription = null, tint = Color(0xFF1DA1F2))
            }
        }
        if (!facebookId.isNullOrBlank()) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painter = painterResource(id = R.drawable.ic_user_facebook), contentDescription = null, tint = Color(0xFF1778F2))
            }
        }
        if (!linkedinId.isNullOrBlank()) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painter = painterResource(id = R.drawable.ic_user_linkedin), contentDescription = null, tint = Color(0xFF0077B5))
            }
        }
    }
}

@Composable
private fun CounterList(itemsCount: Int, followeesCount: Int, followersCount: Int) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            val (itemsText, followeesText, followersText) = createRefs()
            Text(
                text = stringResource(id = R.string.user_items_count, itemsCount),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.constrainAs(itemsText) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(followeesText.start)
                },
                style = MaterialTheme.typography.caption
            )
            Text(
                text = stringResource(id = R.string.user_followees_count, followeesCount),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.constrainAs(followeesText) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                style = MaterialTheme.typography.caption
            )
            Text(
                text = stringResource(id = R.string.user_followers_count, followersCount),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.constrainAs(followersText) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(followeesText.end)
                    end.linkTo(parent.end)
                },
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Composable
private fun FollowButton(userId: String, isFollowingUser: Boolean) {
    val viewModel: UserViewModel = hiltNavGraphViewModel()
    val isLoggedIn by viewModel.isLoggedIn.lifecycleAwareFlow().collectAsState(initial = false)
    val userRouting = LocalUserRouting.current
    val onButtonClicked = {
        when {
            !isLoggedIn -> userRouting.openLoginScreen()
            isFollowingUser -> viewModel.dispatchAction(UserViewModel.Action.UnfollowUser(userId))
            else -> viewModel.dispatchAction(UserViewModel.Action.FollowUser(userId))
        }
    }

    if (isFollowingUser) {
        Button(
            onClick = onButtonClicked,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small.copy(all = CornerSize(0.dp))
        ) {
            Text(
                text = stringResource(id = R.string.user_now_following),
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.W700
            )
        }
    } else {
        OutlinedButton(
            onClick = onButtonClicked,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small.copy(all = CornerSize(0.dp)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium))
        ) {
            Text(text = stringResource(id = R.string.user_follow), style = MaterialTheme.typography.body2, fontWeight = FontWeight.W700)
        }
    }
}

@Composable
private fun FollowingTags(followingTags: List<Tag>) {
    if (followingTags.isEmpty()) {
        return
    }

    Spacer(modifier = Modifier.requiredHeight(48.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(painter = painterResource(id = R.drawable.ic_tags), contentDescription = null, tint = MaterialTheme.colors.primary)
        Spacer(modifier = Modifier.requiredWidth(8.dp))
        Text(text = stringResource(id = R.string.user_following_tags), style = MaterialTheme.typography.body2, fontWeight = FontWeight.W700)
    }
    Spacer(modifier = Modifier.requiredHeight(8.dp))
    FlowRow(modifier = Modifier.fillMaxWidth(), mainAxisSpacing = 8.dp, crossAxisSpacing = 8.dp) {
        followingTags.forEach { tag ->
            Text(
                text = tag.id,
                modifier = Modifier
                    .background(color = MaterialTheme.colors.tagBackground)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
private fun PreviewUserProfileScreen() {
    val user = User(
        id = "increqiita",
        profileImageUrl = "https://qiita-image-store.s3.amazonaws.com/0/190187/profile-images/1499068142",
        description = "Rubyが好きです。Railsも書いています。最近はElixirに興味があります。 Qiitaの開発をしています。",
        facebookId = "facebook",
        followeesCount = 2000,
        followersCount = 230,
        githubLoginName = "github",
        itemsCount = 122,
        linkedinId = "linkedin",
        location = null,
        name = "Innkuri Kiita",
        organization = null,
        twitterScreenName = "twitter",
        websiteUrl = null
    )

    val tags = listOf(
        Tag(id = "Android", itemsCount = 1000, followersCount = 100, iconUrl = ""),
        Tag(id = "iOS", itemsCount = 1000, followersCount = 100, iconUrl = "")
    )

    QiitaTheme {
        UserProfileScreen(user, isFollowingUser = true, followingTags = tags)
    }
}
