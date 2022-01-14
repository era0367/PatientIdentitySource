package knu.myhealthhub.patientidentitysource;

import static knu.myhealthhub.common.JsonUtility.getStringFromObject;
import static knu.myhealthhub.common.JsonUtility.toJsonObject;
import static knu.myhealthhub.enums.ERROR_CODE.XDS_DUPLICATE_UNIQUE_ID_IN_REGISTRY;
import static knu.myhealthhub.enums.ERROR_CODE.XDS_REPOSITORY_ERROR;
import static knu.myhealthhub.patientidentitysource.transactions.ITI08_Retrieve.requestIdentity;
import static knu.myhealthhub.settings.Configuration.*;
import static knu.myhealthhub.settings.KeyString.KEY_FOR_STATUS;
import static knu.myhealthhub.settings.KeyString.KEY_FOR_TOTAL;
import static knu.myhealthhub.settings.errors.ErrorUtility.setErrorMessage;

import knu.myhealthhub.enums.USER_TYPE;
import org.json.simple.JSONObject;

public class IdentityManager {
    public static String getProfile(String type) {
        USER_TYPE userType = USER_TYPE.valueOf(type);
        switch (userType) {
            case DATA_SUBJECT:
                return FHIR_REGISTRY_ENDPOINT + DATA_SUBJECT;
            case DATA_CONSUMER:
                return FHIR_REGISTRY_ENDPOINT + DATA_CONSUMER;
            case MEMBER:
                return FHIR_REGISTRY_ENDPOINT + PERSON;
            case INSTITUTION:
                return FHIR_REGISTRY_ENDPOINT + INSTITUTION;
            default:
                return null;
        }
    }
    public static String getResourceType(String type) {
        USER_TYPE userType = USER_TYPE.valueOf(type);
        switch (userType) {
            case DATA_SUBJECT:
                return DATA_SUBJECT;
            case DATA_CONSUMER:
                return DATA_CONSUMER;
            case MEMBER:
                return PERSON;
            case INSTITUTION:
                return INSTITUTION;
            default:
                return null;
        }
    }
    public static String getTotalCount(String identifier, String resourceType) {
        String requestQueryResult = requestIdentity(resourceType, identifier);
        JSONObject requestQueryResultJson = toJsonObject(requestQueryResult);
        String totalString = getStringFromObject(requestQueryResultJson, KEY_FOR_TOTAL);
        if (null == totalString) {
            String reason = String.format("Fail to find key[total] from %s", requestQueryResultJson.toJSONString());
            return setErrorMessage(XDS_REPOSITORY_ERROR, reason);
        }
        return totalString;
    }
    public static String checkDuplicate(String id, String base) {
        String totalString = getTotalCount(id, base);
        int total = Integer.parseInt(totalString);
        if (0 < total) {
            return setErrorMessage(XDS_DUPLICATE_UNIQUE_ID_IN_REGISTRY, null);
        }
        return TRUE;
    }
    public static String setResponse(String status) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_FOR_STATUS, status);
        return jsonObject.toJSONString();
    }
}
