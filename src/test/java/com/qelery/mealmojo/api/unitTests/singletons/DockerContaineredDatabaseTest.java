package com.qelery.mealmojo.api.unitTests.singletons;

import org.testcontainers.containers.PostgreSQLContainer;

public class DockerContaineredDatabaseTest {

    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:latest")
            .withDatabaseName("spring-mealmojo-test-db")
            .withUsername("testuser")
            .withPassword("pass");

    static {
        postgreSQLContainer.start();
    }
}

