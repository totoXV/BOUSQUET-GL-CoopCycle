package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Commande;
import com.mycompany.myapp.repository.CommandeRepository;
import com.mycompany.myapp.repository.EntityManager;
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
 * Integration tests for the {@link CommandeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CommandeResourceIT {

    private static final String ENTITY_API_URL = "/api/commandes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Commande commande;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Commande createEntity(EntityManager em) {
        Commande commande = new Commande();
        return commande;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Commande createUpdatedEntity(EntityManager em) {
        Commande commande = new Commande();
        return commande;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Commande.class).block();
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
        commande = createEntity(em);
    }

    @Test
    void createCommande() throws Exception {
        int databaseSizeBeforeCreate = commandeRepository.findAll().collectList().block().size();
        // Create the Commande
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commande))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeCreate + 1);
        Commande testCommande = commandeList.get(commandeList.size() - 1);
    }

    @Test
    void createCommandeWithExistingId() throws Exception {
        // Create the Commande with an existing ID
        commande.setId(1L);

        int databaseSizeBeforeCreate = commandeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commande))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCommandesAsStream() {
        // Initialize the database
        commandeRepository.save(commande).block();

        List<Commande> commandeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Commande.class)
            .getResponseBody()
            .filter(commande::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(commandeList).isNotNull();
        assertThat(commandeList).hasSize(1);
        Commande testCommande = commandeList.get(0);
    }

    @Test
    void getAllCommandes() {
        // Initialize the database
        commandeRepository.save(commande).block();

        // Get all the commandeList
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
            .value(hasItem(commande.getId().intValue()));
    }

    @Test
    void getCommande() {
        // Initialize the database
        commandeRepository.save(commande).block();

        // Get the commande
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, commande.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(commande.getId().intValue()));
    }

    @Test
    void getNonExistingCommande() {
        // Get the commande
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCommande() throws Exception {
        // Initialize the database
        commandeRepository.save(commande).block();

        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();

        // Update the commande
        Commande updatedCommande = commandeRepository.findById(commande.getId()).block();

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCommande.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCommande))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
        Commande testCommande = commandeList.get(commandeList.size() - 1);
    }

    @Test
    void putNonExistingCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();
        commande.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, commande.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commande))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();
        commande.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commande))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();
        commande.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(commande))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCommandeWithPatch() throws Exception {
        // Initialize the database
        commandeRepository.save(commande).block();

        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();

        // Update the commande using partial update
        Commande partialUpdatedCommande = new Commande();
        partialUpdatedCommande.setId(commande.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCommande.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCommande))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
        Commande testCommande = commandeList.get(commandeList.size() - 1);
    }

    @Test
    void fullUpdateCommandeWithPatch() throws Exception {
        // Initialize the database
        commandeRepository.save(commande).block();

        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();

        // Update the commande using partial update
        Commande partialUpdatedCommande = new Commande();
        partialUpdatedCommande.setId(commande.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCommande.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCommande))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
        Commande testCommande = commandeList.get(commandeList.size() - 1);
    }

    @Test
    void patchNonExistingCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();
        commande.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, commande.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commande))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();
        commande.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commande))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().collectList().block().size();
        commande.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(commande))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCommande() {
        // Initialize the database
        commandeRepository.save(commande).block();

        int databaseSizeBeforeDelete = commandeRepository.findAll().collectList().block().size();

        // Delete the commande
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, commande.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Commande> commandeList = commandeRepository.findAll().collectList().block();
        assertThat(commandeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
