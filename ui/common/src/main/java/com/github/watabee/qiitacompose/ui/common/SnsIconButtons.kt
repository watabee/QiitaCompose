package com.github.watabee.qiitacompose.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.watabee.qiitacompose.ui.theme.QiitaTheme

@Composable
fun SnsIconButtons(githubLoginName: String?, twitterScreenName: String?, facebookId: String?, linkedinId: String?) {
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
@Preview(name = "SnsIconButtons", showBackground = true, backgroundColor = 0xFFFFFFFF)
private fun PreviewSnsIcons() {
    QiitaTheme {
        SnsIconButtons(githubLoginName = "watabee", twitterScreenName = "watabee", facebookId = "watabee", linkedinId = "watabee")
    }
}
