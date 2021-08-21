package com.qelery.mealmojo.api.testUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.qelery.mealmojo.api.exception.global.ErrorResponseBody;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomAssertions {

    public static void assertContainsErrorMessage(String jsonResponse, String expectedErrorMessage) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ErrorResponseBody responseBody = objectMapper.readValue(jsonResponse, ErrorResponseBody.class);
        assertEquals(expectedErrorMessage, responseBody.getMessage());
    }
}
