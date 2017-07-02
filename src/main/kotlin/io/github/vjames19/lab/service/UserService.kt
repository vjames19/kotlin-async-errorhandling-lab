package io.github.vjames19.lab.service

import io.github.vjames19.futures.jdk8.Future
import io.github.vjames19.lab.Repo
import io.github.vjames19.lab.User
import org.funktionale.option.getOrElse
import java.util.concurrent.CompletableFuture

/**
 * Created by victor.reventos on 6/30/17.
 */
class UserService {

    @Throws(UserServiceError::class)
    fun get(id: Long): CompletableFuture<User> = Future {
        Repo.get(id).getOrElse { throw NotFoundUserServiceError(id) }
    }
}