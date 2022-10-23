package ds.jdl.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import ds.jdl.entities.JsonEntityBase;
import ds.jdl.models.JsonDataSpecification;
import ds.jdl.models.OutputObject;
import ds.jdl.models.Request;
import ds.jdl.repositories.JsonDataRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Pair;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class JsonEntityServiceBase<TEntity extends JsonEntityBase,
        TId extends Serializable,
        TOutModel extends Object>
        implements JsonEntityService<TEntity, TOutModel> {
    private static ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private JsonDataRepository<TEntity, TId> itemRepository;

    public JsonEntityServiceBase(JsonDataRepository<TEntity, TId> itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public List<TOutModel> getAll(Request request, Function<TEntity, TOutModel> mappingFunction) {
        {
            JsonDataSpecification<TEntity> spec =
                    new JsonDataSpecification(request);

            List<TEntity> rows = itemRepository.findAll(spec);
            return rows.stream().map(x ->
                    {
                        if (request != null && request.getSelect() != null && !request.getSelect().isEmpty()) {
                            try {
                                Map<String, Object> map = mapper.readValue(x.getData(), Map.class);
                                Map<String, Object> filteredMap = map.entrySet().stream()
                                        .filter(d ->
                                                request.getSelect().contains(d.getKey())
                                        )
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                                try {
                                    x.setData(mapper.writeValueAsString(filteredMap));
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            } catch (JsonMappingException e) {
                                throw new RuntimeException(e);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        return x;
                    }).map(x -> mappingFunction.apply(x))
                    .collect(Collectors.toUnmodifiableList());
        }
    }

    private HashMap<String, String> streamConvert(Properties prop) {
        return prop.entrySet().stream().collect(
                Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        e -> String.valueOf(e.getValue()),
                        (prev, next) -> next, HashMap::new
                ));
    }

    private void fillMap(String path, JsonNode node, Map<String, String> map, List<Integer> suffix) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) map;
            Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
            String pathPrefix = path.isEmpty() ? "" : path + "-";

            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> entry = iter.next();
                fillMap(pathPrefix + entry.getKey(), entry.getValue(), map, suffix);
            }
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) map;

            for (int i = 0; i < arrayNode.size(); i++) {
                suffix.add(i + 1);
                fillMap(path, arrayNode.get(i), map, suffix);

                if (i + 1 <arrayNode.size()){
                    suffix.remove(arrayNode.size() - 1);
                }
            }

        } else if (node.isValueNode()) {
            if (path.contains("-")) {
                for (int i = 0; i < suffix.size(); i++) {
                    path += "-" + suffix.get(i);
                }

                suffix = new ArrayList<>();
            }

            ValueNode valueNode = (ValueNode) map;
            map.put(path, valueNode.asText());
        }
    }
}
