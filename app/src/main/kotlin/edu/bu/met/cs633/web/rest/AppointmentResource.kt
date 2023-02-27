package edu.bu.met.cs633.web.rest

import edu.bu.met.cs633.domain.Appointment
import edu.bu.met.cs633.repository.AppointmentRepository
import edu.bu.met.cs633.repository.UserRepository
import edu.bu.met.cs633.repository.VehicleRepository
import edu.bu.met.cs633.repository.search.AppointmentSearchRepository
import edu.bu.met.cs633.utils.sendEmail
import edu.bu.met.cs633.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import tech.jhipster.web.util.HeaderUtil
import tech.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*
import java.util.stream.Collectors
import javax.validation.Valid
import javax.validation.constraints.NotNull

private const val ENTITY_NAME = "appointment"
/**
 * REST controller for managing [edu.bu.met.cs633.domain.Appointment].
 */
@RestController
@RequestMapping("/api")
@Transactional
class AppointmentResource(
    private val userRepository: UserRepository,
    private val vehicleRepository: VehicleRepository,
    private val appointmentRepository: AppointmentRepository,
    private val appointmentSearchRepository: AppointmentSearchRepository,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "appointment"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /appointments` : Create a new appointment.
     *
     * @param appointment the appointment to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new appointment, or with status `400 (Bad Request)` if the appointment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/appointments")
    fun createAppointment(@Valid @RequestBody appointment: Appointment): ResponseEntity<Appointment> {
        log.debug("REST request to save Appointment : $appointment")
        if (appointment.id != null) {
            throw BadRequestAlertException(
                "A new appointment cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = appointmentRepository.save(appointment)
        appointmentSearchRepository.index(result)

        val existingUser = userRepository.findOneByLogin(appointment.client?.login!!)

        val formatter: DateTimeFormatter = DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("EEE MMM dd, yyyy h:mma z")
            .toFormatter(Locale.US).withZone(ZoneId.systemDefault())

        val vehicle = vehicleRepository.findByOwnerIsCurrentUser().firstOrNull()

        if (existingUser.isPresent) {
            val client = existingUser.get()
            log.debug(client.toString())
            // TODO could clean up this function. Quick and dirty for now.

            var vehicleInfo = ""

            if (vehicle != null) {
                log.debug(vehicle.toString())

                val make = vehicle.make.orEmpty()
                val model = vehicle.model.orEmpty()

                vehicleInfo = "$make $model"
            }

            sendEmail(
                firstName = client.firstName.orEmpty(),
                lastName = client.lastName.orEmpty(),
                appointmentTime = formatter.format(appointment.startTime),
                toEmail = client.email.orEmpty(),
                vehicle = vehicleInfo,
                phoneNumber = client.phone.orEmpty(),
                services = appointment.services.orEmpty()
            )
        }

        return ResponseEntity.created(URI("/api/appointments/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /appointments/:id} : Updates an existing appointment.
     *
     * @param id the id of the appointment to save.
     * @param appointment the appointment to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated appointment,
     * or with status `400 (Bad Request)` if the appointment is not valid,
     * or with status `500 (Internal Server Error)` if the appointment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/appointments/{id}")
    fun updateAppointment(
        @PathVariable(value = "id", required = false) id: Long,
        @Valid @RequestBody appointment: Appointment
    ): ResponseEntity<Appointment> {
        log.debug("REST request to update Appointment : {}, {}", id, appointment)
        if (appointment.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, appointment.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!appointmentRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = appointmentRepository.save(appointment)
        appointmentSearchRepository.index(result)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    appointment.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /appointments/:id} : Partial updates given fields of an existing appointment, field will ignore if it is null
     *
     * @param id the id of the appointment to save.
     * @param appointment the appointment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appointment,
     * or with status {@code 400 (Bad Request)} if the appointment is not valid,
     * or with status {@code 404 (Not Found)} if the appointment is not found,
     * or with status {@code 500 (Internal Server Error)} if the appointment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/appointments/{id}"], consumes = ["application/json", "application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateAppointment(
        @PathVariable(value = "id", required = false) id: Long,
        @NotNull @RequestBody appointment: Appointment
    ): ResponseEntity<Appointment> {
        log.debug("REST request to partial update Appointment partially : {}, {}", id, appointment)
        if (appointment.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, appointment.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!appointmentRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = appointmentRepository.findById(appointment.id)
            .map {

                if (appointment.created != null) {
                    it.created = appointment.created
                }
                if (appointment.startTime != null) {
                    it.startTime = appointment.startTime
                }
                if (appointment.endTime != null) {
                    it.endTime = appointment.endTime
                }
                if (appointment.services != null) {
                    it.services = appointment.services
                }

                it
            }
            .map { appointmentRepository.save(it) }
            .map {
                appointmentSearchRepository.save(it)

                it
            }

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appointment.id.toString())
        )
    }

    /**
     * `GET  /appointments` : get all the appointments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the [ResponseEntity] with status `200 (OK)` and the list of appointments in body.
     */
    @GetMapping("/appointments")
    fun getAllAppointments(@RequestParam(required = false, defaultValue = "false") eagerload: Boolean): MutableList<Appointment> {

        log.debug("REST request to get all Appointments")
        if (eagerload) {
            return appointmentRepository.findAllWithEagerRelationships()
        } else {
            return appointmentRepository.findAll()
        }
    }

    /**
     * `GET  /appointments/:id` : get the "id" appointment.
     *
     * @param id the id of the appointment to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the appointment, or with status `404 (Not Found)`.
     */
    @GetMapping("/appointments/{id}")
    fun getAppointment(@PathVariable id: Long): ResponseEntity<Appointment> {
        log.debug("REST request to get Appointment : $id")
        val appointment = appointmentRepository.findOneWithEagerRelationships(id)
        return ResponseUtil.wrapOrNotFound(appointment)
    }

    @GetMapping("/appointments/user")
    fun getAppointmentsByCurrentUser(): MutableList<Appointment> {
        log.debug("REST request to get Appointments for user")
        return appointmentRepository.findByClientIsCurrentUser()
    }

    /**
     *  `DELETE  /appointments/:id` : delete the "id" appointment.
     *
     * @param id the id of the appointment to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/appointments/{id}")
    fun deleteAppointment(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Appointment : $id")

        appointmentRepository.deleteById(id)
        appointmentSearchRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/appointments?query=:query` : search for the appointment corresponding
     * to the query.
     *
     * @param query the query of the appointment search.
     * @return the result of the search.
     */
    @GetMapping("/_search/appointments")
    fun searchAppointments(@RequestParam query: String): MutableList<Appointment> {
        log.debug("REST request to search Appointments for query $query")
        return appointmentSearchRepository.search(query)
            .collect(Collectors.toList())
            .toMutableList()
    }
}
