elements-data
=============

Project applying big data processing to page correspondence data for versions of Diderot's Éléments de Physiologie.

The project is incomplete, but represents a draft of processing methods under the map reduce paradigm for the data 
compiled by Jean Mayer in his "La composition fragmentaire des Éléments de Physiologie", Studies on Voltaire and the 
18th Century, (1988).

Under the map-reduce paradigm, the reducers in this code do two things : create a minimal part-of-grammar / POS for
a page correspondence data set, and mark up the digital text of the versions in question with XML or HTML range tags, 
based upon the page correspondence data set. They both utilize the same mapper, which performs the initial distillation 
of the dataset from a file in HFDS.

This code is being checked in as is (with minimal cleanup) as a record of its initial Hadoop 1.x.x implementation, and 
short of full integration testing of the mark up functionality. I envision that its map-reduce code will be converted to run 
upon YARN and be one of many processing techniques in the project, and having initiated "elements-data" as a public 
project, its development will visit, then, full integration testing of the markup functionality.

Gregory Bringman
June 14, 2014