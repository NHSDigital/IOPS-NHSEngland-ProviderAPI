package uk.nhs.england.provider.qedm.configuration


import ca.uhn.fhir.context.FhirContext
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType

import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration



@Configuration
open class OpenApiConfig(@Qualifier("R4") val ctx : FhirContext) {

    var HEALTH_ADMIN = "Health Administration"
    var DOCUMENTS = "Clinical Documents"

    @Bean
    open fun customOpenAPI(
        fhirServerProperties: FHIRServerProperties
       // restfulServer: FHIRR4RestfulServer
    ): OpenAPI? {

        val oas = OpenAPI()
            .info(
                Info()
                    .title(fhirServerProperties.server.name)
                    .version(fhirServerProperties.server.version)
                    .description(
                        "All implementations and NHS England projects **MUST** adhere to NHS England profile and test this using [FHIR Validation](http://hl7.org/fhir/R4/validation.html). These profiles are all derived fro UKCore and so it is not necessary to test against UKCore."
                                + "\n\n The scope of this profile is **England National** and does not the English NHS at local or regional level."
                                + "\n" +
                                "\n | HL7 FHIR | UK Core | NHS England |"
                                + "\n |-----|------|-----|"
                                + "\n | [Appointment](http://hl7.org/fhir/R4/appointment.html)| | [NHSDigital-Appointment](https://simplifier.net/guide/NHSDigital/Home/FHIRAssets/AllAssets/Profiles/NHSDigital-Appointment.guide.md?version=current) |"
                                + "\n | [ServiceRequest](http://hl7.org/fhir/R4/servicerequest.html)| | [NHSDigital-ServiceRequest](https://simplifier.net/guide/NHSDigital/Home/FHIRAssets/AllAssets/Profiles/NHSDigital-ServiceRequest.guide.md?version=current) |"
                                + "\n | [Task](http://hl7.org/fhir/R4/task.html)| | [NHSDigital-Task](https://simplifier.net/guide/NHSDigital/Home/FHIRAssets/AllAssets/Profiles/NHSDigital-Task.guide.md?version=current) |" +

                        "\n\n ## FHIR Implementation Guides"
                                + "\n\n All implementations and NHS England projects **SHOULD** aim to adhere to the **current** version of the following implementation guides."
                                + "\n\n [UK Core Implementation Guide](https://simplifier.net/HL7FHIRUKCoreR4)"
                                + "\n\n [NHS England Implementation Guide](https://simplifier.net/guide/nhsdigital)"
                                        + "\n\n ### Test Patients "
                                        + "\n\n This server is preloaded with data from several NHS England projects. The following NHS Numbers contain examples: "
                                        + "\n\n | NHS Number | Patient Name | Data Source | "
                                        + "\n |---|---|---|"
                                        + "\n |9000000009|Julie Jones|eRS|"
                                        + "\n |9432003812|John Reardon|Pathology/Genomics|"
                                        + "\n |9876543210|Rachel Kanfeld|EPS|" +
                        "\n\n ## NHS England API Documentation"
                                + "\n\n | API | NHS England API | Provider API | Notes | "
                                + "\n |---|---|---|---|"
                                + "\n | [Booking and Referral - FHIR API](https://digital.nhs.uk/developer/api-catalogue/booking-and-referral-fhir) | X | X | See `Get referrals for a patient` and `Get bookings for a patient` |"
                                + "\n | [Electronic Prescription Service - FHIR API](https://digital.nhs.uk/developer/api-catalogue/electronic-prescription-service-fhir) | X | - | See `Search for summary details about a prescription` | "
                                + "\n | [e-Referral Service Patient Care – FHIR API](https://digital.nhs.uk/developer/api-catalogue/e-referral-service-patient-care-fhir) | X | - | See `Retrieve referral requests for a patient`. This API is a provider API for Wayfinder | "
                                + "\n | [Patient Care Aggregator - FHIR API](https://digital.nhs.uk/developer/api-catalogue/patient-care-aggregator-fhir) | - | X | AKA Wayfinder. The provider API is not currently documented | "
                                + "\n | GP Connect Prescription Management | X | - | Check status of this API | "
                                + "\n | [National Record Locator - FHIR API]() | X | X | FHIR R4 version is in progress. This documentation only covers retrieval of documents, clinical data is covered elsewhere. | "
                        + "\n\n ## API Cross Reference"
                                + "\n\n ### Provider API"
                                + "\n\n | API | Appointment | Binary | DocumentReference | ServiceRequest | Task | "
                                + "\n |---|---|---|---|----|-----|"
                                + "\n | Booking and Referral - FHIR API | X |  |  |  |  |"
                                + "\n | Patient Care Aggregator - FHIR API | X | in-progress | in-progress  | X | in-progress | "
                                + "\n | National Record Locator - FHIR API |   | X |    |   |  | "
                                + "\n\n ### NHS England API"
                                + "\n\n | API | Appointment | Binary | DocumentReference | ServiceRequest | Task | "
                                + "\n |---|---|---|---|----|-----|"
                                + "\n | Booking and Referral - FHIR API | X? |  |  | X |  |"
                                + "\n | Electronic Prescription Service - FHIR API |  |  |  |  | X | "
                                + "\n | e-Referral Service Patient Care – FHIR API | future | future | future  | X | future | "
                                + "\n | GP Connect Prescription Management |  |  |  |  | X | "
                                + "\n | National Record Locator - FHIR API |   |  | X   |   |  | "
                                + "\n\n It is believed the scope of these API's could be extended to support regional interop. This would involve adding API's covering `Encounter`, `EpisodeOfCare`, `Patient`, `Schedule` and `Slot`. A high level view of this is contained in the [HL7 FHIR Administration Module](https://www.hl7.org/fhir/r4/administration-module.html)."
                    )
                    .termsOfService("http://swagger.io/terms/")
                    .license(License().name("Apache 2.0").url("http://springdoc.org"))
            )
        oas.addServersItem(
            Server().description(fhirServerProperties.server.name).url(fhirServerProperties.server.baseUrl)
        )

        // MHD



        oas.addTagsItem(
            io.swagger.v3.oas.models.tags.Tag()
                .name(HEALTH_ADMIN)
                .description("")
        )

        oas.addTagsItem(
            io.swagger.v3.oas.models.tags.Tag()
                .name(DOCUMENTS)
                .description("")
        )




        // Binary

        var binaryItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(DOCUMENTS)
                    .summary("Get raw document (Local/Regional: Wayfinder + implied by NRLF)")
                    .responses(getApiResponsesBinary())
                    .addParametersItem(Parameter()
                        .name("id")
                        .`in`("path")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The ID of the resource")
                        .schema(StringSchema())
                        .example("3074093f-183b-47d1-a16c-ea5c101b5451")
                    )
            )
        oas.path("/FHIR/R4/Binary/{id}",binaryItem)

   
        // DocumentReference
        var documentReferenceItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(DOCUMENTS)
                    .summary("Search DocumentReference (Local/Regional: Wayfinder, National: eRS NRLF)")
                    .description("This is only a minimum set of query parameters to be supported.")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient:identifier")
                        .`in`("query")
                        .required(true)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Who/what is the subject of the document. `https://fhir.nhs.uk/Id/nhs-number|{nhsNumber}` ")
                        .schema(StringSchema())
                        .example("https://fhir.nhs.uk/Id/nhs-number|9000000009")
                    )
                    .addParametersItem(Parameter()
                        .name("subject")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("This is the STU3 NRL way of searching. Unsure of R4 version `https://demographics.spineservices.nhs.uk/STU3/Patient/{nhsNumber}`")
                        .schema(StringSchema())

                    )
            )
        oas.path("/FHIR/R4/DocumentReference",documentReferenceItem)


        // Appointment
        var appointmentItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(HEALTH_ADMIN)
                    .summary("Search Appointment (Local/Regional: BARS? + Wayfinder)")
                    .description("BARS Wayfinder")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient:identifier")
                        .`in`("query")
                        .required(true)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Who/what is the subject of the Appointment. `https://fhir.nhs.uk/Id/nhs-number|{nhsNumber}` Note BARS example is invalid FHIR `https://fhir.nhs.uk/Id/nhs-number:{nhsNumber}`")
                        .schema(StringSchema())
                        .example("https://fhir.nhs.uk/Id/nhs-number|9000000009")
                    )
            )
        oas.path("/FHIR/R4/Appointment",appointmentItem)

        // Task
        var taskItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(HEALTH_ADMIN)
                    .summary("Search Task (Supplier: Wayfinder \n" + " National: EPS, GP Connect PFS and eRS)")
                    .description("Local: Wayfinder \n National: EPS and eRS")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient:identifier")
                        .`in`("query")
                        .required(true)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Who/what is the subject of the Task `https://fhir.nhs.uk/Id/nhs-number|{nhsNumber}`")
                        .schema(StringSchema())
                        .example("https://fhir.nhs.uk/Id/nhs-number|9000000009")
                    )
            )
        oas.path("/FHIR/R4/Task",taskItem)

        // ServiceRequest
        var serviceRequestItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(HEALTH_ADMIN)
                    .summary("Search ServiceRequest (Local/Regional: BARS?, National: eRS(for Wayfinder))")
                    .description("")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient:identifier")
                        .`in`("query")
                        .required(true)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Who/what is the subject of the ServiceRequest `https://fhir.nhs.uk/Id/nhs-number|{nhsNumber}`")
                        .schema(StringSchema())
                        .example("https://fhir.nhs.uk/Id/nhs-number|9000000009")
                    )
            )
        oas.path("/FHIR/R4/ServiceRequest",serviceRequestItem)





        return oas
    }



    fun getApiResponses() : ApiResponses {

        val response200 = ApiResponse()
        response200.description = "OK"
        val exampleList = mutableListOf<Example>()
        exampleList.add(Example().value("{}"))
        response200.content = Content().addMediaType("application/fhir+json", MediaType().schema(StringSchema()._default("{}")))
        val apiResponses = ApiResponses().addApiResponse("200",response200)
        return apiResponses
    }

    fun getApiResponsesMarkdown() : ApiResponses {

        val response200 = ApiResponse()
        response200.description = "OK"
        val exampleList = mutableListOf<Example>()
        exampleList.add(Example().value("{}"))
        response200.content = Content().addMediaType("text/markdown", MediaType().schema(StringSchema()._default("{}")))
        val apiResponses = ApiResponses().addApiResponse("200",response200)
        return apiResponses
    }
    fun getApiResponsesXMLJSON() : ApiResponses {

        val response200 = ApiResponse()
        response200.description = "OK"
        val exampleList = mutableListOf<Example>()
        exampleList.add(Example().value("{}"))
        response200.content = Content()
            .addMediaType("application/fhir+json", MediaType().schema(StringSchema()._default("{}")))
            .addMediaType("application/fhir+xml", MediaType().schema(StringSchema()._default("<>")))
        val apiResponses = ApiResponses().addApiResponse("200",response200)
        return apiResponses
    }

    fun getApiResponsesRAWJSON() : ApiResponses {

        val response200 = ApiResponse()
        response200.description = "OK"
        val exampleList = mutableListOf<Example>()
        exampleList.add(Example().value("{}"))
        response200.content = Content()
            .addMediaType("application/json", MediaType().schema(StringSchema()._default("{}")))
        val apiResponses = ApiResponses().addApiResponse("200",response200)
        return apiResponses
    }
    fun getPathItem(tag :String, name : String,fullName : String, param : String, example : String, description : String ) : PathItem {
        val pathItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(tag)
                    .summary("search-type")
                    .description(description)
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name(param)
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("The uri that identifies the "+fullName)
                        .schema(StringSchema().format("token"))
                        .example(example)))
        return pathItem
    }

    fun getApiResponsesBinary() : ApiResponses {

        val response200 = ApiResponse()
        response200.description = "OK"
        val exampleList = mutableListOf<Example>()
        exampleList.add(Example().value("{}"))
        response200.content = Content()
            .addMediaType("*/*", MediaType().schema(StringSchema()._default("{}")))
            .addMediaType("application/fhir+json", MediaType().schema(StringSchema()._default("{}")))
            .addMediaType("application/fhir+xml", MediaType().schema(StringSchema()._default("<>")))
        val apiResponses = ApiResponses().addApiResponse("200",response200)
        return apiResponses
    }
}
