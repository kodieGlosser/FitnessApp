package com.main.toledo.gymtrackr;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Kodie Glosser on 2/14/2015.
 */
public class DatabaseWrapper {

    private String COLUMN_NAME = "name";
    private String COLUMN_MUSCLE_GROUP = "MuscleGroup";
    private String COLUMN_EQUIPMENT_TYPE = "EquipmentType";
    private String COLUMN_TARGET_MUSCLE = "TargetMuscle";
    private String COLUMN_ID = "_ID";
    private String EXERCISE_TABLE = "Exercises";

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
    public Exercise[] browseExercisesByName(String name) {
        String[] whereArgs = new String[] { "%" + name + "%"};
        Cursor c = null;

        try {
            c = myDatabase.query(EXERCISE_TABLE, null, COLUMN_NAME + " like ? COLLATE NOCASE", whereArgs, null, null, COLUMN_NAME);
        } catch (Exception e) {
            Log.e("Query Failure", e.getMessage());
            return null;
        }

        return convertCursorToExercises(c);
    }

    public Exercise[] browseExerciseByEquipmentType(String equipmentType) {
        String[] whereArgs = new String[] { equipmentType};
        Cursor c  = myDatabase.query(EXERCISE_TABLE, null, COLUMN_EQUIPMENT_TYPE + "=? COLLATE NOCASE", whereArgs, null, null, COLUMN_NAME);

        return convertCursorToExercises(c);
    }

    public Exercise[] browseExerciseByEquipmentType(String equipmentType, String name) {
        String[] whereArgs = new String[] { equipmentType, "%" + name + "%"};

        String selectionArgs = COLUMN_EQUIPMENT_TYPE +  "= ? COLLATE NOCASE AND " + COLUMN_NAME + " like ? COLLATE NOCASE";
        Cursor c  = myDatabase.query(EXERCISE_TABLE, null, selectionArgs, whereArgs, null, null, COLUMN_NAME);

        return convertCursorToExercises(c);
    }

    public Exercise[] browseExerciseByMuscleGroup(String muscleGroup) {
        String[] whereArgs = new String[] { muscleGroup};
        Cursor c  = myDatabase.query(EXERCISE_TABLE, null, COLUMN_MUSCLE_GROUP + "=? COLLATE NOCASE", whereArgs, null, null, COLUMN_NAME);

        return convertCursorToExercises(c);
    }

    public Exercise[] browseExerciseByMuscleGroup(String muscleGroup, String name) {
        String[] whereArgs = new String[] { muscleGroup, "%" + name + "%"};

        String selectionArgs = COLUMN_MUSCLE_GROUP +  "= ? COLLATE NOCASE AND " + COLUMN_NAME + " like ? COLLATE NOCASE";
        Cursor c  = myDatabase.query(EXERCISE_TABLE, null, selectionArgs, whereArgs, null, null, COLUMN_NAME);

        return convertCursorToExercises(c);
    }

    public Exercise[] browseExerciseByTargetMuscle(String targetMuscle) {
        String[] whereArgs = new String[] { targetMuscle};
        Cursor c  = myDatabase.query(EXERCISE_TABLE, null, COLUMN_TARGET_MUSCLE + "=? COLLATE NOCASE", whereArgs, null, null, COLUMN_NAME);

        return convertCursorToExercises(c);
    }

    public Exercise[] browseExerciseByTargetMuscle(String targetMuscle, String name) {
        String[] whereArgs = new String[] { targetMuscle, "%" + name + "%"};

        String selectionArgs = COLUMN_TARGET_MUSCLE +  "= ? COLLATE NOCASE AND " + COLUMN_NAME + " like ? COLLATE NOCASE";
        Cursor c  = myDatabase.query(EXERCISE_TABLE, null, selectionArgs, whereArgs, null, null, COLUMN_NAME);

        return convertCursorToExercises(c);
    }


    /**
     * Load just the plan names to display to user
     * @return ArrayList of plans just containing a plan name
     * note: might just want to pass a string back
     */
    public ArrayList<Plan> loadPlanNames() {
        return null;
    }

    /**
     * Loads the entire plan selected by user
     * @return an entire list of the workouts and exercises inside of the plan
     */
    public ArrayList<Plan> loadEntirePlan() {
        return null;
    }

    /**
     * This will load the history of the exercises sorted by date and name
     * @return ArrayList of the exercise history object
     */
    public ArrayList<ExerciseHistory> loadHistoryExerciseNames() {
        return null;
    }


    /**
     *
     * @param exerciseName the name of the exercise that needs to be loaded from history
     * @return
     */
    public ArrayList<ExerciseHistory> loadHistoryByExerciseName(String exerciseName) {
        return null;
    }


    /**
     * Stores the exercise into the plan
     * @param exercise the exercise to be added
     * @param circuitNumber the circuit number where its added
     * @param planNumber the plan number where its added
     */
    public void addExerciseToPlan(Exercise exercise, int circuitNumber, int planNumber) {

    }

    /**
     * If we want to delete it then we can just set the new position and new circuit to -1.
     * @param oldCircuitNumber
     * @param oldPosition
     * @param newCircuit
     * @param newPosition
     */
    public void alterExerciseInPlan(int oldCircuitNumber, int oldPosition, int newCircuit, int newPosition) {

    }

    private Exercise[] convertCursorToExercises(Cursor c) {
        int count = c.getCount();
        int i = 0;
        Exercise[] exercises= new Exercise[count];

        if(count >= 1) {
            while(c.moveToNext()) {

                int val_id = -1;
                String val_name = null, val_equipmentType = null, val_targetMuscle = null, val_muscleGroup = null, columnData = null;

                for (int j = 0; j < 5; j++) {

                    String columnName = c.getColumnName(j);
                    if (!columnName.equalsIgnoreCase(COLUMN_ID)) {
                            columnData = c.getString(c.getColumnIndex(columnName));
                    }

                    if (columnName.equalsIgnoreCase(COLUMN_ID)) {
                        val_id = c.getInt(c.getColumnIndex(columnName));
                    }
                    else if (columnName.equalsIgnoreCase(COLUMN_NAME)) {
                        val_name = columnData;
                    }
                    else if (columnName.equalsIgnoreCase(COLUMN_EQUIPMENT_TYPE)) {
                        val_equipmentType = columnData;
                    }
                    else if (columnName.equalsIgnoreCase(COLUMN_MUSCLE_GROUP)) {
                        val_muscleGroup = columnData;
                    }
                    else if (columnName.equalsIgnoreCase(COLUMN_TARGET_MUSCLE)) {
                        val_targetMuscle = columnData;
                    }
                }
                exercises[i] = new Exercise(val_id, val_name, val_equipmentType, val_muscleGroup, val_targetMuscle);
                i++;
            }

        }

        return exercises;
    }

    public void storeExerciseIntoCircuit(Exercise[] exercises, int circuitNumber){

    }

    public void storeCircuitIntoWorkout(ArrayList<Circuit> circuit, int workoutNumber) {

    }



}
