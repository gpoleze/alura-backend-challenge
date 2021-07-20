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

    public EntityWithUuidId() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public EntityWithUuidId setId(UUID id) {
        this.id = id;
        return null;
    }
}
