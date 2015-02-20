#!/usr/bin/perl

use strict;
use warnings;
use DBI;

my $dbh = DBI->connect (
	#"dbi:SQLite:dbname='/data/data/com.android.toledo.FitnessApp/databases/FitnessApp.db'",
	"dbi:SQLite:dbname=gymtrackr.db",
	"",
	"",
	{ RaiseError => 1 },
) or die $DBI::errstr;

#Exercises
$dbh->do("CREATE TABLE IF NOT EXISTS Exercises (
				_id INTEGER PRIMARY KEY, 
				muscleGroup TEXT, 
				equipmentType TEXT, 
				name TEXT, 
				targetMuscle TEXT
			)");
		
# History
# The date is given as ("YYYY-MM-DD HH:MM:SS.SSS")
$dbh->do("CREATE TABLE IF NOT EXISTS History (
				_id INTEGER PRIMARY KEY,
				date TEXT, 
				weight INTEGER,
				rep INTEGER,
				sequence INTEGER,
				exercise INTEGER,
				FOREIGN KEY(exercise)
					REFERENCES Exercises(_id)
					ON DELETE CASCADE
			)");
			
#Planned Workout
$dbh->do("CREATE TABLE IF NOT EXISTS Planned_Workout (
				_id INTEGER PRIMARY KEY,
				sequence INTEGER,
				workoutTitle TEXT,
				notes TEXT
			)");
			
#In Progress Workout
$dbh->do("CREATE TABLE IF NOT EXISTS In_Progress_Workout (
				_id INTEGER PRIMARY KEY,
				sequence INTEGER,
				workoutTitle TEXT,
				notes TEXT
			)");
			
#Circuit
$dbh->do("CREATE TABLE IF NOT EXISTS Circuit (
				_id INTEGER PRIMARY KEY,
				weight INTEGER,
				rep INTEGER,
				sequence INTEGER,
				exercise INTEGER,
				FOREIGN KEY(exercise)
					REFERENCES Exercises(_id)
					ON DELETE CASCADE
			)");
			
#Planned Union
$dbh->do("CREATE TABLE IF NOT EXISTS Planned_Union (
				_id INTEGER PRIMARY KEY,
				circuit INTEGER,
				plannedWorkout INTEGER,
				FOREIGN KEY(circuit)
					REFERENCES Circuit(_id)
					ON DELETE CASCADE,
				FOREIGN KEY(plannedWorkout)
					REFERENCES Planned_Workout(_id)
					ON DELETE CASCADE
			)");
			
#In Progress Union
$dbh->do("CREATE TABLE IF NOT EXISTS In_Progress_Union (
				_id INTEGER PRIMARY KEY,
				circuit INTEGER,
				inProgressWorkout INTEGER,
				FOREIGN KEY(circuit)
					REFERENCES Circuit(_id)
					ON DELETE CASCADE,
				FOREIGN KEY(inProgressWorkout)
					REFERENCES	In_Progress_Workout(_id)
					ON DELETE CASCADE
			)");
			
#Android MetaData
#	CREATE TABLE "android_metadata" ("locale" TEXT DEFAULT 'en_US')
#	INSERT INTO "android_metadata" VALUES ('en_US')
			
my $filename = 'workouts.txt';
open (my $fh, '<:encoding(UTF-8)', $filename) or die "Could not open file '$filename' $!";

while (my $row = <$fh>) {
	my @values = split(', ', $row);

	my $muscleGroup;
	my $equipmentType;
	my $exerciseName;
	my $targetMuscle;
	my $arrSize = @values;

	for (my $i=0; $i <= $arrSize; $i++) {
		if ($i == 0) {
			$muscleGroup = $values[$i]; 
			}
		elsif ($i == 1) {
			$equipmentType = $values[$i];
			}
		elsif ($i == 2) {
			$exerciseName = $values[$i];
			}
		elsif ($i == 3) {
			$targetMuscle = $values[$i];
			$targetMuscle =~ tr/"%'+\-0-9<=>A-Z_a-z{|}//cd;
			}
	}

	my $query2 = "insert into Exercises (muscleGroup,equipmentType,name,targetMuscle) values ('$muscleGroup','$equipmentType','$exerciseName','$targetMuscle')";
	my $sth = $dbh->prepare($query2);
	$sth->execute();
	print $query2;
	print "\n";
}
$dbh->disconnect;