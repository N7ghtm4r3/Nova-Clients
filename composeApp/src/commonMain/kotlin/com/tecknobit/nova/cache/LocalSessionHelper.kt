package com.tecknobit.nova.cache

import app.cash.sqldelight.db.SqlDriver
import com.tecknobit.equinox.FetcherManager
import com.tecknobit.nova.helpers.storage.DatabaseDriverFactory
import com.tecknobit.nova.navigator
import com.tecknobit.nova.ui.screens.NovaScreen.Companion.SPLASH_SCREEN
import com.tecknobit.novacore.helpers.LocalSessionUtils
import com.tecknobit.novacore.helpers.LocalSessionUtils.NovaSession
import com.tecknobit.novacore.helpers.LocalSessionUtils.SESSIONS_TABLE
import com.tecknobit.novacore.records.NovaUser
import com.tecknobit.novacore.records.NovaUser.Role

/**
 * The **LocalSessionHelper** class is useful to manage the local sessions of the user, so manage the credentials
 * of the user and all his/her personal data like profile pic, email and password
 *
 * @param databaseDriverFactory: the driver factory used to create the specific driver for each platform
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see LocalSessionUtils
 */
class LocalSessionHelper(
    val databaseDriverFactory: DatabaseDriverFactory
) : LocalSessionUtils {

    /**
     * **sqlDriver** -> the driver used by the [database] instance to work with the database
     */
    private val sqlDriver: SqlDriver = databaseDriverFactory.createDriver()

    /**
     * **database** -> the database manager
     */
    private var database = Nova(
        driver = sqlDriver
    )

    /**
     * **dbQuery** -> the queries manager
     */
    private val dbQuery = database.novaQueries

    /**
     * Method to insert a new session
     *
     * @param id: the identifier of the user in that session
     * @param token: the token of the user in that session
     * @param profilePicUrl: the profile pic url of the user in that session
     * @param name: the name of the user
     * @param surname: the surname of the user
     * @param email: the email of the user in that session
     * @param password: the password of the user in that session
     * @param hostAddress: the host address used in that session
     * @param role: the identifier of the user in that session
     * @param language: the language of the user
     */
    override fun insertSession(
        id: String,
        token: String,
        profilePicUrl: String,
        name: String,
        surname: String,
        email: String,
        password: String,
        hostAddress: String,
        role: Role,
        language: String
    ) {
        dbQuery.insertSession(
            id = id,
            token = token,
            profile_pic = profilePicUrl,
            name = name,
            surname = surname,
            email = email,
            password = password,
            host_address = hostAddress,
            role = role.name,
            language = language
        )
        changeActiveSession(id)
    }

    /**
     * Method to set the current active session as inactive 
     *
     * No-any params required
     */
    override fun setCurrentActiveSessionAsInactive() {
        dbQuery.setCurrentActiveSessionAsInactive()
    }

    /**
     * Method to set as the active session a new session
     *
     * @param id: the identifier of the session to set as active
     */
    override fun setNewActiveSession(
        id: String
    ) {
        dbQuery.setNewActiveSession(
            id = id
        )
    }

    /**
     * Method to list all the local sessions of the user.
     *
     * No-any params required
     *
     * @return the list of the local sessions of the user as [List] of [NovaSession]
     */
    override fun getSessions(): List<NovaSession> {
        val currentSessions = arrayListOf<NovaSession>()
        val sessions = dbQuery.getSessions().executeAsList()
        sessions.forEach { session ->
            currentSessions.add(
                fillNovaSession(
                    session = session
                )
            )
        }
        return currentSessions
    }

    /**
     * Method to get the local session specified by the identifier of the user in that session
     *
     * @param id: the user identifier to fetch the local session
     * @return the local session as [NovaSession]
     */
    override fun getSession(
        id: String
    ): NovaSession? {
        val querySession = dbQuery.getSession(
            id = id
        ).executeAsOneOrNull()
        if(querySession != null) {
            return fillNovaSession(
                session = querySession
            )
        }
        return null
    }

    /**
     * Method to get the current active local session
     *
     *
     * No-any params required
     * @return the local session as [NovaSession]
     */
    override fun getActiveSession(): NovaSession? {
        val querySession = dbQuery.getActiveSession().executeAsOneOrNull()
        if(querySession != null) {
            return fillNovaSession(
                session = querySession
            )
        }
        return null
    }

    /**
     * Function to create a [NovaSession] with the data queried to the database
     *
     * @param session: the session queried
     *
     * @return the session as [NovaSession] instance
     */
    private fun fillNovaSession(
        session: Sessions
    ) : NovaSession {
        return NovaSession(
            session.id,
            session.token,
            session.profile_pic,
            session.name,
            session.surname,
            session.email,
            session.password,
            session.host_address,
            NovaUser.Role.valueOf(session.role),
            session.is_active!!,
            session.language
        )
    }

    /**
     * Method to change a value of the current session
     *
     * @param key: the key of the value to change
     * @param sessionValue: the new session value to set
     */
    override fun changeSessionValue(
        key: String,
        sessionValue: String
    ) {
        database.transaction {
            val updateQuery = "UPDATE $SESSIONS_TABLE SET $key = ? WHERE is_active='1'"
            sqlDriver.execute(
                identifier = null,
                sql = updateQuery,
                parameters = 2,
                binders = {
                    bindString(0, sessionValue)
                }
            )
        }
    }

    /**
     * Method to perform the logout action and clear the current local sessions stored
     *
     * No-any params required
     */
    fun logout() {
        deleteAllSessions()
        FetcherManager.setActiveContext(this::class.java)
        navigator.navigate(SPLASH_SCREEN)
    }

    /**
     * Method to delete all the local sessions, used when the user executes a logout or the account deletion 
     * No-any params required
     */
    override fun deleteAllSessions() {
        dbQuery.deleteAllSessions()
    }

    /**
     * Method to delete a specific local session specified by the identifier of the user in that session
     * @param id: the user identifier to delete the local session
     */
    override fun deleteSession(
        id: String
    ) {
        dbQuery.deleteSession(
            id = id
        )
    }

}