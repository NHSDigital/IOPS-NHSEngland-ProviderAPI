
mvn clean install

docker build -t fhir-provider-api .

docker tag fhir-provider-api:latest 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-provider-api:latest
docker tag fhir-provider-api:latest 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-provider-api:1.0.8

docker push 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-provider-api:latest

docker push 365027538941.dkr.ecr.eu-west-2.amazonaws.com/fhir-provider-api:1.0.8
