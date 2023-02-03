package uk.nhs.england.provider.qedm

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.rest.api.EncodingEnum
import ca.uhn.fhir.rest.server.RestfulServer
import org.springframework.beans.factory.annotation.Qualifier
import uk.nhs.england.provider.qedm.configuration.FHIRServerProperties
import uk.nhs.england.provider.qedm.interceptor.AWSAuditEventLoggingInterceptor
import uk.nhs.england.provider.qedm.interceptor.CapabilityStatementInterceptor
import uk.nhs.england.provider.qedm.provider.*
import java.util.*
import javax.servlet.annotation.WebServlet

@WebServlet("/FHIR/R4/*", loadOnStartup = 1, displayName = "FHIR Facade")
class FHIRR4RestfulServer(
    @Qualifier("R4") fhirContext: FhirContext,
    public val fhirServerProperties: FHIRServerProperties,
    val appointmentProvider: AppointmentProvider,
    val taskProvider: TaskProvider,
    val serviceRequestProvider: ServiceRequestProvider,
    val documentReferenceProvider:  DocumentReferenceProvider,
    val binaryProvider: BinaryProvider



) : RestfulServer(fhirContext) {

    override fun initialize() {
        super.initialize()

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

        registerProvider(appointmentProvider)
        registerProvider(taskProvider)
        registerProvider(serviceRequestProvider)
        registerProvider(documentReferenceProvider)
        registerProvider(binaryProvider)

        val awsAuditEventLoggingInterceptor =
            AWSAuditEventLoggingInterceptor(
                this.fhirContext,
                fhirServerProperties
            )

        interceptorService.registerInterceptor(awsAuditEventLoggingInterceptor)
        registerInterceptor(CapabilityStatementInterceptor(fhirServerProperties))

        isDefaultPrettyPrint = true
        defaultResponseEncoding = EncodingEnum.JSON
    }
}
