package pl.put.poznan.transformer.logic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;

/**
 * Class containing all the logic and methods needed to remove properties with given keys from the JSON file.
 *
 * @author Julia Tadej
 * @version 1.0
 */

public class JSONReduced extends Decorator {

    /**
     * Constructor, calls constructor from component passed in constructor's argument.
     * @param component Component from which constructor will be called inside this constructor.
     */
    public JSONReduced(Component component) {
        super(component);
    }


    private void reduceNode(JsonNode currentNode, String[] properties){
        //System.out.println(currentNode.getNodeType());
        if (currentNode instanceof ObjectNode) {
            ((ObjectNode) currentNode).remove(Arrays.asList(properties));
        }
        if(currentNode.isContainerNode()){
            for (JsonNode child : currentNode) {
                reduceNode(child, properties);
            }
        }
    }

    /**
     * Method which converts JSON into tree-like structure so it can be traversed over like a graph.
     * Then it runs reduceNode method which removes given properties from JSON and converts result back into the String.
     *
     * @param jsonString String containing the JSON.
     * @param properties list of keys - properties with them will be removed.
     * @return String with the JSON without elements which were supposed to be removed.
     * @throws JSONException Exception thrown if String doesn't contain correct JSON.
     */
    public String reduce(String jsonString, String[] properties) throws JSONException{

        if (properties == null) properties = new String[]{};
        ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            JsonNode jsonNode = mapper.readTree(jsonString);
            reduceNode(jsonNode, properties);
            jsonString = mapper.writeValueAsString(jsonNode);
        } catch (IOException e){
            e.printStackTrace();
        }

        JSONArray array = JSONTransformer.transform(jsonString);
        result = array.toString(4);
        return result;
    }

    /**
     * Stub method derived from Component interface, not needed in this class, so it just returns null.
     */
    @Override
    public String operation(String jsonString) throws JSONException {
        return null;
    }

    /**
     * Method derived from Component interface, wrapper for reduce method.
     *
     * @param jsonString  String containing the JSON file content.
     * @param input String containing keys of properties which will be removed, delimited with commas.
     * @throws JSONException Exception thrown if String doesn't contain correct JSON.
     * @return String with the JSON without elements with keys listed in 'input' parameter.
     */
    @Override
    public String operation(String jsonString, String input) throws JSONException {
        return reduce(jsonString, input.split(","));
    }
}
