package edu.bu.met.cs633.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import edu.bu.met.cs633.config.LOGIN_REGEX
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.springframework.data.elasticsearch.annotations.FieldType
import java.io.Serializable
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table
import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * A user.
 */
@Entity
@Table(name = "jhi_user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "user")
class User(

    @Id
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    override var id: String? = null,

    @field:NotNull
    @field:Pattern(regexp = LOGIN_REGEX)
    @field:Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    var login: String? = null,

    @field:Size(max = 50)
    @Column(name = "first_name", length = 50)
    var firstName: String? = null,

    @field:Size(max = 50)
    @Column(name = "last_name", length = 50)
    var lastName: String? = null,

    @field:Email
    @field:Size(min = 5, max = 254)
    @Column(length = 254, unique = true)
    var email: String? = null,

    @field:NotNull
    @Column(nullable = false)
    var activated: Boolean? = false,

    @field:Size(min = 2, max = 10)
    @Column(name = "lang_key", length = 10)
    var langKey: String? = null,

    @field:Size(max = 256)
    @Column(name = "image_url", length = 256)
    var imageUrl: String? = null,

    // @field:NotNull
    @field:Size(max = 256)
    @Column(name = "address", length = 256)
    var address: String? = null,

    // @field:NotNull
    @field:Size(max = 256)
    @Column(name = "postal", length = 256)
    var postal: String? = null,

    // @field:NotNull
    @field:Size(max = 256)
    @Column(name = "city", length = 256)
    var city: String? = null,

    // @field:NotNull
    @field:Size(max = 256)
    @Column(name = "phone", length = 256)
    var phone: String? = null,

    @JsonIgnore

    @ManyToMany
    @JoinTable(
        name = "jhi_user_authority",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "authority_name", referencedColumnName = "name")]
    )

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)

    @BatchSize(size = 20)

    var authorities: MutableSet<Authority> = mutableSetOf(),
    createdBy: String? = null,
    createdDate: Instant? = Instant.now(),
    lastModifiedBy: String? = null,
    lastModifiedDate: Instant? = Instant.now()
) : AbstractAuditingEntity<String>(createdBy, createdDate, lastModifiedBy, lastModifiedDate), Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        if (other.id == null || id == null) return false

        return id == other.id
    }

    override fun hashCode() = 31

    override fun toString() =
        "User{" +
            "login='" + login + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", address='" + address + '\'' +
            ", postal='" + postal + '\'' +
            ", city='" + city + '\'' +
            ", phone='" + phone + '\'' +
            ", imageUrl='" + imageUrl + '\'' +
            ", activated='" + activated + '\'' +
            ", langKey='" + langKey + '\'' +
            "}"

    companion object {
        private const val serialVersionUID = 1L
    }
}
