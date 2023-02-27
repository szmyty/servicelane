package edu.bu.met.cs633

import edu.bu.met.cs633.config.AsyncSyncConfiguration
import edu.bu.met.cs633.config.EmbeddedElasticsearch
import edu.bu.met.cs633.config.EmbeddedRedis
import edu.bu.met.cs633.config.EmbeddedSQL
import edu.bu.met.cs633.config.TestSecurityConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

/**
 * Base composite annotation for integration tests.
 */
@kotlin.annotation.Target(AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@SpringBootTest(classes = [ServicelaneApp::class, AsyncSyncConfiguration::class, TestSecurityConfiguration::class])
@EmbeddedRedis
@EmbeddedElasticsearch
@EmbeddedSQL
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
annotation class IntegrationTest
