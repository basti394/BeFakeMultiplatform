package data.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pizza.xyz.befake.db.BeFakeDatabase
import pizza.xyz.befake.model.dtos.feed.User

interface UserRepository {
    fun getToken(): Flow<String>

    fun getTokenString(): String

    fun getUser(): Flow<User>

    fun setUserData(user: User)

    fun loggedIn(): Flow<Boolean>
}

class UserRepositoryImpl : KoinComponent, UserRepository {

    private val database: BeFakeDatabase by inject()

    override fun getToken(): Flow<String> {
        return database.userQueries.getToken().asFlow().mapToOne()
    }

    override fun setUserData(user: User) {
        database.userQueries.insertUserData(user)
    }

    override fun getTokenString(): String {
        return database.userQueries.getToken().executeAsOne()
    }

    override fun getUser(): Flow<User> {
        return database.userQueries.getUser().asFlow().mapToOne()
    }

    override fun loggedIn(): Flow<Boolean> {
        return database.userQueries.getToken().asFlow().mapToOneOrNull().map { !it.isNullOrBlank() }
    }
}