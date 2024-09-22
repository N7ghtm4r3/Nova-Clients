package com.tecknobit.nova.helpers.storage;

import static com.tecknobit.novacore.helpers.LocalSessionUtils.NovaSession.HOST_ADDRESS_KEY;
import static com.tecknobit.novacore.helpers.LocalSessionUtils.NovaSession.IS_ACTIVE_SESSION_KEY;
import static com.tecknobit.novacore.records.NovaItem.IDENTIFIER_KEY;
import static com.tecknobit.novacore.records.User.EMAIL_KEY;
import static com.tecknobit.novacore.records.User.LANGUAGE_KEY;
import static com.tecknobit.novacore.records.User.NAME_KEY;
import static com.tecknobit.novacore.records.User.PASSWORD_KEY;
import static com.tecknobit.novacore.records.User.PROFILE_PIC_URL_KEY;
import static com.tecknobit.novacore.records.User.ROLE_KEY;
import static com.tecknobit.novacore.records.User.SURNAME_KEY;
import static com.tecknobit.novacore.records.User.TOKEN_KEY;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.tecknobit.novacore.helpers.LocalSessionUtils;
import com.tecknobit.novacore.records.User.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code LocalSessionHelper} class is useful to manage the local sessions of the user, so manage the credentials
 * of the user and all his/her personal data like profile pic, email and password
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see SQLiteOpenHelper
 * @see LocalSessionUtils
 */
public class LocalSessionHelper extends SQLiteOpenHelper implements LocalSessionUtils {

    /**
     * {@code DATABASE_VERSION} the version of the local database
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructor to init the {@link LocalSessionHelper} class
     *
     * @param context: the context where the helper has been invoked
     */
    public LocalSessionHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SESSIONS_TABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SESSIONS_TABLE);
        onCreate(db);
    }

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
     *
     */
    @Override
    public void insertSession(String id, String token, String profilePicUrl, String name, String surname,
                              String email, String password, String hostAddress, Role role, String language) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IDENTIFIER_KEY, id);
        values.put(TOKEN_KEY, token);
        values.put(PROFILE_PIC_URL_KEY, hostAddress + "/" + profilePicUrl);
        values.put(NAME_KEY, name);
        values.put(SURNAME_KEY, surname);
        values.put(EMAIL_KEY, email);
        values.put(PASSWORD_KEY, password);
        values.put(HOST_ADDRESS_KEY, hostAddress);
        values.put(ROLE_KEY, role.name());
        values.put(LANGUAGE_KEY, language);
        database.insert(SESSIONS_TABLE, null, values);
        changeActiveSession(id);
    }

    /**
     * Method to set the current active session as inactive <br>
     *
     * No-any params required
     */
    @Override
    public void setCurrentActiveSessionAsInactive() {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IS_ACTIVE_SESSION_KEY, false);
        database.update(SESSIONS_TABLE, values, IS_ACTIVE_SESSION_KEY + "=?",
                new String[]{"1"});
    }

    /**
     * Method to set as the active session a new session
     *
     * @param id: the identifier of the session to set as active
     */
    @Override
    public void setNewActiveSession(String id) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(IS_ACTIVE_SESSION_KEY, true);
        database.update(SESSIONS_TABLE, values, IDENTIFIER_KEY + "=?", new String[]{id});
    }

    /**
     * Method to list all the local sessions of the user. <br>
     * No-any params required
     *
     * @return the list of the local sessions of the user as {@link List} of {@link NovaSession}
     */
    @Override
    public List<NovaSession> getSessions() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + SESSIONS_TABLE, null);
        List<NovaSession> sessions = new ArrayList<>();
        while (cursor.moveToNext())
            sessions.add(fillNovaSession(cursor));
        cursor.close();
        return sessions;
    }

    /**
     * Method to get the local session specified by the identifier of the user in that session
     *
     * @param id: the user identifier to fetch the local session
     * @return the local session as {@link NovaSession}
     */
    @Override
    public NovaSession getSession(String id) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + SESSIONS_TABLE + " WHERE "
                + IDENTIFIER_KEY + "=?", new String[]{id});
        NovaSession session = null;
        if(cursor.moveToFirst()) {
            session = fillNovaSession(cursor);
            cursor.close();
        }
        return session;
    }

    /**
     * Method to get the current active local session <br>
     *
     * No-any params required
     * @return the local session as {@link NovaSession}
     */
    @Override
    public NovaSession getActiveSession() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + SESSIONS_TABLE + " WHERE "
                + IS_ACTIVE_SESSION_KEY + "=?", new String[]{"1"});
        NovaSession activeSession = null;
        if(cursor.moveToFirst()) {
            activeSession = fillNovaSession(cursor);
            cursor.close();
        }
        return activeSession;
    }

    /**
     * Method to fill an local session instance
     *
     * @param cursor: the cursor obtained by the query
     *
     * @return the local session instantiated as {@link NovaSession}
     */
    private NovaSession fillNovaSession(Cursor cursor) {
        return new NovaSession(
                cursor.getString(cursor.getColumnIndexOrThrow(IDENTIFIER_KEY)),
                cursor.getString(cursor.getColumnIndexOrThrow(TOKEN_KEY)),
                cursor.getString(cursor.getColumnIndexOrThrow(PROFILE_PIC_URL_KEY)),
                cursor.getString(cursor.getColumnIndexOrThrow(NAME_KEY)),
                cursor.getString(cursor.getColumnIndexOrThrow(SURNAME_KEY)),
                cursor.getString(cursor.getColumnIndexOrThrow(EMAIL_KEY)),
                cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD_KEY)),
                cursor.getString(cursor.getColumnIndexOrThrow(HOST_ADDRESS_KEY)),
                Role.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ROLE_KEY))),
                cursor.getInt(cursor.getColumnIndexOrThrow(IS_ACTIVE_SESSION_KEY)) == 1,
                cursor.getString(cursor.getColumnIndexOrThrow(LANGUAGE_KEY))
        );
    }

    /**
     * Method to change a value of the current session
     *
     * @param key: the key of the value to change
     * @param sessionValue: the new session value to set
     */
    @Override
    public void changeSessionValue(String key, String sessionValue) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(key, sessionValue);
        database.update(SESSIONS_TABLE, values, IS_ACTIVE_SESSION_KEY + "=?",
                new String[]{"1"});
    }

    /**
     * Method to delete all the local sessions, used when the user executes a logout or the account deletion <br>
     * No-any params required
     */
    @Override
    public void deleteAllSessions() {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(SESSIONS_TABLE, null, null);
    }

    /**
     * Method to delete a specific local session specified by the identifier of the user in that session
     * @param id: the user identifier to delete the local session
     */
    @Override
    public void deleteSession(String id) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(SESSIONS_TABLE, IDENTIFIER_KEY + "=?", new String[]{id});
    }

}
