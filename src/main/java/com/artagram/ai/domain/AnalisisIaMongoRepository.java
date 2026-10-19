package com.artagram.ai.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalisisIaMongoRepository extends MongoRepository<AnalisisIaDocument, UUID> {
    Optional<AnalisisIaDocument> findTopByPublicacionIdOrderByCreadoEnDesc(UUID publicacionId);
}
