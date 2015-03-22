package com.main.toledo.gymtrackr;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DatabaseWrapperTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testBrowseExercisesByName() throws Exception {

    }

    public void testBrowseExercisesByExactName() throws Exception {
        DatabaseWrapper db = new DatabaseWrapper();
        Exercise[] exercises = db.browseExercisesByExactName("Barbell Squat");

        Assert.assertEquals(exercises.length,1);    // fail the test if this doesn't have a length of 1.

        // fail the test if the object returned is not constructed correctly
        Assert.assertEquals(exercises[0].getEquipment(), "Barbell");
        Assert.assertEquals(exercises[0].getMuscleGroup(), "Legs");
        Assert.assertEquals(exercises[0].getTargetMuscle(), "Quadriceps");
        Assert.assertEquals(exercises[0].getName(), "Barbell Squat");
    }

    public void testBrowseExerciseById() throws Exception {

    }

    public void testBrowseExerciseByEquipmentType() throws Exception {

    }

    public void testBrowseExerciseByEquipmentType1() throws Exception {

    }

    public void testBrowseExerciseByMuscleGroup() throws Exception {

    }

    public void testBrowseExerciseByMuscleGroup1() throws Exception {

    }

    public void testBrowseExerciseByTargetMuscle() throws Exception {

    }

    public void testBrowseExerciseByTargetMuscle1() throws Exception {

    }

    public void testLoadPlanNames() throws Exception {

    }

    public void testLoadEntirePlan() throws Exception {

    }

    public void testSaveEntirePlan() throws Exception {

    }

    public void testDeletePlan() throws Exception {

    }

    public void testDeletePlan1() throws Exception {

    }

    public void testLoadExercisesByDate() throws Exception {

    }

    public void testAddExerciseToHistory() throws Exception {

    }

    public void testDeleteExerciseInExerciseTable() throws Exception {

    }

    public void testDeleteExerciseInExerciseTable1() throws Exception {

    }

    public void testAddExerciseToExerciseTable() throws Exception {

    }

    public void testSetOneRepMaxForExercise() throws Exception {

    }

    public void testLoadHistoryExerciseNames() throws Exception {

    }

    public void testLoadHistoryByExerciseName() throws Exception {

    }

}