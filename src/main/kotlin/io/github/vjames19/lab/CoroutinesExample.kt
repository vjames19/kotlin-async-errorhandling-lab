package io.github.vjames19.lab

import io.github.vjames19.futures.jdk8.fallbackTo
import io.github.vjames19.futures.jdk8.recover
import io.github.vjames19.lab.service.ProjectService
import io.github.vjames19.lab.service.ProjectServiceError
import io.github.vjames19.lab.service.UserNotFoundProjectServiceError
import io.github.vjames19.lab.service.UserService
import kotlinx.coroutines.experimental.future.await
import kotlinx.coroutines.experimental.future.future
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.CompletableFuture

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

            // do something with both

        }
    }

    fun errorHandlingWithTryCatch() {
        runBlocking {
            val user = userService.get(1).await()
            val projects = try {
                projectService.getProjectsForUser(user.id)
            } catch (e: Exception) {
                projectFallback(e)
            }

            // imagine having more calls, that you can recover from. Try-catch becomes unreadable.
        }
    }

    fun errorHandlingWithFutures() {
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

            // do something with both
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
