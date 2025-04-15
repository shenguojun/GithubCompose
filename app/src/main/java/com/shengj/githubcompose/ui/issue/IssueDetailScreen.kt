package com.shengj.githubcompose.ui.issue

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.shengj.githubcompose.data.model.Issue

@Composable
fun IssueDetailScreen(
    owner: String,
    repoName: String,
    issueNumber: Int,
    viewModel: IssueDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(owner, repoName, issueNumber) {
        viewModel.loadIssueDetail(owner, repoName, issueNumber)
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "$owner / $repoName #$issueNumber",
                        color = Color.Black,
                        style = MaterialTheme.typography.subtitle1
                    )
                },
                backgroundColor = Color.White,
                elevation = 0.dp,
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: 实现分享功能 */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "分享",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = { /* TODO: 实现更多菜单 */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "更多",
                            tint = Color.Black
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "未知错误",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                uiState.issue != null -> {
                    IssueDetailContent(issue = uiState.issue!!)
                }
            }
        }
    }
}

@Composable
private fun IssueDetailContent(issue: Issue) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 标题
        Text(
            text = issue.title,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 状态标签
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF2EA44F))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = "打开",
                color = Color.White,
                style = MaterialTheme.typography.body2
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 作者信息
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(issue.user.avatarUrl),
                contentDescription = "用户头像",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                Text(
                    text = issue.user.login,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "现在",  // TODO: 格式化时间
                    style = MaterialTheme.typography.caption,
                    color = Color.Gray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 内容
        if (!issue.body.isNullOrBlank()) {
            Text(
                text = issue.body,
                style = MaterialTheme.typography.body1
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 表情回应按钮
        OutlinedButton(
            onClick = { /* TODO: 实现表情回应 */ },
            modifier = Modifier.height(36.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEmotions,
                contentDescription = "添加表情回应",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 子问题区域
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "子问题",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Medium
            )
            IconButton(onClick = { /* TODO: 添加子问题 */ }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加子问题",
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
} 