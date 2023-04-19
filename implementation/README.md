# Implementation of the BPM System
This directory contains the implementation of a proof-of-concept BPM system that supports the tax administration process defined in the Czech code of law.

## Implementation
The implementation is based on Camunda Platform 8.2 Self-Managed. To use the BPM system, deploy the [executable BPMN model](https://github.com/matouda7/tax-administration-digitization/blob/main/implementation/src/main/resources/tax_management.bpmn) to Camunda Platform 8.2 Self-Managed running on localhost using Docker or Helm/Kubernetes, and build and run the Spring Boot application that is part of this directory. The resulting system consists of Tasklist, Operate, Optimize, Identity, and Keycloak components.

For further details on how to run the Camunda Platform 8 Self-Managed, please visit the corresponding [repository](https://github.com/camunda/camunda-platform).

## Testing
Testing of the process and additional glue code is based on *spring-boot-test* and *spring-zeebe-test*. 

## Example Walkthrough
An example walkthrough showing a possible way of using the resulting BPM system can be seen in the following [video](https://youtu.be/KtZEmfZtAfQ).

---

**Note**: To see a thorough description of the As-is analysis, please refer to the thesis text.
