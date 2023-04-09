package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Membre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Membre entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MembreRepository extends ReactiveCrudRepository<Membre, Long>, MembreRepositoryInternal {
    @Override
    <S extends Membre> Mono<S> save(S entity);

    @Override
    Flux<Membre> findAll();

    @Override
    Mono<Membre> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MembreRepositoryInternal {
    <S extends Membre> Mono<S> save(S entity);

    Flux<Membre> findAllBy(Pageable pageable);

    Flux<Membre> findAll();

    Mono<Membre> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Membre> findAllBy(Pageable pageable, Criteria criteria);

}
