package io.github.vjames19.lab

import io.github.vjames19.lab.service.*
import java.util.concurrent.CompletableFuture

/**
 * Created by victor.reventos on 6/30/17.
 */
class CompletableFutureExample {
    val userService = UserService()
    val projectService = ProjectService()

    fun dependent() {
        userService.get(1)
                .flatMap {
                    projectService.getProjectsForUser(it.id)
                }
    }

    fun errorHandling() {
        userService.get(1)
                .flatMap {
                    projectService.getProjectsForUser(it.id)
                            .recover { projectFallback(it) }
                }
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


    fun concurrentWithErrorHandling() {
        val userFuture = userService.get(1)

        // fallback to empty list
        val projectsFuture = projectService.getProjectsForUser(1).recover {
            when (it) {
                is UserNotFoundProjectServiceError -> emptyList<Project>()
                else -> throw it
            }
        }

        CompletableFuture.allOf(userFuture, projectsFuture).map {
            val user = userFuture.get()
            val projects = projectsFuture.get()

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
