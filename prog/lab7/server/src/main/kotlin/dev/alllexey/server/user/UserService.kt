package dev.alllexey.server.user

import dev.alllexey.common.protocol.Request
import dev.alllexey.common.protocol.Response
import dev.alllexey.server.App
import dev.alllexey.server.data.Database

class UserService(
    private val app: App
) {

    fun createUsersTable() {
        val sql = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                login VARCHAR(255) NOT NULL UNIQUE,
                password_hash VARCHAR(255) NOT NULL
            );
        """.trimIndent()

        database().connection.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute(sql)
            }
        }
    }

    fun handleRegister(login: String, passwordHash: String): Pair<User?, Response> {
        val existingUser = getUserByLogin(login)
        if (existingUser != null) {
            return null to Response.ErrorResponse("Пользователь с таким логином уже зарегистрирован")
        }

        val user = User(
            login = login,
            passwordHash = passwordHash
        )

        val createdUser = createUser(user)

        return createdUser to Response.CredentialsResponse(Request.AuthData(login, passwordHash))
    }

    fun handleLogin(login: String, passwordHash: String): Pair<User?, Response> {

        val user = getUserByLogin(login)
            ?: return null to Response.ErrorResponse("Пользователь не найден")

        if (user.passwordHash != passwordHash) {
            return null to Response.ErrorResponse("Неверный пароль")
        }

        return user to Response.CredentialsResponse(Request.AuthData(login, passwordHash))
    }

    fun createUser(user: User): User {
        val sql = "INSERT INTO users (login, password_hash) VALUES (?, ?) RETURNING id"

        database().connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, user.login)
                stmt.setString(2, user.passwordHash)

                val rs = stmt.executeQuery()
                if (rs.next()) {
                    val id = rs.getLong("id")
                    user.id = id
                    return user
                } else {
                    throw RuntimeException("Failed to insert user")
                }
            }
        }
    }

    fun getUserByLogin(login: String): User? {
        val sql = "SELECT id, login, password_hash FROM users WHERE login = ?"

        database().connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, login)

                val rs = stmt.executeQuery()
                return if (rs.next()) {
                    User(
                        id = rs.getLong("id"),
                        login = rs.getString("login"),
                        passwordHash = rs.getString("password_hash")
                    )
                } else null
            }
        }
    }

    private fun database(): Database = app.database
}