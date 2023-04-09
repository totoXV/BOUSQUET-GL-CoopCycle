package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Membre;
import com.mycompany.myapp.repository.MembreRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Membre}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class MembreResource {

    private final Logger log = LoggerFactory.getLogger(MembreResource.class);

    private static final String ENTITY_NAME = "membre";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MembreRepository membreRepository;

    public MembreResource(MembreRepository membreRepository) {
        this.membreRepository = membreRepository;
    }

    /**
     * {@code POST  /membres} : Create a new membre.
     *
     * @param membre the membre to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new membre, or with status {@code 400 (Bad Request)} if the membre has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/membres")
    public Mono<ResponseEntity<Membre>> createMembre(@RequestBody Membre membre) throws URISyntaxException {
        log.debug("REST request to save Membre : {}", membre);
        if (membre.getId() != null) {
            throw new BadRequestAlertException("A new membre cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return membreRepository
            .save(membre)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/membres/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /membres/:id} : Updates an existing membre.
     *
     * @param id the id of the membre to save.
     * @param membre the membre to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated membre,
     * or with status {@code 400 (Bad Request)} if the membre is not valid,
     * or with status {@code 500 (Internal Server Error)} if the membre couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/membres/{id}")
    public Mono<ResponseEntity<Membre>> updateMembre(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Membre membre
    ) throws URISyntaxException {
        log.debug("REST request to update Membre : {}, {}", id, membre);
        if (membre.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, membre.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return membreRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                // no save call needed as we have no fields that can be updated
                return membreRepository
                    .findById(membre.getId())
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
     * {@code PATCH  /membres/:id} : Partial updates given fields of an existing membre, field will ignore if it is null
     *
     * @param id the id of the membre to save.
     * @param membre the membre to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated membre,
     * or with status {@code 400 (Bad Request)} if the membre is not valid,
     * or with status {@code 404 (Not Found)} if the membre is not found,
     * or with status {@code 500 (Internal Server Error)} if the membre couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/membres/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Membre>> partialUpdateMembre(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Membre membre
    ) throws URISyntaxException {
        log.debug("REST request to partial update Membre partially : {}, {}", id, membre);
        if (membre.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, membre.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return membreRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Membre> result = membreRepository
                    .findById(membre.getId())
                    .map(existingMembre -> {
                        return existingMembre;
                    }); // .flatMap(membreRepository::save)

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
     * {@code GET  /membres} : get all the membres.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of membres in body.
     */
    @GetMapping("/membres")
    public Mono<List<Membre>> getAllMembres() {
        log.debug("REST request to get all Membres");
        return membreRepository.findAll().collectList();
    }

    /**
     * {@code GET  /membres} : get all the membres as a stream.
     * @return the {@link Flux} of membres.
     */
    @GetMapping(value = "/membres", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Membre> getAllMembresAsStream() {
        log.debug("REST request to get all Membres as a stream");
        return membreRepository.findAll();
    }

    /**
     * {@code GET  /membres/:id} : get the "id" membre.
     *
     * @param id the id of the membre to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the membre, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/membres/{id}")
    public Mono<ResponseEntity<Membre>> getMembre(@PathVariable Long id) {
        log.debug("REST request to get Membre : {}", id);
        Mono<Membre> membre = membreRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(membre);
    }

    /**
     * {@code DELETE  /membres/:id} : delete the "id" membre.
     *
     * @param id the id of the membre to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/membres/{id}")
    public Mono<ResponseEntity<Void>> deleteMembre(@PathVariable Long id) {
        log.debug("REST request to delete Membre : {}", id);
        return membreRepository
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
