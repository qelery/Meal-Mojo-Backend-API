package com.qelery.mealmojo.api.repository;

import com.qelery.mealmojo.api.singletons.DockerContaineredDatabaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RestaurantRepositoryIntegrationTest extends DockerContaineredDatabaseTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    
}