package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Modelo;
import com.mycompany.myapp.repository.ModeloRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ModeloResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ModeloResourceIT {

    private static final String DEFAULT_MODELO = "AAAAAAAAAA";
    private static final String UPDATED_MODELO = "BBBBBBBBBB";

    private static final String DEFAULT_COLOR = "AAAAAAAAAA";
    private static final String UPDATED_COLOR = "BBBBBBBBBB";

    private static final Integer DEFAULT_POTENCIA = 1;
    private static final Integer UPDATED_POTENCIA = 2;

    private static final Integer DEFAULT_PLAZAS = 1;
    private static final Integer UPDATED_PLAZAS = 2;

    private static final Double DEFAULT_PRECIO = 1D;
    private static final Double UPDATED_PRECIO = 2D;

    private static final String ENTITY_API_URL = "/api/modelos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ModeloRepository modeloRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restModeloMockMvc;

    private Modelo modelo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Modelo createEntity(EntityManager em) {
        Modelo modelo = new Modelo()
            .modelo(DEFAULT_MODELO)
            .color(DEFAULT_COLOR)
            .potencia(DEFAULT_POTENCIA)
            .plazas(DEFAULT_PLAZAS)
            .precio(DEFAULT_PRECIO);
        return modelo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Modelo createUpdatedEntity(EntityManager em) {
        Modelo modelo = new Modelo()
            .modelo(UPDATED_MODELO)
            .color(UPDATED_COLOR)
            .potencia(UPDATED_POTENCIA)
            .plazas(UPDATED_PLAZAS)
            .precio(UPDATED_PRECIO);
        return modelo;
    }

    @BeforeEach
    public void initTest() {
        modelo = createEntity(em);
    }

    @Test
    @Transactional
    void createModelo() throws Exception {
        int databaseSizeBeforeCreate = modeloRepository.findAll().size();
        // Create the Modelo
        restModeloMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(modelo)))
            .andExpect(status().isCreated());

        // Validate the Modelo in the database
        List<Modelo> modeloList = modeloRepository.findAll();
        assertThat(modeloList).hasSize(databaseSizeBeforeCreate + 1);
        Modelo testModelo = modeloList.get(modeloList.size() - 1);
        assertThat(testModelo.getModelo()).isEqualTo(DEFAULT_MODELO);
        assertThat(testModelo.getColor()).isEqualTo(DEFAULT_COLOR);
        assertThat(testModelo.getPotencia()).isEqualTo(DEFAULT_POTENCIA);
        assertThat(testModelo.getPlazas()).isEqualTo(DEFAULT_PLAZAS);
        assertThat(testModelo.getPrecio()).isEqualTo(DEFAULT_PRECIO);
    }

    @Test
    @Transactional
    void createModeloWithExistingId() throws Exception {
        // Create the Modelo with an existing ID
        modelo.setId(1L);

        int databaseSizeBeforeCreate = modeloRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restModeloMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(modelo)))
            .andExpect(status().isBadRequest());

        // Validate the Modelo in the database
        List<Modelo> modeloList = modeloRepository.findAll();
        assertThat(modeloList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkModeloIsRequired() throws Exception {
        int databaseSizeBeforeTest = modeloRepository.findAll().size();
        // set the field null
        modelo.setModelo(null);

        // Create the Modelo, which fails.

        restModeloMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(modelo)))
            .andExpect(status().isBadRequest());

        List<Modelo> modeloList = modeloRepository.findAll();
        assertThat(modeloList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllModelos() throws Exception {
        // Initialize the database
        modeloRepository.saveAndFlush(modelo);

        // Get all the modeloList
        restModeloMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(modelo.getId().intValue())))
            .andExpect(jsonPath("$.[*].modelo").value(hasItem(DEFAULT_MODELO)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)))
            .andExpect(jsonPath("$.[*].potencia").value(hasItem(DEFAULT_POTENCIA)))
            .andExpect(jsonPath("$.[*].plazas").value(hasItem(DEFAULT_PLAZAS)))
            .andExpect(jsonPath("$.[*].precio").value(hasItem(DEFAULT_PRECIO.doubleValue())));
    }

    @Test
    @Transactional
    void getModelo() throws Exception {
        // Initialize the database
        modeloRepository.saveAndFlush(modelo);

        // Get the modelo
        restModeloMockMvc
            .perform(get(ENTITY_API_URL_ID, modelo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(modelo.getId().intValue()))
            .andExpect(jsonPath("$.modelo").value(DEFAULT_MODELO))
            .andExpect(jsonPath("$.color").value(DEFAULT_COLOR))
            .andExpect(jsonPath("$.potencia").value(DEFAULT_POTENCIA))
            .andExpect(jsonPath("$.plazas").value(DEFAULT_PLAZAS))
            .andExpect(jsonPath("$.precio").value(DEFAULT_PRECIO.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingModelo() throws Exception {
        // Get the modelo
        restModeloMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewModelo() throws Exception {
        // Initialize the database
        modeloRepository.saveAndFlush(modelo);

        int databaseSizeBeforeUpdate = modeloRepository.findAll().size();

        // Update the modelo
        Modelo updatedModelo = modeloRepository.findById(modelo.getId()).get();
        // Disconnect from session so that the updates on updatedModelo are not directly saved in db
        em.detach(updatedModelo);
        updatedModelo.modelo(UPDATED_MODELO).color(UPDATED_COLOR).potencia(UPDATED_POTENCIA).plazas(UPDATED_PLAZAS).precio(UPDATED_PRECIO);

        restModeloMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedModelo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedModelo))
            )
            .andExpect(status().isOk());

        // Validate the Modelo in the database
        List<Modelo> modeloList = modeloRepository.findAll();
        assertThat(modeloList).hasSize(databaseSizeBeforeUpdate);
        Modelo testModelo = modeloList.get(modeloList.size() - 1);
        assertThat(testModelo.getModelo()).isEqualTo(UPDATED_MODELO);
        assertThat(testModelo.getColor()).isEqualTo(UPDATED_COLOR);
        assertThat(testModelo.getPotencia()).isEqualTo(UPDATED_POTENCIA);
        assertThat(testModelo.getPlazas()).isEqualTo(UPDATED_PLAZAS);
        assertThat(testModelo.getPrecio()).isEqualTo(UPDATED_PRECIO);
    }

    @Test
    @Transactional
    void putNonExistingModelo() throws Exception {
        int databaseSizeBeforeUpdate = modeloRepository.findAll().size();
        modelo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModeloMockMvc
            .perform(
                put(ENTITY_API_URL_ID, modelo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(modelo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Modelo in the database
        List<Modelo> modeloList = modeloRepository.findAll();
        assertThat(modeloList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchModelo() throws Exception {
        int databaseSizeBeforeUpdate = modeloRepository.findAll().size();
        modelo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModeloMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(modelo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Modelo in the database
        List<Modelo> modeloList = modeloRepository.findAll();
        assertThat(modeloList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamModelo() throws Exception {
        int databaseSizeBeforeUpdate = modeloRepository.findAll().size();
        modelo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModeloMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(modelo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Modelo in the database
        List<Modelo> modeloList = modeloRepository.findAll();
        assertThat(modeloList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateModeloWithPatch() throws Exception {
        // Initialize the database
        modeloRepository.saveAndFlush(modelo);

        int databaseSizeBeforeUpdate = modeloRepository.findAll().size();

        // Update the modelo using partial update
        Modelo partialUpdatedModelo = new Modelo();
        partialUpdatedModelo.setId(modelo.getId());

        partialUpdatedModelo.modelo(UPDATED_MODELO).color(UPDATED_COLOR).potencia(UPDATED_POTENCIA);

        restModeloMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModelo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedModelo))
            )
            .andExpect(status().isOk());

        // Validate the Modelo in the database
        List<Modelo> modeloList = modeloRepository.findAll();
        assertThat(modeloList).hasSize(databaseSizeBeforeUpdate);
        Modelo testModelo = modeloList.get(modeloList.size() - 1);
        assertThat(testModelo.getModelo()).isEqualTo(UPDATED_MODELO);
        assertThat(testModelo.getColor()).isEqualTo(UPDATED_COLOR);
        assertThat(testModelo.getPotencia()).isEqualTo(UPDATED_POTENCIA);
        assertThat(testModelo.getPlazas()).isEqualTo(DEFAULT_PLAZAS);
        assertThat(testModelo.getPrecio()).isEqualTo(DEFAULT_PRECIO);
    }

    @Test
    @Transactional
    void fullUpdateModeloWithPatch() throws Exception {
        // Initialize the database
        modeloRepository.saveAndFlush(modelo);

        int databaseSizeBeforeUpdate = modeloRepository.findAll().size();

        // Update the modelo using partial update
        Modelo partialUpdatedModelo = new Modelo();
        partialUpdatedModelo.setId(modelo.getId());

        partialUpdatedModelo
            .modelo(UPDATED_MODELO)
            .color(UPDATED_COLOR)
            .potencia(UPDATED_POTENCIA)
            .plazas(UPDATED_PLAZAS)
            .precio(UPDATED_PRECIO);

        restModeloMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModelo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedModelo))
            )
            .andExpect(status().isOk());

        // Validate the Modelo in the database
        List<Modelo> modeloList = modeloRepository.findAll();
        assertThat(modeloList).hasSize(databaseSizeBeforeUpdate);
        Modelo testModelo = modeloList.get(modeloList.size() - 1);
        assertThat(testModelo.getModelo()).isEqualTo(UPDATED_MODELO);
        assertThat(testModelo.getColor()).isEqualTo(UPDATED_COLOR);
        assertThat(testModelo.getPotencia()).isEqualTo(UPDATED_POTENCIA);
        assertThat(testModelo.getPlazas()).isEqualTo(UPDATED_PLAZAS);
        assertThat(testModelo.getPrecio()).isEqualTo(UPDATED_PRECIO);
    }

    @Test
    @Transactional
    void patchNonExistingModelo() throws Exception {
        int databaseSizeBeforeUpdate = modeloRepository.findAll().size();
        modelo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModeloMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, modelo.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(modelo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Modelo in the database
        List<Modelo> modeloList = modeloRepository.findAll();
        assertThat(modeloList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchModelo() throws Exception {
        int databaseSizeBeforeUpdate = modeloRepository.findAll().size();
        modelo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModeloMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(modelo))
            )
            .andExpect(status().isBadRequest());

        // Validate the Modelo in the database
        List<Modelo> modeloList = modeloRepository.findAll();
        assertThat(modeloList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamModelo() throws Exception {
        int databaseSizeBeforeUpdate = modeloRepository.findAll().size();
        modelo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModeloMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(modelo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Modelo in the database
        List<Modelo> modeloList = modeloRepository.findAll();
        assertThat(modeloList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteModelo() throws Exception {
        // Initialize the database
        modeloRepository.saveAndFlush(modelo);

        int databaseSizeBeforeDelete = modeloRepository.findAll().size();

        // Delete the modelo
        restModeloMockMvc
            .perform(delete(ENTITY_API_URL_ID, modelo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Modelo> modeloList = modeloRepository.findAll();
        assertThat(modeloList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
