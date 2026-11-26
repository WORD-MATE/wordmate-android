package com.wordmate.android.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordmate.android.WordMateApplication
import com.wordmate.android.data.repository.WordMateRepository

@Composable
fun rememberRepository(): WordMateRepository =
    (LocalContext.current.applicationContext as WordMateApplication).repository

/** 저장소를 주입받는 ViewModel 생성 헬퍼. */
@Composable
inline fun <reified VM : ViewModel> wmViewModel(
    key: String? = null,
    crossinline create: (WordMateRepository) -> VM,
): VM {
    val repository = rememberRepository()
    return viewModel(key = key, factory = viewModelFactory { initializer { create(repository) } })
}
