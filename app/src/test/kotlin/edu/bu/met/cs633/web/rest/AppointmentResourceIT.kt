package edu.bu.met.cs633.web.rest

import edu.bu.met.cs633.IntegrationTest
import edu.bu.met.cs633.domain.Appointment
import edu.bu.met.cs633.repository.AppointmentRepository
import edu.bu.met.cs633.repository.search.AppointmentSearchRepository
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
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Random
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [AppointmentResource] REST controller.
 */
@IntegrationTest
@Extensions(
    ExtendWith(MockitoExtension::class)
)
@AutoConfigureMockMvc
@WithMockUser
class AppointmentResourceIT {
    @Autowired
    private lateinit var appointmentRepository: AppointmentRepository

    @Mock
    private lateinit var appointmentRepositoryMock: AppointmentRepository
    @Autowired
    private lateinit var appointmentSearchRepository: AppointmentSearchRepository

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var restAppointmentMockMvc: MockMvc

    private lateinit var appointment: Appointment

    @AfterEach
    fun cleanupElasticSearchRepository() {
        appointmentSearchRepository.deleteAll()
        assertThat(appointmentSearchRepository.count()).isEqualTo(0)
    }

    @BeforeEach
    fun initTest() {
        appointment = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createAppointment() {
        val databaseSizeBeforeCreate = appointmentRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        // Create the Appointment
        restAppointmentMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(appointment))
        ).andExpect(status().isCreated)

