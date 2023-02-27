package edu.bu.met.cs633.web.rest

import edu.bu.met.cs633.IntegrationTest
import edu.bu.met.cs633.domain.Vehicle
import edu.bu.met.cs633.repository.VehicleRepository
import edu.bu.met.cs633.repository.search.VehicleSearchRepository
import org.apache.commons.collections4.IterableUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.util.IterableUtil
import org.awaitility.Awaitility.await
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import java.util.Random
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [VehicleResource] REST controller.
 */
@IntegrationTest
@Extensions(
    ExtendWith(MockitoExtension::class)
)
@AutoConfigureMockMvc
@WithMockUser
class VehicleResourceIT {
    @Autowired
    private lateinit var vehicleRepository: VehicleRepository

    @Mock
    private lateinit var vehicleRepositoryMock: VehicleRepository
    @Autowired
    private lateinit var vehicleSearchRepository: VehicleSearchRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var restVehicleMockMvc: MockMvc

    private lateinit var vehicle: Vehicle

    @AfterEach
    fun cleanupElasticSearchRepository() {
        vehicleSearchRepository.deleteAll()
        assertThat(vehicleSearchRepository.count()).isEqualTo(0)
    }

    @BeforeEach
    fun initTest() {
        vehicle = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createVehicle() {
        val databaseSizeBeforeCreate = vehicleRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        // Create the Vehicle
        restVehicleMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(vehicle))
        ).andExpect(status().isCreated)

