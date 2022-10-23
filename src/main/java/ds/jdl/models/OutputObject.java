package ds.jdl.models;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@Data
public class OutputObject {
    private Map<String, Object> regularFields;

    private Object data;
}
