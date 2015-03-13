package com.main.toledo.gymtrackr;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.util.Calendar;
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
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String COLUMN_SEQUENCE = "sequence";
    private static final String COLUMN_CIRCUIT_NAME = "circuitName";
    private static final String COLUMN_WORKOUT_ID = "workoutId";
    private static final String COLUMN_PLAN_ID = "planId";
    private static final String COLUMN_OPEN= "open";
    private static final String COLUMN_WORKOUT= "Workout";
    private static final String COLUMN_CIRCUIT= "Circuit";
    private static final String COLUMN_CIRCUIT_ID= "circuitId";
    private static final String COLUMN_PLANNED_UNION= "Planned_Union";
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
        myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
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

    public int saveEntirePlan(Plan plan) {

        if (getPlanIdFromName(plan.getName()) != -1) {
            deletePlan(plan.getName());
        }

        ContentValues planValues = new ContentValues();
        planValues.put(COLUMN_NAME, plan.getName());
        long planId = myDatabase.insert(COLUMN_PLAN, null,planValues);
        Circuit_temp[] circuits = plan.getCircuits();
        for (int i = 0; i < circuits.length; i++) {
            Exercise[] exercises = circuits[i].getExercises();
            long circuitId;
            ContentValues workoutValues = new ContentValues();
            workoutValues.put(COLUMN_CIRCUIT_NAME, circuits[i].getName());
            workoutValues.put(COLUMN_OPEN, circuits[i].isOpen());
            workoutValues.put(COLUMN_SEQUENCE, circuits[i].getSequence());
            long workoutId = myDatabase.insert(COLUMN_WORKOUT, null, workoutValues);

            for (int j = 0; j < exercises.length; j++) {
                ContentValues circuitValues = new ContentValues();
                circuitValues.put(COLUMN_WEIGHT, exercises[j].getWeight());
                circuitValues.put(COLUMN_REP, exercises[j].getRepetitions());
                circuitValues.put(COLUMN_SEQUENCE, exercises[j].getSequence());
                circuitValues.put(COLUMN_EXERCISE, exercises[j].getId());
                circuitId = myDatabase.insert(COLUMN_CIRCUIT, null, circuitValues);

                ContentValues plannedUnionValues = new ContentValues();
                plannedUnionValues.put(COLUMN_WORKOUT_ID, workoutId);
                plannedUnionValues.put(COLUMN_CIRCUIT_ID, circuitId);
                plannedUnionValues.put(COLUMN_PLAN_ID, planId);
                myDatabase.insert(COLUMN_PLANNED_UNION, null, plannedUnionValues);
            }
        }

        return (int)planId;
    }

    public void deletePlan(int planId) {
        String whereClause = "Workout._id IN (select workoutId from Planned_Union where planId=?)";
        String[] whereArgs = {Integer.toString(planId)};
        myDatabase.delete(COLUMN_WORKOUT, whereClause, whereArgs);

        whereClause = "Circuit._id IN (select circuitId from Planned_Union where planId=?)";
        myDatabase.delete(COLUMN_CIRCUIT, whereClause, whereArgs);

        whereClause = "planId = ?";
        myDatabase.delete(COLUMN_PLANNED_UNION, whereClause, whereArgs);

        whereClause = "_id=?";
        myDatabase.delete(COLUMN_PLAN, whereClause, whereArgs);
    }

    public void deletePlan(String planName) {
        int planId = getPlanIdFromName(planName);

        String whereClause = "Workout._id IN (select workoutId from Planned_Union where planId=?)";
        String[] whereArgs = {Integer.toString(planId)};
        myDatabase.delete(COLUMN_WORKOUT, whereClause, whereArgs);

        whereClause = "Circuit._id IN (select circuitId from Planned_Union where planId=?)";
        myDatabase.delete(COLUMN_CIRCUIT, whereClause, whereArgs);

        whereClause = "planId = ?";
        myDatabase.delete(COLUMN_PLANNED_UNION, whereClause, whereArgs);

        whereClause = "_id=?";
        myDatabase.delete(COLUMN_PLAN, whereClause, whereArgs);
    }

    private int getPlanIdFromName(String planName) {
        // get plan id
        Cursor c = myDatabase.query(COLUMN_PLAN, new String[] { COLUMN_ID }, COLUMN_NAME + "=? COLLATE NOCASE", new String[] { planName}, null, null, null);
        int planId = -1;

        while (c.moveToNext()) {
            planId = c.getInt(c.getColumnIndex(COLUMN_ID));
        }
        return planId;
    }

    /**
     * Example usage
     *         SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
               String sdate = "2015-02-05";
               Date val_date = null;
                    try {
                        val_date = format.parse(sdate);
                        db.loadExercisesByDate(val_date);
                    } catch (ParseException e) {
                    e.printStackTrace();
                    }
     * @param date
     * @return
     */
    public ExerciseHistory[] loadExercisesByDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String s_Month = null, s_Day = null, s_Hour = "", s_Minute = "", s_Second = "";
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (month < 10) s_Month = "0" + month;
        if (day < 10) s_Day = "0" + day;

        String format = cal.get(Calendar.YEAR) + "-" + s_Month + "-" + s_Day;
        String rawquery = "select * from History where Date like '%" + format  + "%'";
        Cursor c = myDatabase.rawQuery(rawquery, null);

        return convertCursorToExerciseHistory(c);
    }

    public void addExerciseToHistory(ExerciseHistory[] exercise) {

        for (int i =0; i < exercise.length; i++) {
            ContentValues exerciseHistoryValues = new ContentValues();
            exerciseHistoryValues.put(COLUMN_DATE, exercise[i].getDate().toString());
            exerciseHistoryValues.put(COLUMN_WEIGHT, exercise[i].getWeight());
            exerciseHistoryValues.put(COLUMN_REP, exercise[i].getRep());
            exerciseHistoryValues.put(COLUMN_EXERCISE, exercise[i].getExerciseId());
            exerciseHistoryValues.put(COLUMN_PLAN, exercise[i].getPlanId());
            myDatabase.insert(EXERCISE_HISTORY_TABLE, null, exerciseHistoryValues);
        }

    }

    public void deleteExerciseInExerciseTable(String exerciseName) {
        String selectionArgs = COLUMN_NAME +  "= ? COLLATE NOCASE";
        String[] whereArgs = new String[] { exerciseName };
        myDatabase.delete(COLUMN_EXERCISE, selectionArgs, whereArgs);
    }

    public void deleteExerciseInExerciseTable(int exerciseId) {
        myDatabase.delete(COLUMN_EXERCISE, COLUMN_ID + "= " + exerciseId, null);
    }

    public void addExerciseToExerciseTable(Exercise exercise) {
        ContentValues exerciseValues = new ContentValues();
        exerciseValues.put(COLUMN_EQUIPMENT_TYPE, exercise.getEquipment());
        exerciseValues.put(COLUMN_NAME, exercise.getName());
        exerciseValues.put(COLUMN_TARGET_MUSCLE, exercise.getTargetMuscle());
        exerciseValues.put(COLUMN_MUSCLE_GROUP, exercise.getMuscleGroup());
        myDatabase.insert(EXERCISE_TABLE, null, exerciseValues);
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

//    public void addCircuitToPlan(int sequence, int planNumber, String circuitName) {
//        // query: insert into Planned_Union (plannedWorkout, plan, circuitName) values (sequence, planNumber, circuitName)
//    }
//
//    /**
//     * Adds the exercise to the Circuit
//     * @param exercise the exercise to be added
//     * @param circuitNumber the circuit number where its added
//     * @param sequence the sequence inside the circuit
//     * @param isItOpen true if open, false if not
//     */
//    public void addExerciseToCircuit(Exercise exercise, int circuitNumber, int sequence, boolean isItOpen, int planId) {
//
//    }
//
//    /**
//     * If we want to delete it then we can just set the new position and new circuit to -1.
//     * @param oldCircuitNumber
//     * @param oldSequence
//     * @param newCircuit
//     * @param newSequence
//     */
//    public void alterExerciseInPlan(int oldCircuitNumber, int oldSequence, int newCircuit, int newSequence, int planId) {
//
//    }
//
//    /**
//     * Passing -1 will delete the record
//     * @param oldSequence
//     * @param newSequence
//     */
//    public void alterCircuitInPlan(int oldSequence, int newSequence, int planId){
//
//    }

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

                        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
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
