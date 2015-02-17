package com.main.toledo.gymtrackr;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Kodie Glosser on 2/14/2015.
 */
public class DatabaseWrapper {

    private static String DB_PATH = "/data/data/com.main.toledo.gymtrackr/databases/";
    private static String DB_NAME = "gymtrackr.db";
    private SQLiteDatabase myDatabase;

    public DatabaseWrapper() {
        try {
            openDataBase();
        } catch(SQLException sql) {
            throw sql;
        }
    }

    private void openDataBase() throws SQLException {} {
        // open DB
        String myPath = DB_PATH + DB_NAME;
        myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    /**
     *
     */
    public Cursor browseExercisesByName(String name) {
        String table = "Exercises";
        String[] columns =  new String[] {"*"};
        String whereClause = "name = ?";
        String[] whereArgs = new String[] {name};
        Cursor c = null;

        try {
            c = myDatabase.query(table, columns, whereClause, whereArgs, null, null, null);
        } catch (Exception e) {
            Log.e("Query Failure", e.getMessage());
        }

        return c;
    }

}
