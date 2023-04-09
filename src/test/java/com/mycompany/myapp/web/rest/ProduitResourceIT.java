package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Produit;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ProduitRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ProduitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProduitResourceIT {

    private static final String ENTITY_API_URL = "/api/produits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Produit produit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produit createEntity(EntityManager em) {
        Produit produit = new Produit();
        return produit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produit createUpdatedEntity(EntityManager em) {
        Produit produit = new Produit();
        return produit;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Produit.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        produit = createEntity(em);
    }

    @Test
    void createProduit() throws Exception {
        int databaseSizeBeforeCreate = produitRepository.findAll().collectList().block().size();
        // Create the Produit
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produit))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeCreate + 1);
        Produit testProduit = produitList.get(produitList.size() - 1);
    }

    @Test
    void createProduitWithExistingId() throws Exception {
        // Create the Produit with an existing ID
        produit.setId(1L);

        int databaseSizeBeforeCreate = produitRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllProduitsAsStream() {
        // Initialize the database
        produitRepository.save(produit).block();

        List<Produit> produitList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Produit.class)
            .getResponseBody()
            .filter(produit::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(produitList).isNotNull();
        assertThat(produitList).hasSize(1);
        Produit testProduit = produitList.get(0);
    }

    @Test
    void getAllProduits() {
        // Initialize the database
        produitRepository.save(produit).block();

        // Get all the produitList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(produit.getId().intValue()));
    }

    @Test
    void getProduit() {
        // Initialize the database
        produitRepository.save(produit).block();

        // Get the produit
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, produit.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(produit.getId().intValue()));
    }

    @Test
    void getNonExistingProduit() {
        // Get the produit
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProduit() throws Exception {
        // Initialize the database
        produitRepository.save(produit).block();

        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();

        // Update the produit
        Produit updatedProduit = produitRepository.findById(produit.getId()).block();

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedProduit.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedProduit))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
        Produit testProduit = produitList.get(produitList.size() - 1);
    }

    @Test
    void putNonExistingProduit() throws Exception {
        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();
        produit.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, produit.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProduit() throws Exception {
        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();
        produit.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProduit() throws Exception {
        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();
        produit.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produit))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProduitWithPatch() throws Exception {
        // Initialize the database
        produitRepository.save(produit).block();

        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();

        // Update the produit using partial update
        Produit partialUpdatedProduit = new Produit();
        partialUpdatedProduit.setId(produit.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProduit.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProduit))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
        Produit testProduit = produitList.get(produitList.size() - 1);
    }

    @Test
    void fullUpdateProduitWithPatch() throws Exception {
        // Initialize the database
        produitRepository.save(produit).block();

        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();

        // Update the produit using partial update
        Produit partialUpdatedProduit = new Produit();
        partialUpdatedProduit.setId(produit.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProduit.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProduit))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
        Produit testProduit = produitList.get(produitList.size() - 1);
    }

    @Test
    void patchNonExistingProduit() throws Exception {
        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();
        produit.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, produit.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(produit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProduit() throws Exception {
        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();
        produit.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(produit))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProduit() throws Exception {
        int databaseSizeBeforeUpdate = produitRepository.findAll().collectList().block().size();
        produit.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(produit))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Produit in the database
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProduit() {
        // Initialize the database
        produitRepository.save(produit).block();

        int databaseSizeBeforeDelete = produitRepository.findAll().collectList().block().size();

        // Delete the produit
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, produit.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Produit> produitList = produitRepository.findAll().collectList().block();
        assertThat(produitList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
