In this vignette I attempted to use a Support Vector Machine (SVM) to filter out technical errors in genomics data.

BACKGROUND:
We are trying to implement a clinical test using microbial sequencing.  When bacterial isolates come in, the system needs to determine if two samples are 'clonal' or not.  This helps doctors know if a patient came into the hospital with an infection or if they aquired the infection in the hospital.  Hospital staff can then figure out if a sink or some other divice is the source of the infection.

The clinical pipeline for handling this type of data is beond the scope of this vinnette.  Several relatedness metrics are calculated by that pipeline including average nucelotide identity (ANI), multi-locus sequence typing (MLST) and Single Nucleotide Polymorphism(SNP) detection.  Prior to the skunkwork project described in this vinnette, it was found that SNP detection is more sensitive than other methods, but there are several technical artifacts that we need to filter out to increase the specificity of the test.  

Table 1: foo.xls shows how technical replicates from the same sample are indistiguishable from technical replicates from a different sample. 








