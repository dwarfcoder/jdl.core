package ds.jdl.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private String code;

//    private Map<String, Object> data = new LinkedHashMap<>();
    private Object data;
}
