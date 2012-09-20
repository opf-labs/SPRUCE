#!/usr/bin/perl -w
use File::Find::Rule;
#Find files that match a search string within the metadata content extracts (textextract.txt)

if (scalar @ARGV < 2){
    print "Usage: perl findterm.pl '<FILE PATH>' '<SEARCHTERM>'\n";
    print "Use quotation marks around the arguments if they contain spaces\n";
    exit;
}

my $root = $ARGV[0];
my $searchterm = $ARGV[1];
my $rule =  File::Find::Rule->new;
$rule->file;
$rule->name('textextract.txt');
my @files = $rule->in($root);
my $foundfile = "$searchterm.txt";
open INFO, ">$foundfile" or die $!;

foreach my $checkfile (@files){
     open FILE, "$checkfile" or die $!;
    while (<FILE>) {
        if ($_=~/$searchterm/i){
            print $checkfile,"\n";
            print INFO "$checkfile \n";
            last;
        }
    }
   close FILE;   
}


