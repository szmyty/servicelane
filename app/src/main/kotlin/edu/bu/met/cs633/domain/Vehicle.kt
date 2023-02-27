package edu.bu.met.cs633.domain

import java.io.Serializable
import javax.persistence.*

/**
 * A Vehicle.
 */

@Entity
@Table(name = "vehicle")

// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "vehicle")
@SuppressWarnings("common-java:DuplicatedBlocks")
data class Vehicle(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "make")
    var make: String? = null,

    @Column(name = "model")
    var model: String? = null,

    @Column(name = "color")
    var color: String? = null,

    @Column(name = "vin")
    var vin: String? = null,

    // jhipster-needle-entity-add-field - JHipster will add fields here
) : Serializable {

    @ManyToOne
    var owner: User? = null

    fun owner(user: User?): Vehicle {
        this.owner = user
        return this
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vehicle) return false
        return id != null && other.id != null && id == other.id
    }

    override fun toString(): String {
        return "Vehicle{" +
            "id=" + id +
            ", make='" + make + "'" +
            ", model='" + model + "'" +
            ", color='" + color + "'" +
            ", vin='" + vin + "'" +
            "}"
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
