package edu.bu.met.cs633.repository.search

import edu.bu.met.cs633.domain.Vehicle
import edu.bu.met.cs633.repository.VehicleRepository
import org.elasticsearch.index.query.QueryBuilders.queryStringQuery
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import java.util.stream.Stream

/**
 * Spring Data Elasticsearch repository for the [Vehicle] entity.
 */
interface VehicleSearchRepository : ElasticsearchRepository<Vehicle, Long>, VehicleSearchRepositoryInternal

interface VehicleSearchRepositoryInternal {
    fun search(query: String): Stream<Vehicle>

    fun search(query: Query): Stream<Vehicle>

    fun index(entity: Vehicle)
}

class VehicleSearchRepositoryInternalImpl(
    val elasticsearchTemplate: ElasticsearchRestTemplate,
    val repository: VehicleRepository
) : VehicleSearchRepositoryInternal {

    override fun search(query: String): Stream<Vehicle> {
        val nativeSearchQuery = NativeSearchQuery(queryStringQuery(query))
        return search(nativeSearchQuery)
    }

    override fun search(query: Query): Stream<Vehicle> {
        return elasticsearchTemplate
            .search(query, Vehicle::class.java)
            .map(SearchHit<Vehicle>::getContent)
            .stream()
    }

    override fun index(entity: Vehicle) {
        entity.id?.let {
            repository.findOneWithEagerRelationships(it).ifPresent(elasticsearchTemplate::save)
        }
    }
}
