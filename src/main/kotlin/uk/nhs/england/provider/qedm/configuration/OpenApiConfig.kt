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

    var HEALTH_ADMIN = "Health Admin"
    var DOCUMENTS = "Documents"

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
                                "## FHIR Implementation Guides"
                                + "\n\n [UK Core Implementation Guide (0.5.1)](https://simplifier.net/guide/ukcoreimplementationguide0.5.0-stu1/home?version=current)"
                                + "\n\n [NHS England Implementation Guide (2.6.0)](https://simplifier.net/guide/nhsdigital?version=2.6.0)"

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
                        .example("https://fhir.nhs.uk/Id/nhs-number|3478526985")
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
                        .example("https://fhir.nhs.uk/Id/nhs-number|3478526985")
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
                        .example("https://fhir.nhs.uk/Id/nhs-number|3478526985")
                    )
            )
        oas.path("/FHIR/R4/Task",taskItem)

        // ServiceRequest
        var serviceRequestItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(HEALTH_ADMIN)
                    .summary("Search ServiceRequest (Local/Regional: BARS?, National: eRS(for Wayfinder))")
                    .description("BARS eRS(Wayfinder)")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient:identifier")
                        .`in`("query")
                        .required(true)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Who/what is the subject of the ServiceRequest `https://fhir.nhs.uk/Id/nhs-number|{nhsNumber}`")
                        .schema(StringSchema())
                        .example("https://fhir.nhs.uk/Id/nhs-number|3478526985")
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
