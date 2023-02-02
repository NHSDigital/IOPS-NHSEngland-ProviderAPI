package uk.nhs.england.provider.qedm.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StatusController {
    @GetMapping("_status")
    fun validate(): String {
        return "Validator is alive"
    }
}
