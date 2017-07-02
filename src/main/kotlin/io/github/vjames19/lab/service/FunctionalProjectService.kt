package io.github.vjames19.lab.service

import io.github.vjames19.futures.jdk8.Future
import io.github.vjames19.lab.Project
import io.github.vjames19.lab.Repo
import org.funktionale.either.Either
import org.funktionale.either.toEitherRight
import java.util.concurrent.CompletableFuture

/**
 * Created by victor.reventos on 6/30/17.
 */
class FunctionalProjectService {

    fun getProjectsForUser(userId: Long): CompletableFuture<Either<ProjectServiceError, List<Project>>> = Future {
        Repo.get(userId)
                .toEitherRight { UserNotFoundProjectServiceError(userId) }
                .right()
                .map { Repo.getProjectsForUser(userId) }
    }
}