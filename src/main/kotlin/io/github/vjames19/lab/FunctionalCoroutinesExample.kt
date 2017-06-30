package io.github.vjames19.lab

import io.github.vjames19.lab.service.FunctionalProjectService
import io.github.vjames19.lab.service.FunctionalUserService
import io.github.vjames19.lab.service.ProjectServiceError
import io.github.vjames19.lab.service.UserNotFoundProjectServiceError
import kotlinx.coroutines.experimental.future.await
import kotlinx.coroutines.experimental.runBlocking
import org.funktionale.either.Either
import org.funktionale.either.flatMap
import java.util.concurrent.CompletableFuture

/**
 * Created by victor.reventos on 6/30/17.
 */
class FunctionalCoroutinesExample {
    val userService = FunctionalUserService()
    val projectService = FunctionalProjectService()

    fun dependent() {
        runBlocking {
            val user = userService.get(1).await()
            user.leftMap(this@FunctionalCoroutinesExample::mapError)
                    .flatMap { runBlocking { projectService.getProjectsForUser(it.id).await() } }

        }
    }

    fun dependentWithFutureEither() {
        runBlocking {
            val user = userService.get(1)
                    .toFutureEither()
                    .leftMap { mapError(it) }
                    .future
                    .await()

            val projects = user.flatMap { runBlocking { projectService.getProjectsForUser(it.id).await() } }
        }

    }

    fun errorHandling() {
        runBlocking {
            val user = userService.get(1).awaitEither()
            val projects = projectService.getProjectsForUser(user.id).await()
                    .left()
                    .flatMap { projectFallback(it) }

            // do something with both

        }
    }

    fun concurrent() {
        runBlocking {
            val userFuture = userService.get(1)
            val projectsFuture = projectService.getProjectsForUser(1)

            val user = userFuture.await()
            val projects = userFuture.await()
        }
    }

    fun usingCombine() {
        runBlocking {

            combine(userService.get(1).await(),
                    projectService.getProjectsForUser(1).await()) { user, project ->

                // do something with both
            }
        }
    }

    fun projectFallback(e: Throwable): Either<FunctionalExampleError, List<Project>> = if (e is ProjectServiceError) {
        when (e) {
            is UserNotFoundProjectServiceError -> Either.right(emptyList<Project>())
        }
    } else {
        Either.left(UnknownFunctionalExampleError)
    }

    fun <T> mapError(error: T): FunctionalExampleError = UnknownFunctionalExampleError
}

suspend fun <L, R> CompletableFuture<Either<L, R>>.awaitEither(): R = await().right().get()

inline fun <L, R, R2> Either<L, R>.flatMap(crossinline f: (R) -> Either<L, R2>): Either<L, R2> = right().flatMap { f(it) }

inline fun <L, R, R2> Either<L, R>.map(crossinline f: (R) -> R2): Either<L, R2> = right().map { f(it) }

inline fun <L, R, L2> Either<L, R>.leftMap(crossinline f: (L) -> L2): Either<L2, R> = left().map { f(it) }


fun <L, R1, R2, R> combine(e1: Either<L, R1>, e2: Either<L, R2>, f: (e1: R1, e2: R2) -> R): Either<L, R> {
    return e1.flatMap { a -> e2.flatMap { b -> Either.right(f(a, b)) } }
}