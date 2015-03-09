package com.main.toledo.gymtrackr;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Kodie Glosser on 2/14/2015.
 */
public class DatabaseWrapper {

    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_REP = "rep";
    private static final String COLUMN_EXERCISE = "exercise";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_MUSCLE_GROUP = "MuscleGroup";
    private static final String COLUMN_EQUIPMENT_TYPE = "EquipmentType";
    private static final String COLUMN_PLAN = "Plan";
    private static final String COLUMN_TARGET_MUSCLE = "TargetMuscle";
    private static final String COLUMN_ID = "_ID";
    private static final String EXERCISE_TABLE = "Exercises";
    private static final String EXERCISE_HISTORY_TABLE = "History";
    private static final String DATE_FORMAT = "YYYY-MM-DD HH:MM:SS";
    private static final String COLUMN_SEQUENCE = "sequence";
    private static final String COLUMN_CIRCUIT_NAME = "circuitName";
    private static final String COLUMN_WORKOUT_ID = "workoutId";
    private static final String COLUMN_PLAN_ID = "planId";
    private static final String COLUMN_OPEN= "open";
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

    public Exercise[] browseExerciseById(int ID) {

        Cursor c  = myDatabase.query(EXERCISE_TABLE, null, COLUMN_ID + "= " + ID, null, null, null, COLUMN_NAME);

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
    public String[] loadPlanNames() {
        Cursor c = myDatabase.query(COLUMN_PLAN, new String[]{COLUMN_NAME}, null, null, null, null, null);
        String[] names = new String[c.getCount()];
        int i = 0;
        if (c.getCount() >= 1) {
            while(c.moveToNext()) {
                names[i] = new String(c.getString(0));
                i++;
            }
        }
        return names;
    }

    /**
     * Loads the entire plan selected by user
     * @return an entire list of the workouts and exercises inside of the plan
     */
    public Plan loadEntirePlan(String planName) {
        String rawquery = "select DISTINCT(workoutId), planId from Planned_Union where Planned_Union.planId IN (select _id from Plan where name='" + planName + "')";
        Plan plan = null;
        Cursor c = myDatabase.rawQuery(rawquery, null);
        int count = c.getCount();
        Circuit_temp[] circuits = new Circuit_temp[count];
        int i = 0;
        int planId = -1;
        if(count >= 1) {
            while (c.moveToNext()) {
                for (int j = 0; j < c.getColumnCount(); j++) {

                    String columnName = c.getColumnName(j);
                    int workoutId = -1, sequence = -1;
                    boolean open = false;
                    Exercise[] exercises = null;
                    String name = null;

                    if (columnName.equalsIgnoreCase(COLUMN_WORKOUT_ID)) {
                        workoutId = c.getInt(c.getColumnIndex(columnName));
                        String rawquery1 = "select Open, circuitName, sequence from Workout where _id = " + workoutId;
                        Cursor c1 = myDatabase.rawQuery(rawquery1, null);
                        int count1 = c1.getCount();

                        if (count1 >= 1) {
                            while (c1.moveToNext()) {
                                for (int x = 0; x < c1.getColumnCount(); x++) {
                                    String columnName1 = c1.getColumnName(x);

                                    if (columnName1.equalsIgnoreCase(COLUMN_OPEN)) {
                                        if (c1.getInt(c1.getColumnIndex(columnName1)) == 1){
                                            open = true;
                                        }
                                    }
                                    else if (columnName1.equalsIgnoreCase(COLUMN_CIRCUIT_NAME)) {
                                        name = c1.getString(c1.getColumnIndex(columnName1));
                                    }
                                    else if (columnName1.equalsIgnoreCase(COLUMN_SEQUENCE)) {
                                        sequence = c1.getInt(c1.getColumnIndex(columnName1));
                                    }
                                }
                                exercises = getExercisesFromCircuitTable(workoutId);
                                circuits[i] = new Circuit_temp(name, exercises, workoutId, open, sequence);
                                i++;
                            }
                        }

                    }

                    else if (columnName.equalsIgnoreCase(COLUMN_PLAN_ID)) {
                        planId = c.getInt(c.getColumnIndex(columnName));
                    }
                }
            }
        }
        plan = new Plan(planName, circuits, planId);
        return plan;
    }

    private Exercise[] getExercisesFromCircuitTable(int workoutId) {
        Exercise[] exercises;
        String rawquery2 = "select * from Circuit where Circuit._id IN (select circuitId from Planned_Union where workoutId = " + workoutId + ")";
        Cursor c2 = myDatabase.rawQuery(rawquery2, null);
        int count2 = c2.getCount();
        exercises = new Exercise[count2];
        int z = 0;
        if (count2 >= 1) {
            while(c2.moveToNext()) {
                int weight = -1, rep = -1, sequence1 = -1, exercise = -1, id = -1;

                for (int y = 0; y < c2.getColumnCount(); y++) {
                    String columnName2 = c2.getColumnName(y);
                    if (columnName2.equalsIgnoreCase(COLUMN_ID)){
                        id = c2.getInt(c2.getColumnIndex(columnName2));
                    }
                    else if (columnName2.equalsIgnoreCase(COLUMN_WEIGHT)){
                        weight = c2.getInt(c2.getColumnIndex(columnName2));
                    }
                    else if (columnName2.equalsIgnoreCase(COLUMN_REP)){
                        rep = c2.getInt(c2.getColumnIndex(columnName2));
                    }
                    else if (columnName2.equalsIgnoreCase(COLUMN_SEQUENCE)){
                        sequence1 = c2.getInt(c2.getColumnIndex(columnName2));
                    }
                    else if (columnName2.equalsIgnoreCase(COLUMN_EXERCISE)){
                        exercise = c2.getInt(c2.getColumnIndex(columnName2));
                    }
                }

                exercises[z] = new Exercise(id, browseExerciseById(exercise)[0].getName(), rep, weight, sequence1);
                z++;
            }
        }
        return exercises;
    }

    /**
     * This will load the history of the exercises sorted by date and name
     * @return ArrayList of the exercise history object
     */
    public Exercise[] loadHistoryExerciseNames() {
        String rawQuery = "select Exercises.name from Exercises where Exercises._id IN (select History.exercise from History) ORDER BY Exercises.name";
        Cursor c = myDatabase.rawQuery(rawQuery, null);

        return convertCursorToExercises(c);
    }


    /**
     *
     * @param exerciseName the name of the exercise that needs to be loaded from history
     * @return
     */
    public ExerciseHistory[] loadHistoryByExerciseName(String exerciseName) {
        String rawQuery = "select * from History where History.exercise IN (select Exercises._id from Exercises where Exercises.name=?) ORDER BY datetime(History.date) DESC";
        String[] selectionArgs = new String[] { exerciseName };

        Cursor c = myDatabase.rawQuery(rawQuery, selectionArgs);

        return convertCursorToExerciseHistory(c);
    }

    public void addCircuitToPlan(int sequence, int planNumber, String circuitName) {
        // query: insert into Planned_Union (plannedWorkout, plan, circuitName) values (sequence, planNumber, circuitName)
    }

    /**
     * Adds the exercise to the Circuit
     * @param exercise the exercise to be added
     * @param circuitNumber the circuit number where its added
     * @param sequence the sequence inside the circuit
     * @param isItOpen true if open, false if not
     */
    public void addExerciseToCircuit(Exercise exercise, int circuitNumber, int sequence, boolean isItOpen, int planId) {

    }

    /**
     * If we want to delete it then we can just set the new position and new circuit to -1.
     * @param oldCircuitNumber
     * @param oldSequence
     * @param newCircuit
     * @param newSequence
     */
    public void alterExerciseInPlan(int oldCircuitNumber, int oldSequence, int newCircuit, int newSequence, int planId) {

    }

    /**
     * Passing -1 will delete the record
     * @param oldSequence
     * @param newSequence
     */
    public void alterCircuitInPlan(int oldSequence, int newSequence, int planId){

    }

    private Exercise[] convertCursorToExercises(Cursor c) {
        int count = c.getCount();
        int i = 0;
        Exercise[] exercises= new Exercise[count];

        if(count >= 1) {
            while(c.moveToNext()) {

                int val_id = -1;
                String val_name = null, val_equipmentType = null, val_targetMuscle = null, val_muscleGroup = null, columnData = null;

                for (int j = 0; j < c.getColumnCount(); j++) {

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

    private ExerciseHistory[] convertCursorToExerciseHistory(Cursor c) {
        int count = c.getCount();
        int i = 0;
        ExerciseHistory[] exerciseHistory= new ExerciseHistory[count];

        if(count >= 1) {
            while (c.moveToNext()) {
                String s_val_date; int val_weight= -1, val_rep= -1, val_exerciseId = -1, val_planId = -1, columnData = -1;
                Date val_date = null;
                for (int j = 0; j < c.getColumnCount(); j++) {

                    String columnName = c.getColumnName(j);

                    if (!columnName.equalsIgnoreCase(COLUMN_DATE))
                        columnData = c.getInt(c.getColumnIndex(columnName));


                    if (columnName.equalsIgnoreCase(COLUMN_DATE)) {
                        s_val_date = c.getString(c.getColumnIndex(columnName));

                        DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
                        try {
                            val_date = format.parse(s_val_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (columnName.equalsIgnoreCase(COLUMN_WEIGHT)){
                        val_weight = columnData;
                    }
                    else if (columnName.equalsIgnoreCase(COLUMN_REP)){
                        val_rep = columnData;
                    }
                    else if (columnName.equalsIgnoreCase(COLUMN_EXERCISE)){
                        val_exerciseId = columnData;
                    }
                    else if (columnName.equalsIgnoreCase(COLUMN_PLAN)) {
                        val_planId = columnData;
                    }
                }

                exerciseHistory[i] = new ExerciseHistory(val_date, val_weight, val_rep, val_exerciseId, val_planId);
                i++;
            }
        }

        return exerciseHistory;
    }
}
