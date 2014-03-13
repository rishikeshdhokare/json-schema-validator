package uk.co.o2.json.schema;

import javax.json.JsonArray;
import javax.json.JsonValue;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static uk.co.o2.json.schema.ErrorMessage.singleError;

class ArraySchema implements JsonSchema {

    private JsonSchema items;
    private int maxItems;
    private int minItems;

    ArraySchema() {
        SimpleTypeSchema items = new SimpleTypeSchema();
        items.setType( SimpleType.ANY);
        setItems(items);
    }

    @Override
    public List<ErrorMessage> validate(JsonValue jsonDocument) {
        if (!jsonDocument.getValueType().equals(JsonValue.ValueType.ARRAY)) {
            return singleError("", "Invalid type: must be an array");
        }
        JsonArray jsonArray = (JsonArray) jsonDocument;
        if ((maxItems != 0) && (jsonArray.size() > maxItems)) {
            return singleError("", "Current array size of %d is greater than allowed maximum array size of %d", jsonArray.size(), maxItems);
        }

        if ((minItems != 0) && (jsonArray.size() < minItems)) {
            return singleError("", "Current array size of %d is less than allowed minimum array size of %d", jsonArray.size(), minItems);
        }

        int index = 0;
        List<ErrorMessage> results = new ArrayList<>();
        for(JsonValue item : jsonArray) {
            results.addAll(generateNestedErrorMessages(index++, items.validate(item)));
        }
        return results;
    }

    private List<ErrorMessage> generateNestedErrorMessages(int index, List<ErrorMessage> errorMessages) {
        String pathPrefix = "[" + index + "]";
        return errorMessages.stream()
            .map((error) -> new ErrorMessage(pathPrefix, error))
            .collect(toList());
    }

    void setItems(JsonSchema items) {
        this.items = items;
    }

    JsonSchema getItems() {
        return items;
    }

    void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    int getMaxItems() {
        return maxItems;
    }

    void setMinItems(int minItems) {
        this.minItems = minItems;
    }

    int getMinItems() {
        return minItems;
    }
}
