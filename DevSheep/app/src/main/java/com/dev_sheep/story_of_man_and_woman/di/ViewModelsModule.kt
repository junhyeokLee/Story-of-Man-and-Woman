package com.dev_sheep.story_of_man_and_woman.di


import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {

    viewModel { FeedViewModel(get(), get()) }
    viewModel { MemberViewModel(get()) }
}
