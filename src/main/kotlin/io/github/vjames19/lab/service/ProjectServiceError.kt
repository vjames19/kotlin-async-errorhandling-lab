package io.github.vjames19.lab.service

/**
 * Created by victor.reventos on 6/30/17.
 */
sealed class ProjectServiceError(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

data class UserNotFoundProjectServiceError(val id: Long) : ProjectServiceError("User not found $id")