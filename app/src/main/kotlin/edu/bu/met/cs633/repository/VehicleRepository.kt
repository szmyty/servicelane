package edu.bu.met.cs633.repository

import edu.bu.met.cs633.domain.Vehicle
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Spring Data JPA repository for the Vehicle entity.
 */
@Repository
interface VehicleRepository : JpaRepository<Vehicle, Long> {

    @Query("select vehicle from Vehicle vehicle where vehicle.owner.login = ?#{principal.preferredUsername}")
    fun findByOwnerIsCurrentUser(): MutableList<Vehicle>

    @JvmDefault fun findOneWithEagerRelationships(id: Long): Optional<Vehicle> {
        return this.findOneWithToOneRelationships(id)
    }

    @JvmDefault fun findAllWithEagerRelationships(): MutableList<Vehicle> {
        return this.findAllWithToOneRelationships()
    }

    @JvmDefault fun findAllWithEagerRelationships(pageable: Pageable): Page<Vehicle> {
        return this.findAllWithToOneRelationships(pageable)
    }

    @Query(
        value = "select distinct vehicle from Vehicle vehicle left join fetch vehicle.owner",
        countQuery = "select count(distinct vehicle) from Vehicle vehicle"
    )
    fun findAllWithToOneRelationships(pageable: Pageable): Page<Vehicle>

    @Query("select distinct vehicle from Vehicle vehicle left join fetch vehicle.owner")
    fun findAllWithToOneRelationships(): MutableList<Vehicle>

    @Query("select vehicle from Vehicle vehicle left join fetch vehicle.owner where vehicle.id =:id")
    fun findOneWithToOneRelationships(@Param("id") id: Long): Optional<Vehicle>
}
