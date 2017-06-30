package io.github.vjames19.lab.service

import io.github.vjames19.lab.Project
import io.github.vjames19.lab.Repo
import io.github.vjames19.lab.immediateFuture
import org.funktionale.option.getOrElse
import java.util.concurrent.CompletableFuture

/**
 * Created by victor.reventos on 6/30/17.
 */
class ProjectService {

    @Throws(ProjectServiceError::class)
    fun getProjectsForUser(userId: Long): CompletableFuture<List<Project>> = immediateFuture {
        Repo.get(userId).getOrElse { throw UserNotFoundProjectServiceError(userId) }
        Repo.getProjectsForUser(userId)
    }
}