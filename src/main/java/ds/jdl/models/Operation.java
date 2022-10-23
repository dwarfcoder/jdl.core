package ds.jdl.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Operation {

    Equals("="),
    GreaterThan(">"),
    LessThan("<"),
    GreaterOrEqual(">="),
    LessOrEqual("<="),
    Like("%"),
    None("none");

    private String value;

    Operation(String value) {
        this.value = value;
    }

    public static Operation forType(String type) {
        for(Operation el : values()) {
            if (el.value.equals(type)) {
                return el;
            }
        }

        return None;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() { return this.value; }
}
