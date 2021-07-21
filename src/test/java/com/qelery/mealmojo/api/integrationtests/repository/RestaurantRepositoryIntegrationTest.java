package com.qelery.mealmojo.api.integrationtests.repository;

import com.qelery.mealmojo.api.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.qelery.mealmojo.api.unittests.singletons.DockerContaineredDatabaseTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RestaurantRepositoryIntegrationTest extends DockerContaineredDatabaseTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    
}
