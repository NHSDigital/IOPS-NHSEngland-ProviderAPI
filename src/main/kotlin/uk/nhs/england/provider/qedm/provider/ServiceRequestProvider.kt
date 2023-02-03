package uk.nhs.england.provider.qedm.provider

import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.england.provider.qedm.awsProvider.AWSPatient
import uk.nhs.england.provider.qedm.interceptor.CognitoAuthInterceptor
import javax.servlet.http.HttpServletRequest

@Component
class ServiceRequestProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor,
                             val awsPatient: AWSPatient,
    ) : IResourceProvider {
    override fun getResourceType(): Class<ServiceRequest> {
        return ServiceRequest::class.java
    }

   
    @Search
    fun search(
        httpRequest : HttpServletRequest,
        @RequiredParam(name = "patient:identifier") nhsNumber : TokenParam,

    ): List<ServiceRequest> {
        val serviceRequests = mutableListOf<ServiceRequest>()
        if (nhsNumber.value == null || nhsNumber.system == null) throw UnprocessableEntityException("Malformed patient identifier parameter")
        val patient = awsPatient.getPatient(Identifier().setSystem(nhsNumber.system).setValue(nhsNumber.value))
        if (patient != null) {
            val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, "patient="+patient.idElement.idPart)
            if (resource != null && resource is Bundle) {
                for (entry in resource.entry) {
                    if (entry.hasResource() && entry.resource is ServiceRequest) serviceRequests.add(entry.resource as ServiceRequest)
                }
            }
        }
        return serviceRequests
    }
}