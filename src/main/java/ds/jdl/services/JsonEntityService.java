package ds.jdl.services;

import ds.jdl.entities.JsonEntityBase;
import ds.jdl.models.OutputObject;
import ds.jdl.models.Request;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface JsonEntityService<TEntity extends JsonEntityBase, TOutModel extends Object> {
    List<TOutModel> getAll(Request request, Function<TEntity, TOutModel> mappingFunction);
}
