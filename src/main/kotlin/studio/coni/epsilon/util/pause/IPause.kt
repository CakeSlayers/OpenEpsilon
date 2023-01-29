package studio.coni.epsilon.util.pause

import studio.coni.epsilon.common.AbstractModule

interface IPause {
    fun requestPause(module: AbstractModule): Boolean
}

interface ITimeoutPause : IPause {
    override fun requestPause(module: AbstractModule): Boolean {
        return requestPause(module, 50L)
    }

    fun requestPause(module: AbstractModule, timeout: Int): Boolean {
        return requestPause(module, timeout.toLong())
    }

    fun requestPause(module: AbstractModule, timeout: Long): Boolean
}