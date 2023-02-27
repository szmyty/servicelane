package edu.bu.met.cs633.web.rest

import edu.bu.met.cs633.domain.Vehicle
import edu.bu.met.cs633.repository.VehicleRepository
import edu.bu.met.cs633.repository.search.VehicleSearchRepository
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
import java.util.*
import java.util.stream.Collectors

private const val ENTITY_NAME = "vehicle"
/**
 * REST controller for managing [edu.bu.met.cs633.domain.Vehicle].
 */
@RestController
@RequestMapping("/api")
@Transactional
class VehicleResource(
    private val vehicleRepository: VehicleRepository,
    private val vehicleSearchRepository: VehicleSearchRepository,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "vehicle"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /vehicles` : Create a new vehicle.
     *
     * @param vehicle the vehicle to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new vehicle, or with status `400 (Bad Request)` if the vehicle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/vehicles")
    fun createVehicle(@RequestBody vehicle: Vehicle): ResponseEntity<Vehicle> {
        log.debug("REST request to save Vehicle : $vehicle")
        if (vehicle.id != null) {
            throw BadRequestAlertException(
                "A new vehicle cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = vehicleRepository.save(vehicle)
        vehicleSearchRepository.index(result)
        return ResponseEntity.created(URI("/api/vehicles/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /vehicles/:id} : Updates an existing vehicle.
     *
     * @param id the id of the vehicle to save.
     * @param vehicle the vehicle to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated vehicle,
     * or with status `400 (Bad Request)` if the vehicle is not valid,
     * or with status `500 (Internal Server Error)` if the vehicle couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/vehicles/{id}")
    fun updateVehicle(
        @PathVariable(value = "id", required = false) id: Long,
        @RequestBody vehicle: Vehicle
    ): ResponseEntity<Vehicle> {
        log.debug("REST request to update Vehicle : {}, {}", id, vehicle)
        if (vehicle.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, vehicle.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!vehicleRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = vehicleRepository.save(vehicle)
        vehicleSearchRepository.index(result)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    vehicle.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /vehicles/:id} : Partial updates given fields of an existing vehicle, field will ignore if it is null
     *
     * @param id the id of the vehicle to save.
     * @param vehicle the vehicle to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vehicle,
     * or with status {@code 400 (Bad Request)} if the vehicle is not valid,
     * or with status {@code 404 (Not Found)} if the vehicle is not found,
     * or with status {@code 500 (Internal Server Error)} if the vehicle couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/vehicles/{id}"], consumes = ["application/json", "application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateVehicle(
        @PathVariable(value = "id", required = false) id: Long,
        @RequestBody vehicle: Vehicle
    ): ResponseEntity<Vehicle> {
        log.debug("REST request to partial update Vehicle partially : {}, {}", id, vehicle)
        if (vehicle.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, vehicle.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!vehicleRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = vehicleRepository.findById(vehicle.id)
            .map {

                if (vehicle.make != null) {
                    it.make = vehicle.make
                }
                if (vehicle.model != null) {
                    it.model = vehicle.model
                }
                if (vehicle.color != null) {
                    it.color = vehicle.color
                }
                if (vehicle.vin != null) {
                    it.vin = vehicle.vin
                }

                it
            }
            .map { vehicleRepository.save(it) }
            .map {
                vehicleSearchRepository.save(it)

                it
            }

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vehicle.id.toString())
        )
    }

    /**
     * `GET  /vehicles` : get all the vehicles.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the [ResponseEntity] with status `200 (OK)` and the list of vehicles in body.
     */
    @GetMapping("/vehicles")
    fun getAllVehicles(@RequestParam(required = false, defaultValue = "false") eagerload: Boolean): MutableList<Vehicle> {

        log.debug("REST request to get all Vehicles")
        if (eagerload) {
            return vehicleRepository.findAllWithEagerRelationships()
        } else {
            return vehicleRepository.findAll()
        }
    }

    /**
     * `GET  /vehicles/:id` : get the "id" vehicle.
     *
     * @param id the id of the vehicle to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the vehicle, or with status `404 (Not Found)`.
     */
    @GetMapping("/vehicles/{id}")
    fun getVehicle(@PathVariable id: Long): ResponseEntity<Vehicle> {
        log.debug("REST request to get Vehicle : $id")
        val vehicle = vehicleRepository.findOneWithEagerRelationships(id)
        return ResponseUtil.wrapOrNotFound(vehicle)
    }

    @GetMapping("/vehicles/user")
    fun getVehicleByCurrentUser(): ResponseEntity<Vehicle> {
        log.debug("REST request to get Vehicle for user")
        val vehicle: Optional<Vehicle> = Optional.ofNullable(vehicleRepository.findByOwnerIsCurrentUser().firstOrNull())
        return ResponseUtil.wrapOrNotFound(vehicle)
    }

    /**
     *  `DELETE  /vehicles/:id` : delete the "id" vehicle.
     *
     * @param id the id of the vehicle to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/vehicles/{id}")
    fun deleteVehicle(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Vehicle : $id")

        vehicleRepository.deleteById(id)
        vehicleSearchRepository.deleteById(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }

    /**
     * `SEARCH  /_search/vehicles?query=:query` : search for the vehicle corresponding
     * to the query.
     *
     * @param query the query of the vehicle search.
     * @return the result of the search.
     */
    @GetMapping("/_search/vehicles")
    fun searchVehicles(@RequestParam query: String): MutableList<Vehicle> {
        log.debug("REST request to search Vehicles for query $query")
        return vehicleSearchRepository.search(query)
            .collect(Collectors.toList())
            .toMutableList()
    }
}
