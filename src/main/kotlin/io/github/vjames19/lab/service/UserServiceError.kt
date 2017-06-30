package io.github.vjames19.lab.service

/**
 * Created by victor.reventos on 6/30/17.
 */
sealed class UserServiceError(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

data class NotFoundUserServiceError(val id: Long) : UserServiceError("User not found $id")