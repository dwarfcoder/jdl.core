package ds.jdl.models;

import lombok.Data;

import java.util.List;

@Data
public abstract class FilterBase {

    protected List<Condition> and;
    protected List<Condition> or;
}
