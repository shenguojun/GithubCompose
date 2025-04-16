package com.shengj.githubcompose.di

import com.shengj.githubcompose.data.GithubRepository
import com.shengj.githubcompose.data.GithubRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGithubRepository(impl: GithubRepositoryImpl): GithubRepository
} 