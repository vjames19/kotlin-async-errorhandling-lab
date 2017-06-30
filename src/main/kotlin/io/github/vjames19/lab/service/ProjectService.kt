package io.github.vjames19.lab.service

import io.github.vjames19.lab.Future
import io.github.vjames19.lab.Project
import io.github.vjames19.lab.Repo
import org.funktionale.option.getOrElse
import java.util.concurrent.CompletableFuture

/**
 * Created by victor.reventos on 6/30/17.
 */
class ProjectService {

    @Throws(ProjectServiceError::class)
    fun getProjectsForUser(userId: Long): CompletableFuture<List<Project>> = Future {
        Repo.get(userId).getOrElse { throw UserNotFoundProjectServiceError(userId) }
        Repo.getProjectsForUser(userId)
    }
}