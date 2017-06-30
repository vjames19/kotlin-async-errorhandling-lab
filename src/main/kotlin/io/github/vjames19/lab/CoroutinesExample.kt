package io.github.vjames19.lab

import io.github.vjames19.lab.service.ProjectService
import io.github.vjames19.lab.service.ProjectServiceError
import io.github.vjames19.lab.service.UserNotFoundProjectServiceError
import io.github.vjames19.lab.service.UserService
import kotlinx.coroutines.experimental.future.await
import kotlinx.coroutines.experimental.runBlocking

/**
 * Created by victor.reventos on 6/30/17.
 */
class CoroutinesExample {
    val userService = UserService()
    val projectService = ProjectService()

    fun dependent() {
        runBlocking {
            val user = userService.get(1).await()
            val projects = projectService.getProjectsForUser(user.id).await()

        }
    }

    fun errorHandling() {
        runBlocking {
            val user = userService.get(1).await()
            val projects = projectService.getProjectsForUser(user.id)
                    .recover { projectFallback(it) }
                    .await()
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

    fun projectFallback(e: Throwable): List<Project> = if (e is ProjectServiceError) {
        when (e) {
            is UserNotFoundProjectServiceError -> emptyList<Project>()
        }
    } else {
        throw e
    }
}
