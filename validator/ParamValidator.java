package knu.myhealthhub.patientidentitysource.validator;

import static knu.myhealthhub.common.JsonUtility.*;
import static knu.myhealthhub.enums.ERROR_CODE.XDS_MISSING_DOCUMENT;
import static knu.myhealthhub.patientidentitysource.IdentityManager.checkDuplicate;
import static knu.myhealthhub.patientidentitysource.IdentityManager.getProfile;
import static knu.myhealthhub.patientidentitysource.validator.FhirValidator.isValidFhirData;
import static knu.myhealthhub.settings.Configuration.TRUE;
import static knu.myhealthhub.settings.KeyString.*;
import static knu.myhealthhub.settings.errors.ErrorUtility.setErrorMessage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ParamValidator {
    public static String validateParameter(String body, String type) {
        JSONObject fhirDataJsonObject = toJsonObject(body);
        if (null == fhirDataJsonObject) {
            String reason = String.format("Fail to parse string to JSON - %s", body);
            return setErrorMessage(XDS_MISSING_DOCUMENT, reason);
        }
        String resourceType = getStringFromObject(fhirDataJsonObject, KEY_FOR_RESOURCE_TYPE);
        if (null == resourceType) {
            String reason = String.format("Fail to find key[%s] from %s", KEY_FOR_RESOURCE_TYPE, fhirDataJsonObject);
            return setErrorMessage(XDS_MISSING_DOCUMENT, reason);
        }
        String identifier = getIdentifier(fhirDataJsonObject);
        // @ Todo 예외처리
        String checkDuplicateResult = checkDuplicate(identifier, resourceType);
        if (!checkDuplicateResult.equals(TRUE)) {
            return checkDuplicateResult;
        }
        String profile = getProfile(type);
        if (null == profile) {
            String reason = String.format("Fail to parse string to USER_TYPE - %s", type);
            return setErrorMessage(XDS_MISSING_DOCUMENT, reason);
        }
        return isValidFhirData(profile, fhirDataJsonObject);
    }
    public static String getIdentifier(JSONObject jsonObject) {
        JSONArray identifierList = getJsonArray(jsonObject, KEY_FOR_IDENTIFIER);
        if (null == identifierList) {
            String reason = String.format("Fail to find key[%s] from %s", KEY_FOR_IDENTIFIER, jsonObject);
            return setErrorMessage(XDS_MISSING_DOCUMENT, reason);
        }
        JSONObject identifier = getJsonObjectFromArray(identifierList, 0);
        String value = getStringFromObject(identifier, KEY_FOR_VALUE);
        if (null == value) {
            String reason = String.format("Fail to find key[%s] from %s", KEY_FOR_VALUE, identifier.toJSONString());
            return setErrorMessage(XDS_MISSING_DOCUMENT, reason);
        }
        return value;
    }
}