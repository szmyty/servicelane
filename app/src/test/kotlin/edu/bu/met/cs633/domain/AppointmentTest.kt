package edu.bu.met.cs633.domain

import edu.bu.met.cs633.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AppointmentTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(Appointment::class)
        val appointment1 = Appointment()
        appointment1.id = 1L
        val appointment2 = Appointment()
        appointment2.id = appointment1.id
        assertThat(appointment1).isEqualTo(appointment2)
        appointment2.id = 2L
        assertThat(appointment1).isNotEqualTo(appointment2)
        appointment1.id = null
        assertThat(appointment1).isNotEqualTo(appointment2)
    }
}