        // Validate the Appointment in the database
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeCreate + 1)
        await().atMost(5, TimeUnit.SECONDS).untilAsserted {
            val searchDatabaseSizeAfter = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
            assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1)
        }
        val testAppointment = appointmentList[appointmentList.size - 1]

        assertThat(testAppointment.created).isEqualTo(DEFAULT_CREATED)
        assertThat(testAppointment.startTime).isEqualTo(DEFAULT_START_TIME)
        assertThat(testAppointment.endTime).isEqualTo(DEFAULT_END_TIME)
        assertThat(testAppointment.services).isEqualTo(DEFAULT_SERVICES)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createAppointmentWithExistingId() {
        // Create the Appointment with an existing ID
        appointment.id = 1L

        val databaseSizeBeforeCreate = appointmentRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        // An entity with an existing ID cannot be created, so this API call must fail
        restAppointmentMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(appointment))
        ).andExpect(status().isBadRequest)

        // Validate the Appointment in the database
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeCreate)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkCreatedIsRequired() {
        val databaseSizeBeforeTest = appointmentRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        // set the field null
        appointment.created = null

        // Create the Appointment, which fails.

        restAppointmentMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(appointment))
        ).andExpect(status().isBadRequest)

        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeTest)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkStartTimeIsRequired() {
        val databaseSizeBeforeTest = appointmentRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        // set the field null
        appointment.startTime = null

        // Create the Appointment, which fails.

        restAppointmentMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(appointment))
        ).andExpect(status().isBadRequest)

        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeTest)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkEndTimeIsRequired() {
        val databaseSizeBeforeTest = appointmentRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        // set the field null
        appointment.endTime = null

        // Create the Appointment, which fails.

        restAppointmentMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(appointment))
        ).andExpect(status().isBadRequest)

        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeTest)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkServicesIsRequired() {
        val databaseSizeBeforeTest = appointmentRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        // set the field null
        appointment.services = null

        // Create the Appointment, which fails.

        restAppointmentMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(appointment))
        ).andExpect(status().isBadRequest)

        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeTest)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllAppointments() {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment)

        // Get all the appointmentList
        restAppointmentMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appointment.id?.toInt())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].services").value(hasItem(DEFAULT_SERVICES)))
    }

    @Suppress("unchecked")
    @Throws(Exception::class)
    fun getAllAppointmentsWithEagerRelationshipsIsEnabled() {
        `when`(appointmentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        restAppointmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false"))
            .andExpect(status().isOk)

        verify(appointmentRepositoryMock, times(1)).findAll(any(Pageable::class.java))
    }

    @Suppress("unchecked")
    @Throws(Exception::class)
    fun getAllAppointmentsWithEagerRelationshipsIsNotEnabled() {
        `when`(appointmentRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        restAppointmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true"))
            .andExpect(status().isOk)

        verify(appointmentRepositoryMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAppointment() {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment)

        val id = appointment.id
        assertNotNull(id)

        // Get the appointment
        restAppointmentMockMvc.perform(get(ENTITY_API_URL_ID, appointment.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appointment.id?.toInt()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.services").value(DEFAULT_SERVICES))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingAppointment() {
        // Get the appointment
        restAppointmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putExistingAppointment() {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment)

        val databaseSizeBeforeUpdate = appointmentRepository.findAll().size

        appointmentSearchRepository.save(appointment)
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        // Update the appointment
        val updatedAppointment = appointmentRepository.findById(appointment.id).get()
        // Disconnect from session so that the updates on updatedAppointment are not directly saved in db
        em.detach(updatedAppointment)
        updatedAppointment.created = UPDATED_CREATED
        updatedAppointment.startTime = UPDATED_START_TIME
        updatedAppointment.endTime = UPDATED_END_TIME
        updatedAppointment.services = UPDATED_SERVICES

        restAppointmentMockMvc.perform(
            put(ENTITY_API_URL_ID, updatedAppointment.id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(updatedAppointment))
        ).andExpect(status().isOk)

        // Validate the Appointment in the database
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate)
        val testAppointment = appointmentList[appointmentList.size - 1]
        assertThat(testAppointment.created).isEqualTo(UPDATED_CREATED)
        assertThat(testAppointment.startTime).isEqualTo(UPDATED_START_TIME)
        assertThat(testAppointment.endTime).isEqualTo(UPDATED_END_TIME)
        assertThat(testAppointment.services).isEqualTo(UPDATED_SERVICES)
        await().atMost(5, TimeUnit.SECONDS).untilAsserted {
            val searchDatabaseSizeAfter = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
            assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
            val appointmentSearchList = IterableUtils.toList(appointmentSearchRepository.findAll())
            val testAppointmentSearch = appointmentSearchList.get(searchDatabaseSizeAfter - 1)
            assertThat(testAppointmentSearch.created).isEqualTo(UPDATED_CREATED)
            assertThat(testAppointmentSearch.startTime).isEqualTo(UPDATED_START_TIME)
            assertThat(testAppointmentSearch.endTime).isEqualTo(UPDATED_END_TIME)
            assertThat(testAppointmentSearch.services).isEqualTo(UPDATED_SERVICES)
        }
    }

    @Test
    @Transactional
    fun putNonExistingAppointment() {
        val databaseSizeBeforeUpdate = appointmentRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        appointment.id = count.incrementAndGet()

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppointmentMockMvc.perform(
            put(ENTITY_API_URL_ID, appointment.id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(appointment))
        )
            .andExpect(status().isBadRequest)

        // Validate the Appointment in the database
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchAppointment() {
        val databaseSizeBeforeUpdate = appointmentRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        appointment.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(appointment))
        ).andExpect(status().isBadRequest)

        // Validate the Appointment in the database
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamAppointment() {
        val databaseSizeBeforeUpdate = appointmentRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        appointment.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc.perform(
            put(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(appointment))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Appointment in the database
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateAppointmentWithPatch() {
        appointmentRepository.saveAndFlush(appointment)

        val databaseSizeBeforeUpdate = appointmentRepository.findAll().size

// Update the appointment using partial update
        val partialUpdatedAppointment = Appointment().apply {
            id = appointment.id

            created = UPDATED_CREATED
            endTime = UPDATED_END_TIME
            services = UPDATED_SERVICES
        }

        restAppointmentMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedAppointment.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedAppointment))
        )
            .andExpect(status().isOk)

// Validate the Appointment in the database
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate)
        val testAppointment = appointmentList.last()
        assertThat(testAppointment.created).isEqualTo(UPDATED_CREATED)
        assertThat(testAppointment.startTime).isEqualTo(DEFAULT_START_TIME)
        assertThat(testAppointment.endTime).isEqualTo(UPDATED_END_TIME)
        assertThat(testAppointment.services).isEqualTo(UPDATED_SERVICES)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateAppointmentWithPatch() {
        appointmentRepository.saveAndFlush(appointment)

        val databaseSizeBeforeUpdate = appointmentRepository.findAll().size

// Update the appointment using partial update
        val partialUpdatedAppointment = Appointment().apply {
            id = appointment.id

            created = UPDATED_CREATED
            startTime = UPDATED_START_TIME
            endTime = UPDATED_END_TIME
            services = UPDATED_SERVICES
        }

        restAppointmentMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedAppointment.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedAppointment))
        )
            .andExpect(status().isOk)

// Validate the Appointment in the database
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate)
        val testAppointment = appointmentList.last()
        assertThat(testAppointment.created).isEqualTo(UPDATED_CREATED)
        assertThat(testAppointment.startTime).isEqualTo(UPDATED_START_TIME)
        assertThat(testAppointment.endTime).isEqualTo(UPDATED_END_TIME)
        assertThat(testAppointment.services).isEqualTo(UPDATED_SERVICES)
    }

    @Throws(Exception::class)
    fun patchNonExistingAppointment() {
        val databaseSizeBeforeUpdate = appointmentRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        appointment.id = count.incrementAndGet()

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppointmentMockMvc.perform(
            patch(ENTITY_API_URL_ID, appointment.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(appointment))
        )
            .andExpect(status().isBadRequest)

        // Validate the Appointment in the database
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchAppointment() {
        val databaseSizeBeforeUpdate = appointmentRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        appointment.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet()).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(appointment))
        )
            .andExpect(status().isBadRequest)

        // Validate the Appointment in the database
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamAppointment() {
        val databaseSizeBeforeUpdate = appointmentRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        appointment.id = count.incrementAndGet()

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppointmentMockMvc.perform(
            patch(ENTITY_API_URL).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(appointment))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Appointment in the database
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeUpdate)
        val searchDatabaseSizeAfter = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteAppointment() {
        // Initialize the database
        appointmentRepository.saveAndFlush(appointment)
        appointmentRepository.save(appointment)
        appointmentSearchRepository.save(appointment)
        val databaseSizeBeforeDelete = appointmentRepository.findAll().size
        val searchDatabaseSizeBefore = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete)
        // Delete the appointment
        restAppointmentMockMvc.perform(
            delete(ENTITY_API_URL_ID, appointment.id).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val appointmentList = appointmentRepository.findAll()
        assertThat(appointmentList).hasSize(databaseSizeBeforeDelete - 1)

        val searchDatabaseSizeAfter = IterableUtil.sizeOf(appointmentSearchRepository.findAll())
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun searchAppointment() {
        // Initialize the database
        appointment = appointmentRepository.saveAndFlush(appointment)
        appointmentSearchRepository.save(appointment)
        // Search the appointment
        restAppointmentMockMvc.perform(get("$ENTITY_SEARCH_API_URL?query=id:${appointment.id}"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appointment.id?.toInt())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].services").value(hasItem(DEFAULT_SERVICES)))
    }

    companion object {

        private val DEFAULT_CREATED: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_CREATED: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private val DEFAULT_START_TIME: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_START_TIME: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private val DEFAULT_END_TIME: Instant = Instant.ofEpochMilli(0L)
        private val UPDATED_END_TIME: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

        private const val DEFAULT_SERVICES = "AAAAAAAAAA"
        private const val UPDATED_SERVICES = "BBBBBBBBBB"

        private val ENTITY_API_URL: String = "/api/appointments"
        private val ENTITY_API_URL_ID: String = ENTITY_API_URL + "/{id}"
        private val ENTITY_SEARCH_API_URL: String = "/api/_search/appointments"

        private val random: Random = Random()
        private val count: AtomicLong = AtomicLong(random.nextInt().toLong() + (2 * Integer.MAX_VALUE))

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Appointment {
            val appointment = Appointment(
                created = DEFAULT_CREATED,

                startTime = DEFAULT_START_TIME,

                endTime = DEFAULT_END_TIME,

                services = DEFAULT_SERVICES

            )

            return appointment
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Appointment {
            val appointment = Appointment(
                created = UPDATED_CREATED,

                startTime = UPDATED_START_TIME,

                endTime = UPDATED_END_TIME,

                services = UPDATED_SERVICES

            )

            return appointment
        }
    }
}
