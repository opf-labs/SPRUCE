#!/usr/bin/perl -w

#Performs a preliminary check for duplicate checksums
#in the textnormsha256.txt metadata files
#Will report on files that are the same even if
#they are in different parts of the file system
use File::Find::Rule;

if (scalar @ARGV < 1){
    print "Usage: Enter a file path to search for checksum files.";
    exit;
}

my $rule =  File::Find::Rule->new;
$rule->file;

$rule->name('textnormsha256.txt');
#Chain rules to exclude uninteresting files
#Can just add more exclusion rules
$rule->not_name('*JPG*');
my @files = $rule->in($ARGV[0]);

my %checkhash;
my @duplicates;
my %duplicate_hash;
foreach my $checkfile (@files){
    open FILE, "$checkfile" or die $!;
    while (<FILE>) {
       if (!$checkhash{$_}){
        $checkhash{$_} = [$checkfile];
       }
       else {
        push (@{ $checkhash{$_}}, $checkfile);
       }
    }
   close FILE;
}
open DUPFILE, ">copies.txt" or die $!;
while ( ($k,$v) = each %checkhash ) {
    if (scalar @$v > 1){
        print "Checksum: $k \n";
        print DUPFILE "Checksum: $k \n";
        print "DUPLICATES\n";
        print DUPFILE "DUPLICATES\n";
        
        foreach my $dup(@$v){
            $dup =~s/\/textnormsha256.txt$//;
          print  $dup,"\n";
          print DUPFILE "$dup\n";
        }
       print DUPFILE "\n";        
    }   
}
close DUPFILE;


