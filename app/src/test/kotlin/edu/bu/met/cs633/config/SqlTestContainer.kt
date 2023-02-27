package edu.bu.met.cs633.config

import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.testcontainers.containers.JdbcDatabaseContainer

interface SqlTestContainer : InitializingBean, DisposableBean {
    fun getTestContainer(): JdbcDatabaseContainer<*>
}
