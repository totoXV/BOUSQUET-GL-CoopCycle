package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Produit;
import com.mycompany.myapp.repository.ProduitRepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Produit}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ProduitResource {

    private final Logger log = LoggerFactory.getLogger(ProduitResource.class);

    private static final String ENTITY_NAME = "produit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProduitRepository produitRepository;

    public ProduitResource(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    /**
     * {@code POST  /produits} : Create a new produit.
     *
     * @param produit the produit to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new produit, or with status {@code 400 (Bad Request)} if the produit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/produits")
    public Mono<ResponseEntity<Produit>> createProduit(@RequestBody Produit produit) throws URISyntaxException {
        log.debug("REST request to save Produit : {}", produit);
        if (produit.getId() != null) {
            throw new BadRequestAlertException("A new produit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return produitRepository
            .save(produit)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/produits/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /produits/:id} : Updates an existing produit.
     *
     * @param id the id of the produit to save.
     * @param produit the produit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated produit,
     * or with status {@code 400 (Bad Request)} if the produit is not valid,
     * or with status {@code 500 (Internal Server Error)} if the produit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/produits/{id}")
    public Mono<ResponseEntity<Produit>> updateProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Produit produit
    ) throws URISyntaxException {
        log.debug("REST request to update Produit : {}, {}", id, produit);
        if (produit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, produit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return produitRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                // no save call needed as we have no fields that can be updated
                return produitRepository
                    .findById(produit.getId())
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
     * {@code PATCH  /produits/:id} : Partial updates given fields of an existing produit, field will ignore if it is null
     *
     * @param id the id of the produit to save.
     * @param produit the produit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated produit,
     * or with status {@code 400 (Bad Request)} if the produit is not valid,
     * or with status {@code 404 (Not Found)} if the produit is not found,
     * or with status {@code 500 (Internal Server Error)} if the produit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/produits/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Produit>> partialUpdateProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Produit produit
    ) throws URISyntaxException {
        log.debug("REST request to partial update Produit partially : {}, {}", id, produit);
        if (produit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, produit.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return produitRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Produit> result = produitRepository
                    .findById(produit.getId())
                    .map(existingProduit -> {
                        return existingProduit;
                    }); // .flatMap(produitRepository::save)

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
     * {@code GET  /produits} : get all the produits.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of produits in body.
     */
    @GetMapping("/produits")
    public Mono<List<Produit>> getAllProduits() {
        log.debug("REST request to get all Produits");
        return produitRepository.findAll().collectList();
    }

    /**
     * {@code GET  /produits} : get all the produits as a stream.
     * @return the {@link Flux} of produits.
     */
    @GetMapping(value = "/produits", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Produit> getAllProduitsAsStream() {
        log.debug("REST request to get all Produits as a stream");
        return produitRepository.findAll();
    }

    /**
     * {@code GET  /produits/:id} : get the "id" produit.
     *
     * @param id the id of the produit to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the produit, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/produits/{id}")
    public Mono<ResponseEntity<Produit>> getProduit(@PathVariable Long id) {
        log.debug("REST request to get Produit : {}", id);
        Mono<Produit> produit = produitRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(produit);
    }

    /**
     * {@code DELETE  /produits/:id} : delete the "id" produit.
     *
     * @param id the id of the produit to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/produits/{id}")
    public Mono<ResponseEntity<Void>> deleteProduit(@PathVariable Long id) {
        log.debug("REST request to delete Produit : {}", id);
        return produitRepository
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
