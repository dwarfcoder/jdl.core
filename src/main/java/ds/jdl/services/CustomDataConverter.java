package ds.jdl.services;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@Converter
public final class CustomDataConverter implements AttributeConverter<Map<String, ? extends Object>, PGobject> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public PGobject convertToDatabaseColumn(Map<String, ?> map) {
        PGobject po = new PGobject();
        po.setType("jsonb");

        try {
            po.setValue(map == null ? null : MAPPER.writeValueAsString(map));
        } catch (SQLException | JsonProcessingException ex) {
            throw new IllegalStateException(ex);
        }
        return po;
    }

    @Override
    public Map<String, ? extends Object> convertToEntityAttribute(PGobject pGobject) {
        if (pGobject == null || pGobject.getValue() == null) {
            return null;
        }
        try {
            return MAPPER.readValue(pGobject.getValue(), new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException ex) {
            return null;
        }
    }
}
