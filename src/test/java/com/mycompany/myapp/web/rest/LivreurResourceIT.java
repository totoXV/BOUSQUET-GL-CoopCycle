package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Livreur;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.LivreurRepository;
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
 * Integration tests for the {@link LivreurResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class LivreurResourceIT {

    private static final String ENTITY_API_URL = "/api/livreurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LivreurRepository livreurRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Livreur livreur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Livreur createEntity(EntityManager em) {
        Livreur livreur = new Livreur();
        return livreur;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Livreur createUpdatedEntity(EntityManager em) {
        Livreur livreur = new Livreur();
        return livreur;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Livreur.class).block();
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
        livreur = createEntity(em);
    }

    @Test
    void createLivreur() throws Exception {
        int databaseSizeBeforeCreate = livreurRepository.findAll().collectList().block().size();
        // Create the Livreur
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(livreur))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll().collectList().block();
        assertThat(livreurList).hasSize(databaseSizeBeforeCreate + 1);
        Livreur testLivreur = livreurList.get(livreurList.size() - 1);
    }

    @Test
    void createLivreurWithExistingId() throws Exception {
        // Create the Livreur with an existing ID
        livreur.setId(1L);

        int databaseSizeBeforeCreate = livreurRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(livreur))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll().collectList().block();
        assertThat(livreurList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllLivreursAsStream() {
        // Initialize the database
        livreurRepository.save(livreur).block();

        List<Livreur> livreurList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Livreur.class)
            .getResponseBody()
            .filter(livreur::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(livreurList).isNotNull();
        assertThat(livreurList).hasSize(1);
        Livreur testLivreur = livreurList.get(0);
    }

    @Test
    void getAllLivreurs() {
        // Initialize the database
        livreurRepository.save(livreur).block();

        // Get all the livreurList
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
            .value(hasItem(livreur.getId().intValue()));
    }

    @Test
    void getLivreur() {
        // Initialize the database
        livreurRepository.save(livreur).block();

        // Get the livreur
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, livreur.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(livreur.getId().intValue()));
    }

    @Test
    void getNonExistingLivreur() {
        // Get the livreur
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingLivreur() throws Exception {
        // Initialize the database
        livreurRepository.save(livreur).block();

        int databaseSizeBeforeUpdate = livreurRepository.findAll().collectList().block().size();

        // Update the livreur
        Livreur updatedLivreur = livreurRepository.findById(livreur.getId()).block();

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedLivreur.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedLivreur))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll().collectList().block();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
        Livreur testLivreur = livreurList.get(livreurList.size() - 1);
    }

    @Test
    void putNonExistingLivreur() throws Exception {
        int databaseSizeBeforeUpdate = livreurRepository.findAll().collectList().block().size();
        livreur.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, livreur.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(livreur))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll().collectList().block();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchLivreur() throws Exception {
        int databaseSizeBeforeUpdate = livreurRepository.findAll().collectList().block().size();
        livreur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(livreur))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll().collectList().block();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamLivreur() throws Exception {
        int databaseSizeBeforeUpdate = livreurRepository.findAll().collectList().block().size();
        livreur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(livreur))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll().collectList().block();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateLivreurWithPatch() throws Exception {
        // Initialize the database
        livreurRepository.save(livreur).block();

        int databaseSizeBeforeUpdate = livreurRepository.findAll().collectList().block().size();

        // Update the livreur using partial update
        Livreur partialUpdatedLivreur = new Livreur();
        partialUpdatedLivreur.setId(livreur.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLivreur.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedLivreur))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll().collectList().block();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
        Livreur testLivreur = livreurList.get(livreurList.size() - 1);
    }

    @Test
    void fullUpdateLivreurWithPatch() throws Exception {
        // Initialize the database
        livreurRepository.save(livreur).block();

        int databaseSizeBeforeUpdate = livreurRepository.findAll().collectList().block().size();

        // Update the livreur using partial update
        Livreur partialUpdatedLivreur = new Livreur();
        partialUpdatedLivreur.setId(livreur.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLivreur.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedLivreur))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll().collectList().block();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
        Livreur testLivreur = livreurList.get(livreurList.size() - 1);
    }

    @Test
    void patchNonExistingLivreur() throws Exception {
        int databaseSizeBeforeUpdate = livreurRepository.findAll().collectList().block().size();
        livreur.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, livreur.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(livreur))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll().collectList().block();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchLivreur() throws Exception {
        int databaseSizeBeforeUpdate = livreurRepository.findAll().collectList().block().size();
        livreur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(livreur))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll().collectList().block();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamLivreur() throws Exception {
        int databaseSizeBeforeUpdate = livreurRepository.findAll().collectList().block().size();
        livreur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(livreur))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll().collectList().block();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteLivreur() {
        // Initialize the database
        livreurRepository.save(livreur).block();

        int databaseSizeBeforeDelete = livreurRepository.findAll().collectList().block().size();

        // Delete the livreur
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, livreur.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Livreur> livreurList = livreurRepository.findAll().collectList().block();
        assertThat(livreurList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
