package changer;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class Changer {

    public static final String SITE_CATEGORY_PATH = "@SiteCategoryPath";
    public static final String LABOR_STANDARD_NAME = "@LaborStandardName";
    public static final String INITIAL = "0393 WARWICK";
    public static final String REPLACEMENT = "001 Test Club";
    //Constants for json keys
    public static final String JSON_ITEMS_RETRIEVE_RESPONSES = "itemsRetrieveResponses";
    public static final String JSON_ITEM_DATA_INFO = "itemDataInfo";
    public static final String JSON_RESPONSE_OBJECT_NODE = "responseObjectNode";
    public static final String JSON_LABOR_STANDARD_GROUP = "LaborStandardGroup";
    public static final String JSON_LABOR_STANDARDS = "LaborStandards";
    public static final String JSON_LABOR_STANDARD = "LaborStandard";
    //File names
    public static final String INPUT_FILE_NAME = "response.json";
    public static final String OUTPUT_FILE_NAME = "output";
    public static final String EMPTY_STRING = "";

    public static void main(String[] args) throws IOException {
        //Here is file name with json in root of project
        Path path = Paths.get(INPUT_FILE_NAME);
        if (Files.notExists(path))
            throw new IllegalArgumentException("No such file is present");
        String inputJson = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        JSONObject object = new JSONObject(inputJson);
        Map<String, Object> jsonMap = object.toMap();
        replaceNullValuesInMapWithString(jsonMap);
        HashMap itemsRetrieveResponse = (HashMap) ((ArrayList) jsonMap.get(JSON_ITEMS_RETRIEVE_RESPONSES)).get(0);
        replaceNullValuesInMapWithString(itemsRetrieveResponse);
        HashMap itemDataInfo = (HashMap) itemsRetrieveResponse.get(JSON_ITEM_DATA_INFO);
        replaceNullValuesInMapWithString(itemDataInfo);
        HashMap responseObjectNode = (HashMap) itemsRetrieveResponse.get(JSON_RESPONSE_OBJECT_NODE);
        replaceNullValuesInMapWithString(responseObjectNode);
        HashMap laborStandardGroup = (HashMap) responseObjectNode.get(JSON_LABOR_STANDARD_GROUP);
        replaceNullValuesInMapWithString(laborStandardGroup);
        HashMap laborStandards = (HashMap) laborStandardGroup.get(JSON_LABOR_STANDARDS);
        replaceNullValuesInMapWithString(laborStandards);
        ArrayList<HashMap<String, Object>> laborStandardList = (ArrayList) laborStandards.get(JSON_LABOR_STANDARD);

        Iterator<HashMap<String, Object>> iterator = laborStandardList.iterator();
        while (iterator.hasNext()) {
            HashMap<String, Object> ls = iterator.next();
            String sitePath = (String) ls.get(SITE_CATEGORY_PATH);
            if (sitePath.contains(INITIAL)) {
                String oldPathValue = (String) ls.get(SITE_CATEGORY_PATH);
                String newPathValue = oldPathValue.replace(INITIAL, REPLACEMENT);
                ls.put(SITE_CATEGORY_PATH, newPathValue);

                String oldNameValue = (String) ls.get(LABOR_STANDARD_NAME);
                String newNameValue = oldNameValue.replace(INITIAL, REPLACEMENT);
                ls.put(LABOR_STANDARD_NAME, newNameValue);
            } else {
                iterator.remove();
            }
            replaceNullValuesInMapWithString(ls);
        }

        String timeStamp = LocalDateTime.now().toString().replace(":", ".");

        String outputString = JSONObject.valueToString(jsonMap);
        BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_NAME + "_" + timeStamp + ".json"));
        writer.write(outputString);
        writer.close();
    }

    public static void replaceNullValuesInMapWithString(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (Objects.isNull(entry.getValue())) {
                entry.setValue(EMPTY_STRING);
            }
        }
    }
}
