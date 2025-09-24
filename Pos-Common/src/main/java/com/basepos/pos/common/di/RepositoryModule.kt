package com.basepos.pos.common.di

import com.basepos.pos.common.data.repository.SessionManagerImpl
import com.basepos.pos.common.domain.repository.SessionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */
@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindSessionManager(sessionManagerImpl: SessionManagerImpl): SessionManager
}