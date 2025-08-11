package org.example.project

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object IOScope {
    val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
}

interface Closeable { fun close() }

fun <T> StateFlow<T>.watch(block: (T) -> Unit): Closeable {
    val job = IOScope.scope.launch {
        collect { value -> block(value) }
    }
    return object : Closeable {
        override fun close() = job.cancel()
    }
}