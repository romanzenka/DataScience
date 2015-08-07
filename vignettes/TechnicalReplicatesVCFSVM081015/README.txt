In this vignette I attempted to use a Support Vector Machine (SVM) to filter out technical errors in genomics data.

BACKGROUND:
We are trying to implement a clinical test using microbial sequencing.  When bacterial isolates come in, the system needs to determine if two samples are 'clonal' or not.  This helps doctors know if a patient came into the hospital with an infection or if they acquired the infection in the hospital.  Hospital staff can then figure out if a sink or some other device is the source of the infection.

The clinical pipeline for handling this type of data is beyond the scope of this vignette.  Several relatedness metrics are calculated by that pipeline including average nucleotide identity (ANI), multi-locus sequence typing (MLST) and Single Nucleotide Polymorphism (SNP) detection.  Prior to this skunkworks project described in this vignette, it was found that SNP detection is more sensitive than other methods, but there are several technical artifacts that we need to filter out to increase the specificity of the test.  

Technical Replicates (the same data sequenced many times) where done to determine the reproducibility of the test.  If the test is reproducible, there will be a threshold (counted in the number of SNPs) that makes it possible on any given sample to understand the amount of error that can be tolerated before two samples can not be distinguished between one another.  In the technical replicate sequencing, three isolates where sequenced: WGS019, WGS021, and USA1000.  In an ANI phylogenetic analysis (Figure 1: S_aureus_ANI.png) these isolates where determined to be very close, but differentiate-able.  The methods used for this analysis would probably be very interesting for a future vignette.  In short, R was used to cluster the data using APCluster (Affinity Propagation clustering in R).

Table 1: ImprovementsWithThresholding.xlsx shows how technical replicates from the same sample are indistinguishable from technical replicates from a different sample.   At first the technical replicates for several technical replicates in each group are marked incorrectly.  This is undesirable because it indicates that isolates that are different (e.g. USA1000 and WGS019) are not distinguishable from each other.  As we play with some custom thresholds we designed, you can see that the seperate-ability of the classes in the spreadsheet improves dramatically.

GOAL:
A method was hand crafted the substantially reduced the error found in the technical replicates that results in 96%+ accuracy.  There are two problems: 1) the method is sensitive to particular mapping differences found between USA1000 and close relatives and the reference (a USA300 type strain), and 2) the hand crafted method took MANY hours to craft (well over a month).  The goal of this vignette, was to use machine learning (specifically support vector machines) to rapidly gain the insights found by the engineer in a fraction of the time and produce an error filtering script with the same or better accuracy in a fraction of the time.

CODE:
The pre-process code that was used to create testing and training files for the SVM from the raw VCF can be found in: VCFMachineLearningTest.  TODO: give people access to the raw data!

TOOLS:
For this, I just used libsvm.  The setup script has the download paths.  Once downloaded, just extract the tar, change to the directory and make.  There is a version of libsvm in C and Java.  If I was going to use the classifier in production, I would probably use the java classifier imbedded in my project.  For the experiments here, I just used the c executables.

DATA:
The setup script should bring down the original VCF file (MultiSample.vcf) and also 5 processed files:
vcf_all - all of the sample pairs from the vcf formatted for the SVM
vcf_train_scaled.model - the svm model
vcf_train - every 5th record from all above (used for training)
vcf_train_scaled - every 5th record from above scaled.

IMPORTANT: when scaling data it is essential to scale the entire dataset together before splitting into training and testing sets.  While not completely correct (all contains train, and they should be separate) for the experiment here it DOES NOT MATTER because predictions will be higher not lower.  If the SVM could get even close, I would go back and do it properly. (see below)

Commands used in the final evaluation:
$ ./svm-scale vcf_all > vcf_all_scaled

and finally the prediction:
$ ./svm-predict vcf_all_scaled vcf_train_scaled.model /dev/null
Accuracy = 70.7678% (644978/911400) (classification)

I did use the guide to train parameters.

