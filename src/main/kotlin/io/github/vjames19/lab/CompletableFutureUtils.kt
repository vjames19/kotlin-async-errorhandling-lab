package io.github.vjames19.lab

import java.util.concurrent.CompletableFuture

/**
 * Created by victor.reventos on 6/30/17.
 */
inline fun <T> immediateFuture(block: () -> T): CompletableFuture<T> {
    return try {
        CompletableFuture.completedFuture(block())
    } catch (e: Exception) {
        CompletableFuture<T>().apply {
            completeExceptionally(e)
        }
    }
}


inline fun <A, B> CompletableFuture<A>.map(crossinline f: (A) -> B): CompletableFuture<B> = thenApply { f(it) }

inline fun <A, B> CompletableFuture<A>.flatMap(crossinline f: (A) -> CompletableFuture<B>): CompletableFuture<B> = thenCompose { f(it) }

inline fun <A> CompletableFuture<A>.recover(crossinline f: (Throwable) -> A): CompletableFuture<A> = exceptionally { f(it) }