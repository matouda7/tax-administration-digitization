# Digitization of the Czech tax administration process

The repository corresponds to the diploma thesis that focuses on the creation of a BPM system using DEMO, BPMN and Camunda to digitize the tax administration process defined in the Czech code of law. To see a detailed description of creation process, refer to the thesis text.

The repository contains a complete set of documents created during the case study, which was a part of the thesis. The case study focused on the digitization of tax administration as defined in the Czech code of law. The methodology defining the process of creating the BPM system consists of three subsequent steps:

- **As-is analysis** of the tax administration using DEMO
- **To-be analysis** of the tax administration using BPMN
- **Implementation** of BPM system using Camunda Platform 8

## As-is analysis

The As-is analysis describes the current state of the tax administration process. To reveal and capture this state, the OER analysis and DEMO Cooperation and Fact Models are used. The results of the analysis are visible in the [as-is-analysis](https://github.com/matouda7/tax-administration-digitization/tree/main/as-is-analysis) directory

## To-be analysis 

The To-be analysis is used to design and show a possible solution of the process after the digitization is completed. The BPMN level 2 is used to capture the to-be state. The results of the analysis are shown in the [to-be-analysis](https://github.com/matouda7/tax-administration-digitization/tree/main/to-be-analysis) directory.

## Implementation

To prove the presented concept, the designed proof-of-concept solution using a BPM system is implemented. The final solution is based on the Camunda Platform 8 Self-Managed version. The files created during the implementation of the BPM system can be seen in the [implementation](https://github.com/matouda7/tax-administration-digitization/tree/main/implementation) repository. The directory contains all the necessary files to deploy the process model and run the proof-of-concept system.