        // Validate the Vehicle in the database
        val vehicleList = vehicleRepository.findAll()
        assertThat(vehicleList).hasSize(databaseSizeBeforeCreate + 1)
        await().atMost(5, TimeUnit.SECONDS).untilAsserted {
            val searchDatabaseSizeAfter = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
            assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1)
        }
        val testVehicle = vehicleList[vehicleList.size - 1]

        assertThat(testVehicle.make).isEqualTo(DEFAULT_MAKE)
        assertThat(testVehicle.model).isEqualTo(DEFAULT_MODEL)
        assertThat(testVehicle.color).isEqualTo(DEFAULT_COLOR)
        assertThat(testVehicle.vin).isEqualTo(DEFAULT_VIN)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createVehicleWithExistingId() {
        // Create the Vehicle with an existing ID
        vehicle.id = 1L

        val databaseSizeBeforeCreate = vehicleRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        // An entity with an existing ID cannot be created, so this API call must fail
        restVehicleMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(vehicle))
        ).andExpect(status().isBadRequest)

        // Validate the Vehicle in the database
        val vehicleList = vehicleRepository.findAll()
        assertThat(vehicleList).hasSize(databaseSizeBeforeCreate)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllVehicles() {
        // Initialize the database
        vehicleRepository.saveAndFlush(vehicle)

        // Get all the vehicleList
        restVehicleMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vehicle.id?.toInt())))
            .andExpect(jsonPath("$.[*].make").value(hasItem(DEFAULT_MAKE)))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)))
            .andExpect(jsonPath("$.[*].vin").value(hasItem(DEFAULT_VIN)))
    }

    @Suppress("unchecked")
    @Throws(Exception::class)
    fun getAllVehiclesWithEagerRelationshipsIsEnabled() {
        `when`(vehicleRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        restVehicleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false"))
            .andExpect(status().isOk)

        verify(vehicleRepositoryMock, times(1)).findAll(any(Pageable::class.java))
    }

    @Suppress("unchecked")
    @Throws(Exception::class)
    fun getAllVehiclesWithEagerRelationshipsIsNotEnabled() {
        `when`(vehicleRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        restVehicleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true"))
            .andExpect(status().isOk)

        verify(vehicleRepositoryMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getVehicle() {
        // Initialize the database
        vehicleRepository.saveAndFlush(vehicle)

        val id = vehicle.id
        assertNotNull(id)

        // Get the vehicle
        restVehicleMockMvc.perform(get(ENTITY_API_URL_ID, vehicle.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(vehicle.id?.toInt()))
            .andExpect(jsonPath("$.make").value(DEFAULT_MAKE))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL))
            .andExpect(jsonPath("$.color").value(DEFAULT_COLOR))
            .andExpect(jsonPath("$.vin").value(DEFAULT_VIN))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingVehicle() {
        // Get the vehicle
        restVehicleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putExistingVehicle() {
        // Initialize the database
        vehicleRepository.saveAndFlush(vehicle)

        val databaseSizeBeforeUpdate = vehicleRepository.findAll().size

        vehicleSearchRepository.save(vehicle)
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        // Update the vehicle
        val updatedVehicle = vehicleRepository.findById(vehicle.id).get()
        // Disconnect from session so that the updates on updatedVehicle are not directly saved in db
        em.detach(updatedVehicle)
        updatedVehicle.make = UPDATED_MAKE
        updatedVehicle.model = UPDATED_MODEL
        updatedVehicle.color = UPDATED_COLOR
        updatedVehicle.vin = UPDATED_VIN

        restVehicleMockMvc.perform(
            put(ENTITY_API_URL_ID, updatedVehicle.id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedVehicle))
        ).andExpect(status().isOk)

        // Validate the Vehicle in the database
        val vehicleList = vehicleRepository.findAll()
        assertThat(vehicleList).hasSize(databaseSizeBeforeUpdate)
        val testVehicle = vehicleList[vehicleList.size - 1]
        assertThat(testVehicle.make).isEqualTo(UPDATED_MAKE)
        assertThat(testVehicle.model).isEqualTo(UPDATED_MODEL)
        assertThat(testVehicle.color).isEqualTo(UPDATED_COLOR)
        assertThat(testVehicle.vin).isEqualTo(UPDATED_VIN)
        await().atMost(5, TimeUnit.SECONDS).untilAsserted {
            val searchDatabaseSizeAfter = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
            assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
            val vehicleSearchList = IterableUtils.toList(vehicleSearchRepository.findAll())
            val testVehicleSearch = vehicleSearchList.get(searchDatabaseSizeAfter - 1)
            assertThat(testVehicleSearch.make).isEqualTo(UPDATED_MAKE)
            assertThat(testVehicleSearch.model).isEqualTo(UPDATED_MODEL)
            assertThat(testVehicleSearch.color).isEqualTo(UPDATED_COLOR)
            assertThat(testVehicleSearch.vin).isEqualTo(UPDATED_VIN)
        }
    }

    @Test
    @Transactional
    fun putNonExistingVehicle() {
        val databaseSizeBeforeUpdate = vehicleRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        vehicle.id = count.incrementAndGet()

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVehicleMockMvc.perform(
            put(ENTITY_API_URL_ID, vehicle.id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(vehicle))
        )
            .andExpect(status().isBadRequest)

        // Validate the Vehicle in the database
        val vehicleList = vehicleRepository.findAll()
        assertThat(vehicleList).hasSize(databaseSizeBeforeUpdate)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchVehicle() {
        val databaseSizeBeforeUpdate = vehicleRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        vehicle.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVehicleMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(vehicle))
        ).andExpect(status().isBadRequest)

        // Validate the Vehicle in the database
        val vehicleList = vehicleRepository.findAll()
        assertThat(vehicleList).hasSize(databaseSizeBeforeUpdate)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamVehicle() {
        val databaseSizeBeforeUpdate = vehicleRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        vehicle.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVehicleMockMvc.perform(
            put(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(vehicle))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Vehicle in the database
        val vehicleList = vehicleRepository.findAll()
        assertThat(vehicleList).hasSize(databaseSizeBeforeUpdate)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateVehicleWithPatch() {
        vehicleRepository.saveAndFlush(vehicle)

        val databaseSizeBeforeUpdate = vehicleRepository.findAll().size

// Update the vehicle using partial update
        val partialUpdatedVehicle = Vehicle().apply {
            id = vehicle.id

            model = UPDATED_MODEL
            color = UPDATED_COLOR
        }

        restVehicleMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedVehicle.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedVehicle))
        )
            .andExpect(status().isOk)

// Validate the Vehicle in the database
        val vehicleList = vehicleRepository.findAll()
        assertThat(vehicleList).hasSize(databaseSizeBeforeUpdate)
        val testVehicle = vehicleList.last()
        assertThat(testVehicle.make).isEqualTo(DEFAULT_MAKE)
        assertThat(testVehicle.model).isEqualTo(UPDATED_MODEL)
        assertThat(testVehicle.color).isEqualTo(UPDATED_COLOR)
        assertThat(testVehicle.vin).isEqualTo(DEFAULT_VIN)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateVehicleWithPatch() {
        vehicleRepository.saveAndFlush(vehicle)

        val databaseSizeBeforeUpdate = vehicleRepository.findAll().size

// Update the vehicle using partial update
        val partialUpdatedVehicle = Vehicle().apply {
            id = vehicle.id

            make = UPDATED_MAKE
            model = UPDATED_MODEL
            color = UPDATED_COLOR
            vin = UPDATED_VIN
        }

        restVehicleMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedVehicle.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedVehicle))
        )
            .andExpect(status().isOk)

// Validate the Vehicle in the database
        val vehicleList = vehicleRepository.findAll()
        assertThat(vehicleList).hasSize(databaseSizeBeforeUpdate)
        val testVehicle = vehicleList.last()
        assertThat(testVehicle.make).isEqualTo(UPDATED_MAKE)
        assertThat(testVehicle.model).isEqualTo(UPDATED_MODEL)
        assertThat(testVehicle.color).isEqualTo(UPDATED_COLOR)
        assertThat(testVehicle.vin).isEqualTo(UPDATED_VIN)
    }

    @Throws(Exception::class)
    fun patchNonExistingVehicle() {
        val databaseSizeBeforeUpdate = vehicleRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        vehicle.id = count.incrementAndGet()

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVehicleMockMvc.perform(
            patch(ENTITY_API_URL_ID, vehicle.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(vehicle))
        )
            .andExpect(status().isBadRequest)

        // Validate the Vehicle in the database
        val vehicleList = vehicleRepository.findAll()
        assertThat(vehicleList).hasSize(databaseSizeBeforeUpdate)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchVehicle() {
        val databaseSizeBeforeUpdate = vehicleRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        vehicle.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVehicleMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet()).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(vehicle))
        )
            .andExpect(status().isBadRequest)

        // Validate the Vehicle in the database
        val vehicleList = vehicleRepository.findAll()
        assertThat(vehicleList).hasSize(databaseSizeBeforeUpdate)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamVehicle() {
        val databaseSizeBeforeUpdate = vehicleRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        vehicle.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVehicleMockMvc.perform(
            patch(ENTITY_API_URL).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(vehicle))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Vehicle in the database
        val vehicleList = vehicleRepository.findAll()
        assertThat(vehicleList).hasSize(databaseSizeBeforeUpdate)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteVehicle() {
        // Initialize the database
        vehicleRepository.saveAndFlush(vehicle)
        vehicleRepository.save(vehicle)
        vehicleSearchRepository.save(vehicle)
        val databaseSizeBeforeDelete = vehicleRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete)
        // Delete the vehicle
        restVehicleMockMvc.perform(
            delete(ENTITY_API_URL_ID, vehicle.id).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val vehicleList = vehicleRepository.findAll()
        assertThat(vehicleList).hasSize(databaseSizeBeforeDelete - 1)

        val searchDatabaseSizeAfter = IterableUtil.sizeOf(vehicleSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchVehicle() {
        // Initialize the database
        vehicle = vehicleRepository.saveAndFlush(vehicle)
        vehicleSearchRepository.save(vehicle)
        // Search the vehicle
        restVehicleMockMvc.perform(get("$ENTITY_SEARCH_API_URL?query=id:${vehicle.id}"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vehicle.id?.toInt())))
            .andExpect(jsonPath("$.[*].make").value(hasItem(DEFAULT_MAKE)))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL)))
            .andExpect(jsonPath("$.[*].color").value(hasItem(DEFAULT_COLOR)))
            .andExpect(jsonPath("$.[*].vin").value(hasItem(DEFAULT_VIN)))
    }

    companion object {

        private const val DEFAULT_MAKE = "AAAAAAAAAA"
        private const val UPDATED_MAKE = "BBBBBBBBBB"

        private const val DEFAULT_MODEL = "AAAAAAAAAA"
        private const val UPDATED_MODEL = "BBBBBBBBBB"

        private const val DEFAULT_COLOR = "AAAAAAAAAA"
        private const val UPDATED_COLOR = "BBBBBBBBBB"

        private const val DEFAULT_VIN = "AAAAAAAAAA"
        private const val UPDATED_VIN = "BBBBBBBBBB"

        private val ENTITY_API_URL: String = "/api/vehicles"
        private val ENTITY_API_URL_ID: String = ENTITY_API_URL + "/{id}"
        private val ENTITY_SEARCH_API_URL: String = "/api/_search/vehicles"

        private val random: Random = Random()
        private val count: AtomicLong = AtomicLong(random.nextInt().toLong() + (2 * Integer.MAX_VALUE))

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Vehicle {
            val vehicle = Vehicle(
                make = DEFAULT_MAKE,

                model = DEFAULT_MODEL,

                color = DEFAULT_COLOR,

                vin = DEFAULT_VIN

            )

            return vehicle
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Vehicle {
            val vehicle = Vehicle(
                make = UPDATED_MAKE,

                model = UPDATED_MODEL,

                color = UPDATED_COLOR,

                vin = UPDATED_VIN

            )

            return vehicle
        }
    }
}
