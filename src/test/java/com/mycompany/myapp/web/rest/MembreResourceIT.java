package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Membre;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MembreRepository;
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
 * Integration tests for the {@link MembreResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MembreResourceIT {

    private static final String ENTITY_API_URL = "/api/membres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MembreRepository membreRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Membre membre;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Membre createEntity(EntityManager em) {
        Membre membre = new Membre();
        return membre;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Membre createUpdatedEntity(EntityManager em) {
        Membre membre = new Membre();
        return membre;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Membre.class).block();
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
        membre = createEntity(em);
    }

    @Test
    void createMembre() throws Exception {
        int databaseSizeBeforeCreate = membreRepository.findAll().collectList().block().size();
        // Create the Membre
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(membre))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Membre in the database
        List<Membre> membreList = membreRepository.findAll().collectList().block();
        assertThat(membreList).hasSize(databaseSizeBeforeCreate + 1);
        Membre testMembre = membreList.get(membreList.size() - 1);
    }

    @Test
    void createMembreWithExistingId() throws Exception {
        // Create the Membre with an existing ID
        membre.setId(1L);

        int databaseSizeBeforeCreate = membreRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(membre))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Membre in the database
        List<Membre> membreList = membreRepository.findAll().collectList().block();
        assertThat(membreList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllMembresAsStream() {
        // Initialize the database
        membreRepository.save(membre).block();

        List<Membre> membreList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Membre.class)
            .getResponseBody()
            .filter(membre::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(membreList).isNotNull();
        assertThat(membreList).hasSize(1);
        Membre testMembre = membreList.get(0);
    }

    @Test
    void getAllMembres() {
        // Initialize the database
        membreRepository.save(membre).block();

        // Get all the membreList
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
            .value(hasItem(membre.getId().intValue()));
    }

    @Test
    void getMembre() {
        // Initialize the database
        membreRepository.save(membre).block();

        // Get the membre
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, membre.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(membre.getId().intValue()));
    }

    @Test
    void getNonExistingMembre() {
        // Get the membre
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMembre() throws Exception {
        // Initialize the database
        membreRepository.save(membre).block();

        int databaseSizeBeforeUpdate = membreRepository.findAll().collectList().block().size();

        // Update the membre
        Membre updatedMembre = membreRepository.findById(membre.getId()).block();

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedMembre.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedMembre))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Membre in the database
        List<Membre> membreList = membreRepository.findAll().collectList().block();
        assertThat(membreList).hasSize(databaseSizeBeforeUpdate);
        Membre testMembre = membreList.get(membreList.size() - 1);
    }

    @Test
    void putNonExistingMembre() throws Exception {
        int databaseSizeBeforeUpdate = membreRepository.findAll().collectList().block().size();
        membre.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, membre.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(membre))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Membre in the database
        List<Membre> membreList = membreRepository.findAll().collectList().block();
        assertThat(membreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMembre() throws Exception {
        int databaseSizeBeforeUpdate = membreRepository.findAll().collectList().block().size();
        membre.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(membre))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Membre in the database
        List<Membre> membreList = membreRepository.findAll().collectList().block();
        assertThat(membreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMembre() throws Exception {
        int databaseSizeBeforeUpdate = membreRepository.findAll().collectList().block().size();
        membre.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(membre))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Membre in the database
        List<Membre> membreList = membreRepository.findAll().collectList().block();
        assertThat(membreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMembreWithPatch() throws Exception {
        // Initialize the database
        membreRepository.save(membre).block();

        int databaseSizeBeforeUpdate = membreRepository.findAll().collectList().block().size();

        // Update the membre using partial update
        Membre partialUpdatedMembre = new Membre();
        partialUpdatedMembre.setId(membre.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMembre.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMembre))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Membre in the database
        List<Membre> membreList = membreRepository.findAll().collectList().block();
        assertThat(membreList).hasSize(databaseSizeBeforeUpdate);
        Membre testMembre = membreList.get(membreList.size() - 1);
    }

    @Test
    void fullUpdateMembreWithPatch() throws Exception {
        // Initialize the database
        membreRepository.save(membre).block();

        int databaseSizeBeforeUpdate = membreRepository.findAll().collectList().block().size();

        // Update the membre using partial update
        Membre partialUpdatedMembre = new Membre();
        partialUpdatedMembre.setId(membre.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMembre.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMembre))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Membre in the database
        List<Membre> membreList = membreRepository.findAll().collectList().block();
        assertThat(membreList).hasSize(databaseSizeBeforeUpdate);
        Membre testMembre = membreList.get(membreList.size() - 1);
    }

    @Test
    void patchNonExistingMembre() throws Exception {
        int databaseSizeBeforeUpdate = membreRepository.findAll().collectList().block().size();
        membre.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, membre.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(membre))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Membre in the database
        List<Membre> membreList = membreRepository.findAll().collectList().block();
        assertThat(membreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMembre() throws Exception {
        int databaseSizeBeforeUpdate = membreRepository.findAll().collectList().block().size();
        membre.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(membre))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Membre in the database
        List<Membre> membreList = membreRepository.findAll().collectList().block();
        assertThat(membreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMembre() throws Exception {
        int databaseSizeBeforeUpdate = membreRepository.findAll().collectList().block().size();
        membre.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(membre))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Membre in the database
        List<Membre> membreList = membreRepository.findAll().collectList().block();
        assertThat(membreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMembre() {
        // Initialize the database
        membreRepository.save(membre).block();

        int databaseSizeBeforeDelete = membreRepository.findAll().collectList().block().size();

        // Delete the membre
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, membre.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Membre> membreList = membreRepository.findAll().collectList().block();
        assertThat(membreList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
