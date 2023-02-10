package uk.nhs.england.provider.qedm.provider

import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.param.TokenParam
import ca.uhn.fhir.rest.server.IResourceProvider
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException
import org.hl7.fhir.r4.model.*
import org.springframework.stereotype.Component
import uk.nhs.england.provider.qedm.awsProvider.AWSPatient
import uk.nhs.england.provider.qedm.configuration.FHIRServerProperties
import uk.nhs.england.provider.qedm.interceptor.CognitoAuthInterceptor
import uk.nhs.england.provider.qedm.util.FhirSystems
import javax.servlet.http.HttpServletRequest

@Component
class DocumentReferenceProvider(var cognitoAuthInterceptor: CognitoAuthInterceptor,
val awsPatient: AWSPatient,
                              val  fhirServerProperties: FHIRServerProperties
) : IResourceProvider {
    override fun getResourceType(): Class<DocumentReference> {
        return DocumentReference::class.java
    }

    @Read
    fun read(httpRequest : HttpServletRequest, @IdParam internalId: IdType): DocumentReference? {
        val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, null)
        return if (resource is DocumentReference) fixUrl(resource as DocumentReference) else null
    }

    fun fixUrl(documentReference: DocumentReference) : DocumentReference {
        if (documentReference.hasContent() ) {
            for (content in documentReference.content) {
                if (content.hasAttachment() && content.attachment.hasUrl()) {
                    if (content.attachment.url.startsWith("http://localhost:")) {
                        var urls = content.attachment.url.split("Binary")
                        if (urls.size>1) content.attachment.url = fhirServerProperties.server.baseUrl + "/FHIR/R4/Binary"+ urls[1]
                    }
                }
            }
        }
        return documentReference
    }
   
    @Search
    fun search(
        httpRequest : HttpServletRequest,
        @RequiredParam(name  = "patient:identifier") nhsNumber : TokenParam

    ): List<DocumentReference> {
        val documents = mutableListOf<DocumentReference>()
        if (nhsNumber.value == null || nhsNumber.system == null) throw UnprocessableEntityException("Malformed patient identifier parameter")
        val patient = awsPatient.getPatient(Identifier().setSystem(nhsNumber.system).setValue(nhsNumber.value))
        if (patient != null) {
            val resource: Resource? = cognitoAuthInterceptor.readFromUrl(httpRequest.pathInfo, "patient="+patient.idElement.idPart)
            if (resource != null && resource is Bundle) {
                for (entry in resource.entry) {
                    if (entry.hasResource() && entry.resource is DocumentReference) {
                        val documentReference = fixUrl(entry.resource as DocumentReference)

                        documents.add(documentReference)
                    }
                }
            }
        }
        return documents
    }
}
