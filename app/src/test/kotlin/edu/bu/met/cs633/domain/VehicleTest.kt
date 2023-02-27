package edu.bu.met.cs633.domain

import edu.bu.met.cs633.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class VehicleTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Vehicle::class)
        val vehicle1 = Vehicle()
        vehicle1.id = 1L
        val vehicle2 = Vehicle()
        vehicle2.id = vehicle1.id
        assertThat(vehicle1).isEqualTo(vehicle2)
        vehicle2.id = 2L
        assertThat(vehicle1).isNotEqualTo(vehicle2)
        vehicle1.id = null
        assertThat(vehicle1).isNotEqualTo(vehicle2)
    }
}
