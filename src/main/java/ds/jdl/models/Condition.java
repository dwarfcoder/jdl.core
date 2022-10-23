package ds.jdl.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Condition extends FilterBase {
    private String fieldName;
    private String value;
    private Operation operation;
}
