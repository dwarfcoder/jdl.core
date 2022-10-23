package ds.jdl.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;


@MappedSuperclass
public abstract class JsonEntityBase {
    public abstract String getData();
    public abstract void setData(String data);
}
