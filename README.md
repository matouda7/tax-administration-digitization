# Digitization of the Czech tax administration process

The repository that corresponds to the diploma thesis focused on the creation of BPM system using DEMO and Camunda in order to digitize the Tax administration process defined in Czech code of law.

The repository contains a complete set of documents that were created during the case study that was part of the thesis. The case study is focused on the digitization of the Tax administration defined in the Czech code of law. The process of creation of BPM system consists of three subsequent steps:

 - **As-is analysis** of the Tax administration using DEMO
 - **To-be analysis** of the Tax administration using BPMN
 - **Implementation** of BPM system using Camunda Platform 8

## As-is analysis

The As-is analysis describes the current state of the Tax administration process. To reveal and capture the state the OER analysis and DEMO Cooperation and Fact model is used. The results of the analysis are visible in the [as-is_analysis](https://github.com/matouda7/tax-administration-digitization/tree/main/as-is_analysis) directory.

## To-be analysis 

The To-be analysis is used design and show the possible to-be solution of the process after the digitization is completed. The BPMN level 2 is used to capture the to-be state. The result of the analysis is shown in [to-be_anaylsis](https://github.com/matouda7/tax-administration-digitization/tree/main/to-be_analysis) directory. 

## Implementation

To proof the presented concept the implementation of the designed solution using BPM system is created. The final solution is based on the Camunda Platform 8 Self-managed version. The files created during the implementation of the BPM system can be seen in the [implementation](https://github.com/matouda7/tax-administration-digitization/tree/main/implementation) repository. The directory contains all files that are necessary to run the system and deploy the process model.
