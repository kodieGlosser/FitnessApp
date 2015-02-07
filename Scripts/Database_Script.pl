#!/usr/bin/perl

use strict;
use warnings;
use DBI;

my $dbh = DBI->connect (
	"dbi:SQLite:dbname='/data/data/com.android.toledo.FitnessApp/databases/FitnessApp.db'",
	"",
	"",
	{ RaiseError => 1 },
) or die $DBI::errstr;

my $filename = 'workouts.txt';
open (my $fh, '<:encoding(UTF-8)', $filename) or die "Could not open file '$filename' $!";

while (my $row = <$fh>) {
	# my @values = split(',', $row);

	# my $muscleGroup;
	# my $equipmentType;
	# my $exerciseName;
	# my $targetMuscle;
	# my $arrSize = @values;

	# for (my $i=0; $i <= $arrSize; $i++) {
		# if ($i == 0) {
			# $muscleGroup = $values[$i]; 
			# }
		# elsif ($i == 1) {
			# $equipmentType = $values[$i];
			# }
		# elsif ($i == 2) {
			# $exerciseName = $values[$i];
			# }
		# elsif ($i == 3) {
			# $targetMuscle = $values[$i];
			# }
	# }

	# my $query = "insert into Exercises values ($muscleGroup, $equipmentType, $exerciseName, $targetMuscle)";
	my $query2 = "insert into Exercises values ($row);";
	my $sth = $dbh->prepare("query2");
	$sth->execute();
	print $query2;
	print "\n";
}