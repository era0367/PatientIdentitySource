package knu.myhealthhub.patientidentitysource.transactions;

import static knu.myhealthhub.common.JsonUtility.*;
import static knu.myhealthhub.enums.ERROR_CODE.FHIR_SERVER_ERROR;
import static knu.myhealthhub.patientidentitysource.IdentityManager.getResourceType;
import static knu.myhealthhub.patientidentitysource.IdentityManager.getTotalCount;
import static knu.myhealthhub.settings.Configuration.FHIR_SERVER_ENDPOINT_LOCALHOST;
import static knu.myhealthhub.settings.KeyString.*;
import static knu.myhealthhub.settings.errors.ErrorUtility.setErrorMessage;
import static knu.myhealthhub.transactions.RestSender.createRest;
import static knu.myhealthhub.transactions.RestSender.getFhirHeader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpMethod;

public class ITI08_Retrieve {
    public static String retrieveIdentity(String identifier, String type) {
        String resourceType = getResourceType(type);
        String totalString = getTotalCount(identifier, resourceType);
        if (totalString.equalsIgnoreCase("1")) {
            return totalString;
        }
        String requestIdentityResult = requestIdentity(resourceType, identifier);
        if (null == requestIdentityResult) {
            return setErrorMessage(FHIR_SERVER_ERROR, null);
        }
        return parseRequestIdentityResult(requestIdentityResult);
    }
    public static String requestIdentity(String resourceType, String identifier) {
        String url = String.format("%s%s?%s.%s=%s", FHIR_SERVER_ENDPOINT_LOCALHOST, resourceType, KEY_FOR_IDENTIFIER, KEY_FOR_VALUE, identifier);
        return createRest(url, HttpMethod.GET, getFhirHeader(), new JSONObject());
    }
    private static String parseRequestIdentityResult(String requestIdentityResult) {
        JSONObject requestIdentityResultJson = toJsonObject(requestIdentityResult);
        if (null == requestIdentityResultJson) {
            String reason = String.format("Fail to parse String to JSON - %s", requestIdentityResult);
            setErrorMessage(FHIR_SERVER_ERROR, reason);
        }
        JSONArray entryList = getJsonArray(requestIdentityResultJson, KEY_FOR_RESOURCE_ENTRY);
        if (null == entryList) {
            String reason = String.format("Fail to find key[%s] from %s", KEY_FOR_RESOURCE_ENTRY, requestIdentityResultJson.toJSONString());
            return setErrorMessage(FHIR_SERVER_ERROR, reason);
        }
        JSONObject entry = getJsonObjectFromArray(entryList, 0);
        JSONObject resource = getJsonObject(entry, KEY_FOR_RESOURCE);
        if (null == resource) {
            String reason = String.format("Fail to find key[%s] from %s", KEY_FOR_RESOURCE, requestIdentityResultJson.toJSONString());
            return setErrorMessage(FHIR_SERVER_ERROR, reason);
        }
        return resource.toJSONString();
    }
}
