package com.guilhermehermes.infrastructure.adapter.out.persistence;

import br.com.gubee.interview.domain.adapters.DeleteHeroPort;
import br.com.gubee.interview.domain.adapters.LoadHeroPort;
import br.com.gubee.interview.domain.adapters.SaveHeroPort;
import br.com.gubee.interview.domain.model.Hero;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class HeroPersistenceAdapter implements LoadHeroPort, SaveHeroPort, DeleteHeroPort {

    private final HeroMongoRepository heroRepository;

    public HeroPersistenceAdapter(HeroMongoRepository heroRepository) {
        this.heroRepository = heroRepository;
    }
    @Override
    public Hero save(Hero hero) {
        System.out.println("HeroPersistenceAdapter.save");
        System.out.println(hero);
        HeroEntityPersistence heroEntityPersistence = new HeroEntityPersistence(hero);
        return heroRepository.save(heroEntityPersistence).toDomain();
    }


    @Override
    public Optional<Hero> findById(String id) {
        return heroRepository.findById(new ObjectId(id)).map(HeroEntityPersistence::toDomain);

    }

    @Override
    public List<Hero> findByName(String name) {
        return heroRepository.findByNameContainingIgnoreCase(name).stream()
                .map(HeroEntityPersistence::toDomain)
                .toList();
    }

    @Override
    public Hero update(Hero hero) {
        HeroEntityPersistence heroEntityPersistence = new HeroEntityPersistence(hero);
        return heroRepository.save(heroEntityPersistence).toDomain();
    }

    @Override
    public void delete(String id) {
        heroRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        heroRepository.deleteAll();
    }

    @Override
    public void deleteById(String id) {
        heroRepository.deleteById(id);
    }

    @Override
    public List<Hero> findAll() {
        return heroRepository.findAll().stream()
                .map(HeroEntityPersistence::toDomain)
                .toList();
    }
}
