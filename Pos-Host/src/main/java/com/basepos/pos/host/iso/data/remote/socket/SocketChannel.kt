package com.basepos.pos.host.iso.data.remote.socket

import com.basepos.pos.common.domain.repository.SessionManager
import com.basepos.pos.host.BuildConfig
import com.basepos.pos.host.iso.transaction.IsoPackager
import org.jpos.iso.channel.PostChannel
import org.jpos.util.Logger
import org.jpos.util.SimpleLogListener
import javax.inject.Inject

/**
 * @Author: ifechukwu.udorji
 * @Date: 7/3/2024
 */
class SocketChannel @Inject constructor(
    private val sessionManager: SessionManager,
    private val transactionPackager: IsoPackager
) {
    fun setup(): PostChannel {
        val terminalParameters = sessionManager.getHostParameters()!!
        val channel =
            PostChannel(
                terminalParameters.serverIP,
                terminalParameters.port,
                transactionPackager
            )

        if (BuildConfig.DEBUG) {
            val logger = Logger()
            logger.addListener(SimpleLogListener(System.out))
            channel.setLogger(logger, "channel")
        }

        channel.timeout = 60000
        if (terminalParameters.enableSSL) channel.socketFactory = TcpSsLConnection()
        return channel
    }
}