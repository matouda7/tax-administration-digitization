# Implementation of the BPM system

The directory contains implementation of the proof-of-concept BPM system supporting tax administration process defined in Czech code of law.

## Implementation

The implementation is based on Camunda Platform 8.2 Self-Managed. To use the BPM system deploy the [executable BPMN model](https://github.com/matouda7/tax-administration-digitization/blob/main/implementation/src/main/resources/tax_management.bpmn) to Camunda Platform 8.2 Self-Managed running on localhost using Docker or Helm/Kubernetes and build and run the Spring boot application. Resulting system consists of Tasklist, Operate, Optimize, Identity and Keycloak components.

For further details how to run the Camunda Platform 8 Self-Managed visit the corresponding [repository](https://github.com/camunda/camunda-platform).  

## Testing

The testing of the process and additional glue code is based on *spring-boot-test* and *spring-zeebe-test*. 

## Example walkthrough

The example walkthrough showing possible way of using the resulting BPM system can be seen in following [video](https://youtu.be/KtZEmfZtAfQ).

---

**Note**: To see the thorough description of the implementation see the text of the thesis.