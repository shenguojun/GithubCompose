package com.shengj.githubcompose.ui.repository

import android.util.Base64
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.model.MarkdownColors
import com.mikepenz.markdown.model.MarkdownTypography
import java.nio.charset.Charset

@Composable
fun RepositoryScreen(
    owner: String,
    repoName: String,
    viewModel: RepositoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToRaiseIssue: (owner: String, repoName: String) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(owner, repoName) {
        viewModel.loadRepositoryDetails(owner, repoName)
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text(repoName, color = Color.Black) },
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
                    TextButton(
                        onClick = { onNavigateToRaiseIssue(owner, repoName) }
                    ) {
                        Text("创建议题", color = MaterialTheme.colors.primary)
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
                    ErrorDisplay(message = uiState.error ?: "发生未知错误")
                }
                uiState.repository != null -> {
                    RepositoryContent(uiState = uiState)
                }
                else -> {
                     // Maybe an initial empty state or handle error where repo is null but no error message?
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                         Text("无法加载仓库信息")
                    }
                }
            }
        }
    }
}

@Composable
fun RepositoryContent(uiState: RepositoryUiState) {
    val repo = uiState.repository!!
    val scrollState = rememberScrollState()
    var decodedReadme by remember { mutableStateOf<String?>(null) }
    var isMarkdownVisible by remember { mutableStateOf(false) }

    // Decode README content when it becomes available or changes
    LaunchedEffect(uiState.readmeContent) {
        decodedReadme = uiState.readmeContent?.let {
            try {
                String(Base64.decode(it.replace("\n", ""), Base64.DEFAULT), Charset.forName("UTF-8"))
            } catch (e: Exception) {
                "无法解码 README: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(text = repo.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        repo.description?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, style = MaterialTheme.typography.body1, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = "Stars", tint = Color.Gray, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "${repo.stargazersCount}", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Icon(Icons.Default.ForkRight, contentDescription = "Forks", tint = Color.Gray, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "${repo.forksCount}", color = Color.Gray, fontSize = 14.sp)
            repo.language?.let {
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.Code, contentDescription = "Language", tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = it, color = Color.Gray, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Render Markdown README with lazy loading
        if (decodedReadme != null) {
            // Show a button to toggle markdown visibility
            if (!isMarkdownVisible) {
                TextButton(
                    onClick = { isMarkdownVisible = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("显示 README")
                }
            } else {
                // Use remember to prevent recomposition of markdown content
                val markdownContent = remember(decodedReadme) {
                    decodedReadme!!.take(10000) // Limit content size
                }
                
                Markdown(
                    content = markdownContent,
                    typography = markdownTypography(),
                    colors = markdownColor(),
                    imageTransformer = Coil3ImageTransformerImpl
                )
            }
        } else if (uiState.readmeContent == null && !uiState.isLoading) {
            Text("未找到 README 文件", color = Color.Gray)
        }
    }
}

@Composable
fun ErrorDisplay(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = "Error",
            tint = MaterialTheme.colors.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message, color = MaterialTheme.colors.error, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
fun markdownTypography(
    h1: TextStyle = MaterialTheme.typography.h4,
    h2: TextStyle = MaterialTheme.typography.h5,
    h3: TextStyle = MaterialTheme.typography.h6,
    h4: TextStyle = MaterialTheme.typography.h4,
    h5: TextStyle = MaterialTheme.typography.h5,
    h6: TextStyle = MaterialTheme.typography.h6,
    text: TextStyle = MaterialTheme.typography.body1,
    code: TextStyle = MaterialTheme.typography.body2.copy(fontFamily = FontFamily.Monospace),
    inlineCode: TextStyle = text.copy(fontFamily = FontFamily.Monospace),
    quote: TextStyle = MaterialTheme.typography.body2.plus(SpanStyle(fontStyle = FontStyle.Italic)),
    paragraph: TextStyle = MaterialTheme.typography.body1,
    ordered: TextStyle = MaterialTheme.typography.body1,
    bullet: TextStyle = MaterialTheme.typography.body1,
    list: TextStyle = MaterialTheme.typography.body1,
    link: TextStyle = MaterialTheme.typography.body1.copy(
        fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline
    ),
    textLink: TextLinkStyles = TextLinkStyles(style = link.toSpanStyle()),
    table: TextStyle = text,
): MarkdownTypography = DefaultMarkdownTypography(
    h1 = h1,
    h2 = h2,
    h3 = h3,
    h4 = h4,
    h5 = h5,
    h6 = h6,
    text = text,
    quote = quote,
    code = code,
    inlineCode = inlineCode,
    paragraph = paragraph,
    ordered = ordered,
    bullet = bullet,
    list = list,
    link = link,
    textLink = textLink,
    table = table,
)

@Deprecated("Use `markdownColor` without text colors instead. Please set text colors via `markdownTypography`. This will be removed in a future release.")
@Composable
fun markdownColor(
    text: Color = MaterialTheme.colors.onBackground,
    codeText: Color = Color.Unspecified,
    inlineCodeText: Color = Color.Unspecified,
    linkText: Color = Color.Unspecified,
    codeBackground: Color = MaterialTheme.colors.onBackground.copy(alpha = 0.1f),
    inlineCodeBackground: Color = codeBackground,
    dividerColor: Color = MaterialTheme.colors.primaryVariant,
    tableText: Color = Color.Unspecified,
    tableBackground: Color = MaterialTheme.colors.onBackground.copy(alpha = 0.02f),
): MarkdownColors = DefaultMarkdownColors(
    text = text,
    codeText = codeText,
    inlineCodeText = inlineCodeText,
    linkText = linkText,
    codeBackground = codeBackground,
    inlineCodeBackground = inlineCodeBackground,
    dividerColor = dividerColor,
    tableText = tableText,
    tableBackground = tableBackground,
)