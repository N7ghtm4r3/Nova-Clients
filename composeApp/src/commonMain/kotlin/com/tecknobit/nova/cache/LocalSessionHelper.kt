package com.tecknobit.nova.cache

import com.tecknobit.nova.helpers.storage.DatabaseDriverFactory
import com.tecknobit.novacore.helpers.LocalSessionUtils
import com.tecknobit.novacore.helpers.LocalSessionUtils.NovaSession
import com.tecknobit.novacore.helpers.LocalSessionUtils.SESSIONS_TABLE
import com.tecknobit.novacore.records.NovaUser.Role

class LocalSessionHelper(
    databaseDriverFactory: DatabaseDriverFactory
) : LocalSessionUtils {

    private var database = Nova(
        driver = databaseDriverFactory.createDriver()
    )

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
     * Method to set the current active session as inactive <br></br>
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
     * Method to list all the local sessions of the user. <br></br>
     * No-any params required
     *
     * @return the list of the local sessions of the user as [List] of [NovaSession]
     */
    // TODO: TO SET
    override fun getSessions(): List<NovaSession> {
        dbQuery.getSessions()
        return listOf()
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
        return null
        /*return dbQuery.getSession(
            id = id
        )*/
    }

    /**
     * Method to get the current active local session <br></br>
     *
     * No-any params required
     * @return the local session as [NovaSession]
     */
    override fun getActiveSession(): NovaSession? {
        return null
        /*dbQuery.getActiveSession()*/
    }

    /**
     * Method to change a value of the current session
     *
     * @param key: the key of the value to change
     * @param sessionValue: the new session value to set
     */
    // TODO: TO CHECK TO IMPLEMENT RAW QUERY
    override fun changeSessionValue(
        key: String,
        sessionValue: String
    ) {
        val updateQuery = "UPDATE $SESSIONS_TABLE SET $key = $sessionValue"
    }

    /**
     * Method to delete all the local sessions, used when the user executes a logout or the account deletion <br></br>
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