package knu.myhealthhub.patientidentitysource.controller;

import static knu.myhealthhub.patientidentitysource.transactions.ITI08_Register.registerIdentity;
import static knu.myhealthhub.patientidentitysource.transactions.ITI08_Retrieve.retrieveIdentity;
import static knu.myhealthhub.patientidentitysource.validator.ParamValidator.validateParameter;
import static knu.myhealthhub.settings.Configuration.IDENTITY;
import static knu.myhealthhub.settings.Configuration.TRUE;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class PatientIdentitySourceController {
    @GetMapping(IDENTITY)
    public String xdsPatientIdentityFeedRetrieve(@RequestParam String identifier, @RequestParam String type) {
        return retrieveIdentity(identifier, type);
    }
    @PostMapping(IDENTITY)
    public String xdsPatientIdentityFeedCreated(@RequestBody @Validated String type, @RequestBody @Validated String body) {
        String validateParamResult = validateParameter(body, type);
        if (!validateParamResult.equals(TRUE)) {
            return validateParamResult;
        }
        return registerIdentity(type, body);
    }
    //    @PutMapping(IDENTITY)
    //    public String xdsPatientIdentityFeedUpdateDataSubject(@RequestBody @Validated String body) {
    //        // @Todo
    //        return "";
    //    }
}