Here is a dump of a bunch of raw tries on the data:
Here is how to use the svm:
  830  svm-train heart_scale
  831  ./svm-train heart_scale
  832  ls
  833  vim heart_scale.model
  834  wc heart_scale
  835  wc heart_scale.model
  836  vim README
  837  vim README
  838  ls
  839  head heart_scale
  840  head /tmp/foo.txt
  841  head -n 1000 /tmp/foo.txt > vcf_raw.txt
  842  ./svm-scale
  843  ./svm-scale vcf_raw.txt
  844  ./svm-scale vcf_raw.txt > vcf_scaled.txt
  845  ls
  846  cat vcf_scaled.txt
  847  ls
  848  head -n 2000 /tmp/foo.txt | tail -n 1000 > vcf_raw.t
  849  less vcf_raw.t
  850  history | grep svm-scale
  851  ./svm-scale vcf_raw.t > vcf_scaled.t
  852  ./svm-train vcf_scaled.txt
  853  ls
  854  ./svm-predict
  855  ./svm-predict vcf_scaled.t vcf_scaled.txt.model foo.txt
  856  vim foo.txt
  857  rm foo.txt
  858  python tools/grid.py vcf_scaled.txt
  859  head -n 10000 /tmp/foo.txt | tail -n 9000 > vcf_raw.train
  860  ls
  861  rm vcf_*
  862  ls
  863  head -n 10000 /tmp/foo.txt > vcf_raw.train
  864  head -n 11000 /tmp/foo.txt | tail -n 1000 > vcf_raw.test
  865  history | grep scale
  866  ./svm-scale vcf_raw.train > vcf_scaled.train
  867  ./svm-scale vcf_raw.test > vcf_scaled.test
  868  ./svm-train vcf_scaled.train
  869  ./svm-predict vcf_scaled.test vcf_scaled.train.model foo.txt
  870  python tools/grid.py vcf_scaled.train
  871  ls
  872  rm vcf_*
  873  ls
  874  head -n 10000 /tmp/foo.txt > vcf_raw
  875  ./svm-scale vcf_raw > vcf_raw.scaled
  876  head -n 2000 vcf_raw.scaled > vcf_raw.scaled.test
  877  tail -n 8000 vcf_raw.scaled > vcf_raw.scaled.train
  878  ./svm-train vcf_raw.scaled.train
  879  ./svm-predict vcf_raw.scaled.test
  880  ./svm-predict vcf_raw.scaled.test vcf_raw.scaled.train.model
  881  ./svm-predict vcf_raw.scaled.test vcf_raw.scaled.train.model /dev/null
  882  rm -rf /tmp/foo.txt
  883  wc -l /tmp/foo.txt
  884  ls -alh /tmp/foo.txt
  885  ls -alh /tmp/foo.txt
  886  wc -l /tmp/foo.txt
  887  ls -alh /tmp/foo.txt
  888  ls -alh /tmp/foo.txt
  889  ls -alh /tmp/foo.txt
  890  ls -alh /tmp/foo.txt
  891  ls -alh /tmp/foo.txt
  892  wc -l /tmp/foo.txt
  893  wc -l /tmp/foo.txt
  894  tail -n 10000 /tmp/foo.txt > vcf_train
  895  head -n /tmp/foo.txt > vcf_test
  896  head -n 10000 /tmp/foo.txt > vcf_test
  897  ls
  898  rm vcf*
  899  head -n 10000 /tmp/foo.txt > vcf_test
  900  tail -n 10000 /tmp/foo.txt > vcf_train
  901  history | grep vcf-s
  902  history | grep vcf-scale
  903  history | grep vcf
  904  ls
  905  tail -n 10000 /tmp/foo.txt > vcf
  906  head -n 10000 /tmp/foo.txt >> vcf
  907  rm vcf_t*
  908  ls
  909  history | grep vcf
  910  ./svm-scale vcf > vcf_scaled
  911  head -n 10000 vcf_scaled > vcf_scaled_test
  912  tail -n 10000 vcf_scaled > vcf_scaled_train
  913  ./svm-train vcf_scaled_train
  914  ./svm-predict
  915  ./svm-predict vcf_scaled_test vcf_scaled_train.model /dev/null
  916  ./svm-predict vcf_scaled_train vcf_scaled_train.model /dev/null
  917  vim README
  918*
  919  vim README
  920  python tools/easy.py
  921  python tools/easy.py vcf_scaled_train vcf_scaled_test
  922  cd tools/
  923  python easy.py ../vcf_scaled_train ../vcf_scaled_test
  924  vim easy.py
  925  python easy.py ../vcf_scaled_train ../vcf_scaled_test
  926  vim easy.py
  927  python easy.py ../vcf_scaled_train ../vcf_scaled_test
  928  vim README
  929  ls
  930  rm vcf_scaled_t*
  931  ls
  932  cd ..
  933  ls
  934  vim README
  935  rm vcf_scaled_train.model
  936  ./svm-train
  937  ./svm-train -t 0 vcf_scaled_train
  938  ./svm-predict
  939  ./svm-predict vcf_scaled_train vcf_scaled_train.model
  940  ./svm-predict vcf_scaled_train vcf_scaled_train.model /dev/null
  941  ls
  942  rm vcf*
  943  history | grep vcf
  944  tail -n 10000 /tmp/foo.txt > vcf
  945  head -n 10000 /tmp/foo.txt >> vcf
  946  ./svm-scale vcf > vcf_scaled
  947  head -n 10000 vcf_scaled > vcf_scaled_test
  948  tail -n 10000 vcf_scaled > vcf_scaled_train
  949  ./svm-train vcf_scaled_train
  950  ./svm-predict vcf_scaled_test vcf_scaled_train.model /dev/null
  951  ./svm-predict vcf_scaled_test vcf_scaled_train.model svmout.txt
  952  vim svmout.txt
  953  vim vcf_scaled_train.model
  954  ls
  955  less vcf_scaled
  956  less /tmp/foo.txt
  957  rm vcf*
  958  history | grep vcf
  959  tail -n 10000 /tmp/foo.txt > vcf
  960  head -n 10000 /tmp/foo.txt >> vcf
  961  ./svm-scale vcf > vcf_scaled
  962  less vcf_scaled
  963  less /tmp/foo.txt
  964  less vcf_scaled
  965  less /tmp/foo.txt
  966  less /tmp/foo.txt
  967  history | grep vcf
  968  tail -n 10000 /tmp/foo.txt > vcf
  969  head -n 10000 /tmp/foo.txt >> vcf
  970  ./svm-scale vcf > vcf_scaled
  971  less vcf_scaled
  972  head -n 10000 vcf_scaled > vcf_scaled_test
  973  tail -n 10000 vcf_scaled > vcf_scaled_train
  974  ./svm-train vcf_scaled_train
  975  /svm-predict vcf_scaled_test vcf_scaled_train.model /dev/null
  976  ./svm-predict vcf_scaled_test vcf_scaled_train.model /dev/null
  977  cd tools/
  978  ls
  979  python grid.py ../vcf_scaled_train
  980  cd ..
  981  ls
  982  ./svm-train -c 32768.0 -g 2.0 vcf_scaled
  983  history | grep svm-scale
  984  ./svm-scale /tmp/foo.txt > vcf_all_scaled
  985*
  986  rm vcf*
  987  history | grep vcf
  988  ./svm-scale /tmp/foo.txt > vcf_train_scaled
  989  ./svm-train -c 32768.0 -g 2.0 vcf_train_scaled
  990  ls
  991  history | grep svm
  992  mv /tmp/foo.txt vcf_all
  993  mv vcf_all vcf_all_20
  994  ls
  995  mv vcf_all_20 vcf_train
  996  mv /tmp/foo.txt vcf_all
  997  ./svm-scale vcf_all > vcf_all_scaled
  998  ./svm-predict vcf_all_scaled vcf_train_scaled /dev/null
  999  ./svm-predict vcf_all_scaled vcf_train_scaled.model /dev/null
 1000  cd
 1001  cd -
 1002  history
R5038574:libsvm-3.20 m102417$ pwd
/Users/m102417/tools/libsvm-3.20






