package io.github.vjames19.lab.service

import io.github.vjames19.lab.Repo
import io.github.vjames19.lab.User
import io.github.vjames19.lab.immediateFuture
import org.funktionale.either.Either
import org.funktionale.either.toEitherRight
import java.util.concurrent.CompletableFuture

/**
 * Created by victor.reventos on 6/30/17.
 */
class FunctionalUserService {

    fun get(id: Long): CompletableFuture<Either<UserServiceError, User>> = immediateFuture {
        Repo.get(id).toEitherRight { NotFoundUserServiceError(id) }
    }
}