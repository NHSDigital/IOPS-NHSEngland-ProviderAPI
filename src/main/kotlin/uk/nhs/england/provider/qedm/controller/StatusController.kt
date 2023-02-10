package uk.nhs.england.provider.qedm.controller

import io.swagger.annotations.ApiOperation
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StatusController {


    @Operation(summary = "For NHS England API Management hosted resource servers only", tags = ["Security and API Management"])
    @GetMapping("_status")
    fun status(): String {
        return "Validator is alive"
    }

    @Operation(summary = "Used with NHS England API Management OAuth2 Authorisation Server.",description = "See [NHS England - register your public key with us](https://digital.nhs.uk/developer/guides-and-documentation/security-and-authorisation/application-restricted-restful-apis-signed-jwt-authentication#step-3-register-your-public-key-with-us) \n\n Only one instance is required by NHS England APIM and may be hosted separate to resource servers", tags = ["Security and API Management"])
    @GetMapping("jwks", )
    fun jwks(): String {
        return "{\r\n    \"keys\": [\r\n        {\r\n            \"kty\": \"RSA\",\r\n            \"e\": \"AQAB\",\r\n            \"alg\": \"RS512\",\r\n            \"n\": \"zD6MuP_fgwn-YaIqWzJJsyO-B51WxVw1lxfTTDRIF-ZcYPtsFmdULdMkXVHV5-LUAeCviZcaX8KLRPgM2oqZYO4KmMRWZac5kLcs2oR7vpmWcReXnr2FhlDvrwGrpsdlV2fRTaZPsH711SGug96ybAZGLAUE_o9YJLk50315C9_yW9iACZQT0m8MJDv6C-lTftcH7wWpNq5L2qa-plb13hkxMlCcm3voSV9O87ggaZZPkEvBM9jTcIQww4Dit9j_jMKNWWYs24zvyA_s8iinqPJx15f_vpGdmFZmPIS7fwBGwSMlXKDRw722jAdbUOJB7gchcUYULZ6jeHloqcjmJlswj6ZQmiRYiBELcz5smTAz5-lppCl4cMmKomDyTzRtK_MDLPRegZMNF9uV9TgFFoExD0TNehzaeQxBMkeFCq3GgZD7GQdKYdzysjflKl6X1yVhjIlOkbIjUcRAq1s1yrrRxoRJ_zjU1JeCyeepjFJkg7oh1Z3sgd8Yg4iyRQbEx--u78X4y6SpHzsKnuj-rj9YcjHsGCt4Nqdv_0v-cczk7YXT7g86T8hiJJvehLDu4aUu0jWeFKAqktkG8XR-61EwiJefkP_GQaRkqdwpJqhubWmsu-AulihOI5q2sHAYmICYW7TpTU-WnrI-qJyaOAPlFxm7XWiXUnywyKFRJFk\",\r\n            \"use\": \"sig\",\r\n            \"kid\": \"test\"\r\n        }\r\n    ]\r\n}"
    }
}
