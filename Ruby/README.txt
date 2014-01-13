Program has one external library ruby-narray.
First install library with script upgma.sh and then run program.

The program has 3 required arguments.

Required:

first argument - path to file containing sequence information

second argument - type of input file (FASTA or sequence)

third argument - method used for sequence reading (jc69 - Jukes-Cantor or k80 - Kimura)

Example:
ruby upgma.rb sekvence.fasta FASTA jc69