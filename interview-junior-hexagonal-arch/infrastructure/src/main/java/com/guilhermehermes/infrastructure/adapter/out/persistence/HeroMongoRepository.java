package com.guilhermehermes.infrastructure.adapter.out.persistence;



import br.com.gubee.interview.domain.model.Hero;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

interface HeroMongoRepository extends MongoRepository<HeroEntityPersistence, ObjectId> {

    List<HeroEntityPersistence> findByNameContainingIgnoreCase(String name);

    List<HeroEntityPersistence> findAllByIdIn(List<ObjectId> ids);

    HeroEntityPersistence findById(String id);

    void deleteById(String id);
}
