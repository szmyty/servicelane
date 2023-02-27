package edu.bu.met.cs633.repository

import edu.bu.met.cs633.domain.Appointment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Spring Data JPA repository for the Appointment entity.
 */
@Repository
interface AppointmentRepository : JpaRepository<Appointment, Long> {

    @Query("select appointment from Appointment appointment where appointment.client.login = ?#{principal.preferredUsername}")
    fun findByClientIsCurrentUser(): MutableList<Appointment>

    @JvmDefault fun findOneWithEagerRelationships(id: Long): Optional<Appointment> {
        return this.findOneWithToOneRelationships(id)
    }

    @JvmDefault fun findAllWithEagerRelationships(): MutableList<Appointment> {
        return this.findAllWithToOneRelationships()
    }

    @JvmDefault fun findAllWithEagerRelationships(pageable: Pageable): Page<Appointment> {
        return this.findAllWithToOneRelationships(pageable)
    }

    @Query(
        value = "select distinct appointment from Appointment appointment left join fetch appointment.client",
        countQuery = "select count(distinct appointment) from Appointment appointment"
    )
    fun findAllWithToOneRelationships(pageable: Pageable): Page<Appointment>

    @Query("select distinct appointment from Appointment appointment left join fetch appointment.client")
    fun findAllWithToOneRelationships(): MutableList<Appointment>

    @Query("select appointment from Appointment appointment left join fetch appointment.client where appointment.id =:id")
    fun findOneWithToOneRelationships(@Param("id") id: Long): Optional<Appointment>
}
