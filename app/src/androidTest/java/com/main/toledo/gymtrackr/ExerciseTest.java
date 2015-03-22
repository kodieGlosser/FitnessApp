package com.main.toledo.gymtrackr;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ExerciseTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testAddSeparatePlanMetrics() throws Exception {
        Exercise[] exercises = new Exercise[1];

        for (int i = 0; i < exercises.length; i++ ){

            exercises[i].setEquipment("something");
            exercises[i].setTargetMuscle("something");
            exercises[i].setName("something");

            Assert.assertEquals(exercises[i].getName(), "something");

        }

    }

    public void testSetOneRepMax() throws Exception {

    }

    public void testGetOneRepMax() throws Exception {

    }

    public void testSetTime() throws Exception {

    }

    public void testGetTime() throws Exception {

    }

    public void testSetOther() throws Exception {

    }

    public void testGetOther() throws Exception {

    }

    public void testSetOneRepMaxPercent() throws Exception {

    }

    public void testGetOneRepMaxPercent() throws Exception {

    }

    public void testSetWeight() throws Exception {

    }

    public void testGetWeight() throws Exception {

    }

    public void testSetRepetitions() throws Exception {

    }

    public void testGetRepetitions() throws Exception {

    }

    public void testSetSequence() throws Exception {

    }

    public void testGetSequence() throws Exception {

    }

    public void testSetId() throws Exception {

    }

    public void testGetId() throws Exception {

    }

    public void testSetTargetMuscle() throws Exception {

    }

    public void testGetTargetMuscle() throws Exception {

    }

    public void testSetMuscleGroup() throws Exception {

    }

    public void testGetMuscleGroup() throws Exception {

    }

    public void testSetName() throws Exception {

    }

    public void testGetName() throws Exception {

    }

    public void testSetEquipment() throws Exception {

    }

    public void testGetEquipment() throws Exception {

    }

    public void testSetLastPerformed() throws Exception {

    }

    public void testGetLastPerformed() throws Exception {

    }

    public void testGetMetrics() throws Exception {

    }

    public void testGetPlanMetrics() throws Exception {

    }

    public void testAddMetrics() throws Exception {

    }

    public void testSetSaveToHistory() throws Exception {

    }

    public void testGetMetricValueByType() throws Exception {

    }

    public void testIsSaveToHistorySet() throws Exception {

    }

    public void testToString() throws Exception {

    }
}