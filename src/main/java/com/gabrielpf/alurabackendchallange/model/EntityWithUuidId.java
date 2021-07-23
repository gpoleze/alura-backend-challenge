package com.gabrielpf.alurabackendchallange.model;

import java.util.UUID;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;

@MappedSuperclass
public class EntityWithUuidId {
    @Id
    @Type(type = "pg-uuid")
    private UUID id;

    protected EntityWithUuidId() {
        this.id = UUID.randomUUID();
    }

    protected EntityWithUuidId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return UUID.fromString(id.toString());
    }

    public EntityWithUuidId setId(UUID id) {
        this.id = id;
        return null;
    }
}
