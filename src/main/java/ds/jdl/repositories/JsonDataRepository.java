package ds.jdl.repositories;

import ds.jdl.entities.JsonEntityBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

//@Repository
public interface JsonDataRepository<TEntity extends JsonEntityBase, TId extends Serializable>
        extends JpaRepository<TEntity, TId>, JpaSpecificationExecutor<TEntity> {
}
