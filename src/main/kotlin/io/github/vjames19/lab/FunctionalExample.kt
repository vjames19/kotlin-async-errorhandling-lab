package io.github.vjames19.lab

import io.github.vjames19.lab.service.FunctionalProjectService
import io.github.vjames19.lab.service.FunctionalUserService
import io.github.vjames19.lab.service.ProjectServiceError
import io.github.vjames19.lab.service.UserNotFoundProjectServiceError
import org.funktionale.either.Either
import org.funktionale.either.flatMap
import java.util.concurrent.CompletableFuture

/**
 * Created by victor.reventos on 6/30/17.
 */
class FunctionalExample {
    val userService = FunctionalUserService()
    val projectService = FunctionalProjectService()

    fun dependent(): CompletableFuture<Either<FunctionalExampleError, List<Project>>> {
        return userService.get(1)
                .toFutureEither()
                .leftMap(this::mapError)
                .flatMap {
                    projectService.getProjectsForUser(it.id)
                            .toFutureEither()
                            .leftMap(this::mapError)
                }.future
    }

    fun concurrent() {
        val userFuture = userService.get(1)
        val projectsFuture = projectService.getProjectsForUser(1)

        CompletableFuture.allOf(userFuture, projectsFuture).map {
            val user = userFuture.get()
            val projects = projectsFuture.get()
            // do something with both
        }
    }

    fun errorHandling() {
        val userFuture = userService.get(1)

        // fallback to empty list
        val projectsFuture = projectService.getProjectsForUser(1).map {
            it.left().flatMap {
                when (it) {
                    is UserNotFoundProjectServiceError -> Either.Right<ProjectServiceError, List<Project>>(emptyList<Project>())
                }
            }
        }

        CompletableFuture.allOf(userFuture, projectsFuture).map {
            val user = userFuture.get()
            val projects = projectsFuture.get()

            // do something with both
        }
    }

    fun <T> mapError(error: T): FunctionalExampleError = UnknownFunctionalExampleError
}

sealed class FunctionalExampleError(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

object UnknownFunctionalExampleError : FunctionalExampleError()


class FutureEither<L, R>(val future: CompletableFuture<Either<L, R>>) {

    fun <L2> leftMap(f: (L) -> L2): FutureEither<L2, R> {
        return FutureEither(future.map { it.left().map(f) })
    }

    fun <R2> flatMap(f: (R) -> FutureEither<L, R2>): FutureEither<L, R2> {
        val newFuture: CompletableFuture<Either<L, R2>> = future.flatMap<Either<L, R>, Either<L, R2>> {
            when (it) {
                is Either.Right -> f(it.r).future
                is Either.Left -> CompletableFuture.completedFuture(Either.Left<L, R2>(it.l))
            }

        }
        return FutureEither(newFuture)
    }
}

fun <L, R> CompletableFuture<Either<L, R>>.toFutureEither(): FutureEither<L, R> = FutureEither(this)