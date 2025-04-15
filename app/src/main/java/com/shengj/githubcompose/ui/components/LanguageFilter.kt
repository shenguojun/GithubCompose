package com.shengj.githubcompose.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LanguageFilter(
    selectedLanguage: String?,
    onLanguageSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val languages = listOf(
        "Kotlin",
        "Java",
        "Python",
        "JavaScript",
        "TypeScript",
        "Go",
        "Rust",
        "Swift",
        "C++",
        "C#"
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedLanguage == null,
                onClick = { onLanguageSelected(null) },
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text("全部")
            }
        }
        items(languages) { language ->
            FilterChip(
                selected = selectedLanguage == language,
                onClick = { onLanguageSelected(language) },
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(language)
            }
        }
    }
} 