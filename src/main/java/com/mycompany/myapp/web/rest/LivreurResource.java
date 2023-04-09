package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Livreur;
import com.mycompany.myapp.repository.LivreurRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Livreur}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LivreurResource {

    private final Logger log = LoggerFactory.getLogger(LivreurResource.class);

    private static final String ENTITY_NAME = "livreur";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LivreurRepository livreurRepository;

    public LivreurResource(LivreurRepository livreurRepository) {
        this.livreurRepository = livreurRepository;
    }

    /**
     * {@code POST  /livreurs} : Create a new livreur.
     *
     * @param livreur the livreur to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new livreur, or with status {@code 400 (Bad Request)} if the livreur has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/livreurs")
    public Mono<ResponseEntity<Livreur>> createLivreur(@RequestBody Livreur livreur) throws URISyntaxException {
        log.debug("REST request to save Livreur : {}", livreur);
        if (livreur.getId() != null) {
            throw new BadRequestAlertException("A new livreur cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return livreurRepository
            .save(livreur)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/livreurs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /livreurs/:id} : Updates an existing livreur.
     *
     * @param id the id of the livreur to save.
     * @param livreur the livreur to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated livreur,
     * or with status {@code 400 (Bad Request)} if the livreur is not valid,
     * or with status {@code 500 (Internal Server Error)} if the livreur couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/livreurs/{id}")
    public Mono<ResponseEntity<Livreur>> updateLivreur(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Livreur livreur
    ) throws URISyntaxException {
        log.debug("REST request to update Livreur : {}, {}", id, livreur);
        if (livreur.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, livreur.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return livreurRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                // no save call needed as we have no fields that can be updated
                return livreurRepository
                    .findById(livreur.getId())
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /livreurs/:id} : Partial updates given fields of an existing livreur, field will ignore if it is null
     *
     * @param id the id of the livreur to save.
     * @param livreur the livreur to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated livreur,
     * or with status {@code 400 (Bad Request)} if the livreur is not valid,
     * or with status {@code 404 (Not Found)} if the livreur is not found,
     * or with status {@code 500 (Internal Server Error)} if the livreur couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/livreurs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Livreur>> partialUpdateLivreur(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Livreur livreur
    ) throws URISyntaxException {
        log.debug("REST request to partial update Livreur partially : {}, {}", id, livreur);
        if (livreur.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, livreur.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return livreurRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Livreur> result = livreurRepository
                    .findById(livreur.getId())
                    .map(existingLivreur -> {
                        return existingLivreur;
                    }); // .flatMap(livreurRepository::save)

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /livreurs} : get all the livreurs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of livreurs in body.
     */
    @GetMapping("/livreurs")
    public Mono<List<Livreur>> getAllLivreurs() {
        log.debug("REST request to get all Livreurs");
        return livreurRepository.findAll().collectList();
    }

    /**
     * {@code GET  /livreurs} : get all the livreurs as a stream.
     * @return the {@link Flux} of livreurs.
     */
    @GetMapping(value = "/livreurs", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Livreur> getAllLivreursAsStream() {
        log.debug("REST request to get all Livreurs as a stream");
        return livreurRepository.findAll();
    }

    /**
     * {@code GET  /livreurs/:id} : get the "id" livreur.
     *
     * @param id the id of the livreur to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the livreur, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/livreurs/{id}")
    public Mono<ResponseEntity<Livreur>> getLivreur(@PathVariable Long id) {
        log.debug("REST request to get Livreur : {}", id);
        Mono<Livreur> livreur = livreurRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(livreur);
    }

    /**
     * {@code DELETE  /livreurs/:id} : delete the "id" livreur.
     *
     * @param id the id of the livreur to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/livreurs/{id}")
    public Mono<ResponseEntity<Void>> deleteLivreur(@PathVariable Long id) {
        log.debug("REST request to delete Livreur : {}", id);
        return livreurRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
