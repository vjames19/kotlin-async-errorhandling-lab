package io.github.vjames19.lab

import org.funktionale.option.Option
import org.funktionale.option.toOption

/**
 * Created by victor.reventos on 6/30/17.
 */
object Repo {

    private val users = mapOf(
            1L to User(id = 1, name = "user1")
    )

    private val projects = mapOf(
            1L to Project(id = 1L, userId = 1L, name = "project1")
    )

    fun get(id: Long): Option<User> = users[id].toOption()

    fun getProjectsForUser(userId: Long): List<Project> {
        return projects.values.filter { it.userId == userId }
    }
}