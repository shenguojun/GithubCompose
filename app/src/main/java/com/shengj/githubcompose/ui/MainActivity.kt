package com.shengj.githubcompose.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.shengj.githubcompose.ui.popular.PopularReposScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // start OAuthCallbackActivity
        // val intent = Intent(this, LoginActivity::class.java)
        // startActivity(intent)
        setContent {
            PopularReposScreen()
        }
    }
}