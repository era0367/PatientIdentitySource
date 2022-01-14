package knu.myhealthhub.patientidentitysource.transactions;

import static knu.myhealthhub.common.JsonUtility.getStringFromObject;
import static knu.myhealthhub.common.JsonUtility.toJsonObject;
import static knu.myhealthhub.enums.ERROR_CODE.FHIR_SERVER_ERROR;
import static knu.myhealthhub.enums.ERROR_CODE.XDS_REPOSITORY_ERROR;
import static knu.myhealthhub.patientidentitysource.IdentityManager.*;
import static knu.myhealthhub.patientidentitysource.validator.ParamValidator.getIdentifier;
import static knu.myhealthhub.settings.Configuration.*;
import static knu.myhealthhub.settings.KeyString.KEY_FOR_RESOURCE_ID;
import static knu.myhealthhub.settings.errors.ErrorUtility.setErrorMessage;
import static knu.myhealthhub.transactions.RestSender.createRest;
import static knu.myhealthhub.transactions.RestSender.getFhirHeader;

import org.json.simple.JSONObject;
import org.springframework.http.HttpMethod;

public class ITI08_Register {
    public static String registerIdentity(String type, String body) {
        String resourceType = getResourceType(type);
        JSONObject resourceJson = toJsonObject(body);
        if (null == resourceJson) {
            String reason = String.format("Fail to parse String to JSON - %s", body);
            setErrorMessage(FHIR_SERVER_ERROR, reason);
        }
        String identifier = getIdentifier(resourceJson);
        String checkDuplicateResult = checkDuplicate(identifier, resourceType);
        if (!checkDuplicateResult.equals(TRUE)) {
            return checkDuplicateResult;
        }
        String requestRegisterIdentityResult = requestRegisterIdentity(resourceType);
        if (null == requestRegisterIdentityResult) {
            return setErrorMessage(FHIR_SERVER_ERROR, null);
        }
        return parseRequestRegisterIdentityResult(requestRegisterIdentityResult);
    }
    private static String requestRegisterIdentity(String resourceType) {
        String url = FHIR_SERVER_ENDPOINT_LOCALHOST + resourceType;
        return createRest(url, HttpMethod.POST, getFhirHeader(), new JSONObject());
    }
    private static String parseRequestRegisterIdentityResult(String requestRegisterIdentityResult) {
        JSONObject requestRegisterIdentityResultJson = toJsonObject(requestRegisterIdentityResult);
        if (null == requestRegisterIdentityResultJson) {
            String reason = String.format("Fail to parse String to JSON - %s", requestRegisterIdentityResult);
            return setErrorMessage(FHIR_SERVER_ERROR, reason);
        }
        String id = getStringFromObject(requestRegisterIdentityResultJson, KEY_FOR_RESOURCE_ID);
        if (null == id) {
            String reason = String.format("Fail to find key[%s] from %s", KEY_FOR_RESOURCE_ID, requestRegisterIdentityResultJson.toJSONString());
            return setErrorMessage(XDS_REPOSITORY_ERROR, reason);
        }
        return setResponse(SUCCESS);
    }
}
