package com.kdob.piq.configserver

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.config.server.environment.NativeEnvironmentRepository
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(properties = ["spring.profiles.active=native"])
class ConfigServerApplicationTests {

    @Autowired
    private lateinit var repository: NativeEnvironmentRepository

    @Test
    fun contextLoads() {
        assertNotNull(repository)
    }

    @Test
    fun shouldFindConfigForAuthServer() {
        val environment = repository.findOne("auth-server", "local", null)
        assertNotNull(environment)
        assertEquals("auth-server", environment.name)
        assertTrue(environment.propertySources.isNotEmpty(), "Property sources should not be empty")
        
        val propertySourceNames = environment.propertySources.map { it.name }
        assertTrue(propertySourceNames.any { it.contains("auth-server-local.yaml") || it.contains("auth-server.yaml") }, 
            "Should contain auth-server config")
    }
}

private fun assertEquals(expected: Any?, actual: Any?) {
    kotlin.test.assertEquals(expected, actual)
}
