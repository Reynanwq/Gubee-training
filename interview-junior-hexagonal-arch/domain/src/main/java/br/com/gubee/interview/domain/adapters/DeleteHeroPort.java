package br.com.gubee.interview.domain.adapters;

public interface DeleteHeroPort {
    void delete(String id);

    void deleteAll();

    void deleteById(String id);
}
