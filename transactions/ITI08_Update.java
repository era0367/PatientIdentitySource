package knu.myhealthhub.patientidentitysource.transactions;

import static knu.myhealthhub.common.JsonUtility.*;
import static knu.myhealthhub.settings.Configuration.FHIR_SERVER_ENDPOINT_LOCALHOST;
import static knu.myhealthhub.settings.KeyString.*;
import static knu.myhealthhub.transactions.RestSender.createRest;
import static knu.myhealthhub.transactions.RestSender.getHeader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.HttpMethod;

public class ITI08_Update {
    public static String updateIdentity(String body) {
        JSONObject bodyObject = toJsonObject(body);
        /* resource 값 확인 필요 */
        String resourceType = bodyObject.get(KEY_FOR_RESOURCE_TYPE).toString();
        JSONArray identifier = getJsonArray(bodyObject, KEY_FOR_IDENTIFIER);
        String id = getStringFromObject((JSONObject) identifier.get(0), KEY_FOR_VALUE);
        String url = FHIR_SERVER_ENDPOINT_LOCALHOST + resourceType + "?" + KEY_FOR_IDENTIFIER + "." + KEY_FOR_VALUE + "=" + id;
        String response = createRest(url, HttpMethod.POST, getHeader(""), bodyObject);
        return response;
    }
}
