package edu.bu.met.cs633.domain

import java.io.Serializable
import java.time.Instant
import javax.persistence.*
import javax.validation.constraints.*

/**
 * A Appointment.
 */

@Entity
@Table(name = "appointment")

// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "appointment")
@SuppressWarnings("common-java:DuplicatedBlocks")
data class Appointment(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    var id: Long? = null,

    @get: NotNull

    @Column(name = "created", nullable = false)
    var created: Instant? = null,

    @get: NotNull

    @Column(name = "start_time", nullable = false)
    var startTime: Instant? = null,

    @get: NotNull

    @Column(name = "end_time", nullable = false)
    var endTime: Instant? = null,

    @get: NotNull

    @Column(name = "services", nullable = false)
    var services: String? = null,

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    @ManyToOne
    var client: User? = null

    fun client(user: User?): Appointment {
        this.client = user
        return this
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Appointment) return false
        return id != null && other.id != null && id == other.id
    }

    override fun toString(): String {
        return "Appointment{" +
            "id=" + id +
            ", created='" + created + "'" +
            ", startTime='" + startTime + "'" +
            ", endTime='" + endTime + "'" +
            ", services='" + services + "'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
