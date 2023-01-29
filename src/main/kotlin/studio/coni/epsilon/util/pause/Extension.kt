package studio.coni.epsilon.util.pause

import studio.coni.epsilon.common.AbstractModule
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun <R> IPause.withPause(module: AbstractModule, block: () -> R): R? {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return if (requestPause(module)) block.invoke()
    else null
}

inline fun <R> PriorityTimeoutPause.withPause(module: AbstractModule, timeout: Int, block: () -> R): R? =
    withPause(module, timeout.toLong(), block)

@OptIn(ExperimentalContracts::class)
inline fun <R> PriorityTimeoutPause.withPause(module: AbstractModule, timeout: Long, block: () -> R): R? {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    return if (requestPause(module, timeout)) block.invoke()
    else null
}