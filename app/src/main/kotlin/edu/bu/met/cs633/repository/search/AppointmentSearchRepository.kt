package edu.bu.met.cs633.repository.search

import edu.bu.met.cs633.domain.Appointment
import edu.bu.met.cs633.repository.AppointmentRepository
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import java.util.stream.Stream

/**
 * Spring Data Elasticsearch repository for the [Appointment] entity.
 */
interface AppointmentSearchRepository : ElasticsearchRepository<Appointment, Long>, AppointmentSearchRepositoryInternal

interface AppointmentSearchRepositoryInternal {
    fun search(query: String): Stream<Appointment>

    fun search(query: Query): Stream<Appointment>

    fun index(entity: Appointment)
}

class AppointmentSearchRepositoryInternalImpl(
    val elasticsearchTemplate: ElasticsearchRestTemplate,
    val repository: AppointmentRepository
) : AppointmentSearchRepositoryInternal {

    override fun search(query: String): Stream<Appointment> {
        val nativeSearchQuery = NativeSearchQuery(queryStringQuery(query))
        return search(nativeSearchQuery)
    }

    override fun search(query: Query): Stream<Appointment> {
        return elasticsearchTemplate
            .search(query, Appointment::class.java)
            .map(SearchHit<Appointment>::getContent)
            .stream()
    }

    override fun index(entity: Appointment) {
        entity.id?.let {
            repository.findOneWithEagerRelationships(it).ifPresent(elasticsearchTemplate::save)
        }
    }
}
