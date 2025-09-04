package com.backend.mock;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ProductConverter {
    public static void main(String[] args) throws IOException {
        // Đường dẫn file input và output
        String inputPath = "./data/beauti-care-raw.json";
        String outputPath = "./data/beauti-care.json";

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Đọc file input (file này là 1 object lớn, có trường "products" là array)
        JsonNode root = mapper.readTree(new File(inputPath));
        JsonNode productsNode = root.path("products"); // Nếu là list, sửa lại: root.get(0).path("products")
        if (productsNode.isMissingNode()) {
            // Có thể trường hợp file là một list chứa 1 object, thử lấy node đầu tiên
            productsNode = root.get(0).path("products");
        }

        List<ObjectNode> resultList = new ArrayList<>();

        for (JsonNode product : productsNode) {
            ObjectNode obj = mapper.createObjectNode();

            obj.put("id", product.path("_id").asText());
            obj.put("name", product.path("title").path("en").asText());
            obj.put("imageUrl", product.path("image").get(0).asText(""));
            obj.put("description", product.path("description").path("en").asText());
            obj.put("salePrice", product.path("prices").path("price").asDouble());
            obj.put("compareAtPrice", product.path("prices").path("originalPrice").asDouble());
            obj.put("stock", product.path("stock").asInt());

            // tags là array, nhưng thường lại là 1 string JSON array
            List<String> tags = new ArrayList<>();
            JsonNode tagArr = product.path("tag");
            if (tagArr.isArray()) {
                for (JsonNode tag : tagArr) {
                    tags.add(tag.asText());
                }
            }
            obj.putPOJO("tags", tags);

            // category object
            JsonNode catNode = product.path("category");
            ObjectNode catObj = mapper.createObjectNode();
            catObj.put("id", catNode.path("_id").asText());
            catObj.put("name", catNode.path("name").path("en").asText());
            obj.set("category", catObj);

            resultList.add(obj);
        }

        // Ghi ra file mới (pretty print)
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputPath), resultList);

        System.out.println("Đã convert thành công! Xem file " + outputPath);
    }
}
