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
import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration



@Configuration
open class OpenApiConfig(@Qualifier("R4") val ctx : FhirContext) {
    var MHD = ""
    var ITI67 = "Find Documents"
    var ITI68 = "Retrieve Document"

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
                .name(MHD + " " + ITI67)
                .description("")
        )

        oas.addTagsItem(
            io.swagger.v3.oas.models.tags.Tag()
                .name(MHD + " " + ITI68)
                .description("")
        )




        // Binary

        var binaryItem = PathItem()
            .get(
                Operation()
                    .addTagsItem(MHD + " " + ITI68)
                    .summary("Get raw document (eRS Wayfinder implied by NRLF)")
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
                    .addTagsItem(MHD + " " + ITI67)
                    .summary("Search DocumentReference (Wayfinder eRS NRLF)")
                    .description("Wayfinder eRS NRLF")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient:identifier")
                        .`in`("query")
                        .required(true)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Who/what is the subject of the document")
                        .schema(StringSchema())
                        .example("073eef49-81ee-4c2e-893b-bc2e4efd2630")
                    )
                    .addParametersItem(Parameter()
                        .name("date")
                        .`in`("query")
                        .required(false)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("When this document reference was created")
                        .schema(StringSchema())
                    )


            )
        oas.path("/FHIR/R4/DocumentReference",documentReferenceItem)


        // Task
        var taskItem = PathItem()
            .get(
                Operation()
                    .addTagsItem("Task")
                    .summary("Search Task (BARS Wayfinder)")
                    .description("BARS Wayfinder")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient:identifier")
                        .`in`("query")
                        .required(true)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Who/what is the subject of the Task")
                        .schema(StringSchema())
                        .example("https://fhir.nhs.uk/Id/nhs-number%7C4857773456")
                    )
            )
        oas.path("/FHIR/R4/Task",taskItem)

        // ServiceRequest
        var serviceRequestItem = PathItem()
            .get(
                Operation()
                    .addTagsItem("ServiceRequest")
                    .summary("Search ServiceRequest (BARS eRS(Wayfinder))")
                    .description("BARS eRS(Wayfinder)")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient:identifier")
                        .`in`("query")
                        .required(true)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Who/what is the subject of the ServiceRequest")
                        .schema(StringSchema())
                        .example("https://fhir.nhs.uk/Id/nhs-number%7C4857773456")
                    )
            )
        oas.path("/FHIR/R4/ServiceRequest",serviceRequestItem)


        // Appointment
        var appointmentItem = PathItem()
            .get(
                Operation()
                    .addTagsItem("Appointment")
                    .summary("Search Appointment (BARS Wayfinder)")
                    .description("BARS Wayfinder")
                    .responses(getApiResponses())
                    .addParametersItem(Parameter()
                        .name("patient:identifier")
                        .`in`("query")
                        .required(true)
                        .style(Parameter.StyleEnum.SIMPLE)
                        .description("Who/what is the subject of the Appointment")
                        .schema(StringSchema())
                        .example("https://fhir.nhs.uk/Id/nhs-number%7C4857773456")
                    )
            )
        oas.path("/FHIR/R4/Appointment",appointmentItem)


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
