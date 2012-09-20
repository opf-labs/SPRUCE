#!/usr/bin/perl -w
use File::Find::Rule;
use File::Compare;
#Find files with filenames matching a term.
#use a glob expression to match file names. Possible
#combinations are: begins with, ends with, contains
#e.g. geoff, *geoff.*, *geoff.doc
#Will recurse downwards through directories
#TODO try to match more than one directory
#Report is named after the search term

if (scalar @ARGV < 2){
    print "Usage: perl comparenames.pl '<FILE PATH>' '<SEARCHTERM>'\n";
    print "Use quotation marks around both arguments\n";
    exit;
}
my $matchterm = $ARGV[1];
my $root = $ARGV[0];
print 'matchterm'. $matchterm,"\n";
my $rule =  File::Find::Rule->new;
$rule->file;
$rule->name($matchterm);
my @files = $rule->in($root);

my $foundfile ="namecomparisons.txt";
if (scalar @files ==0){
    print "No matches\n";
    exit;
}
open (INFO, ">", $foundfile) or die "Could not open file $foundfile";

foreach my $line (@files){
    print $line, "\n";
    print INFO "$line \n";
}

close INFO;




