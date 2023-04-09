package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Commande;
import com.mycompany.myapp.repository.CommandeRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Commande}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CommandeResource {

    private final Logger log = LoggerFactory.getLogger(CommandeResource.class);

    private static final String ENTITY_NAME = "commande";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CommandeRepository commandeRepository;

    public CommandeResource(CommandeRepository commandeRepository) {
        this.commandeRepository = commandeRepository;
    }

    /**
     * {@code POST  /commandes} : Create a new commande.
     *
     * @param commande the commande to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new commande, or with status {@code 400 (Bad Request)} if the commande has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/commandes")
    public Mono<ResponseEntity<Commande>> createCommande(@RequestBody Commande commande) throws URISyntaxException {
        log.debug("REST request to save Commande : {}", commande);
        if (commande.getId() != null) {
            throw new BadRequestAlertException("A new commande cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return commandeRepository
            .save(commande)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/commandes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /commandes/:id} : Updates an existing commande.
     *
     * @param id the id of the commande to save.
     * @param commande the commande to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commande,
     * or with status {@code 400 (Bad Request)} if the commande is not valid,
     * or with status {@code 500 (Internal Server Error)} if the commande couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/commandes/{id}")
    public Mono<ResponseEntity<Commande>> updateCommande(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Commande commande
    ) throws URISyntaxException {
        log.debug("REST request to update Commande : {}, {}", id, commande);
        if (commande.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commande.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return commandeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                // no save call needed as we have no fields that can be updated
                return commandeRepository
                    .findById(commande.getId())
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
     * {@code PATCH  /commandes/:id} : Partial updates given fields of an existing commande, field will ignore if it is null
     *
     * @param id the id of the commande to save.
     * @param commande the commande to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commande,
     * or with status {@code 400 (Bad Request)} if the commande is not valid,
     * or with status {@code 404 (Not Found)} if the commande is not found,
     * or with status {@code 500 (Internal Server Error)} if the commande couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/commandes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Commande>> partialUpdateCommande(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Commande commande
    ) throws URISyntaxException {
        log.debug("REST request to partial update Commande partially : {}, {}", id, commande);
        if (commande.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commande.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return commandeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Commande> result = commandeRepository
                    .findById(commande.getId())
                    .map(existingCommande -> {
                        return existingCommande;
                    }); // .flatMap(commandeRepository::save)

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
     * {@code GET  /commandes} : get all the commandes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of commandes in body.
     */
    @GetMapping("/commandes")
    public Mono<List<Commande>> getAllCommandes() {
        log.debug("REST request to get all Commandes");
        return commandeRepository.findAll().collectList();
    }

    /**
     * {@code GET  /commandes} : get all the commandes as a stream.
     * @return the {@link Flux} of commandes.
     */
    @GetMapping(value = "/commandes", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Commande> getAllCommandesAsStream() {
        log.debug("REST request to get all Commandes as a stream");
        return commandeRepository.findAll();
    }

    /**
     * {@code GET  /commandes/:id} : get the "id" commande.
     *
     * @param id the id of the commande to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the commande, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/commandes/{id}")
    public Mono<ResponseEntity<Commande>> getCommande(@PathVariable Long id) {
        log.debug("REST request to get Commande : {}", id);
        Mono<Commande> commande = commandeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(commande);
    }

    /**
     * {@code DELETE  /commandes/:id} : delete the "id" commande.
     *
     * @param id the id of the commande to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/commandes/{id}")
    public Mono<ResponseEntity<Void>> deleteCommande(@PathVariable Long id) {
        log.debug("REST request to delete Commande : {}", id);
        return commandeRepository
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
