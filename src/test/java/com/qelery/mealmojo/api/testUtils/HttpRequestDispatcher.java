package com.qelery.mealmojo.api.testUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A wrapper class for common Spring's MockMvc actions.
 *
 */
@Component
public class HttpRequestDispatcher {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public HttpRequestDispatcher(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public String performGET(String url) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performGET(String url, ResultMatcher expectedStatus) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performGET(String url, int expectedStatus) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        get(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performPOST(String url, Object requestBody) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                post(url).accept(MediaType.APPLICATION_JSON)
                        .content(asJson(requestBody))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performPOST(String url, Object requestBody, ResultMatcher expectedStatus) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        post(url).accept(MediaType.APPLICATION_JSON)
                                .content(asJson(requestBody))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performPOST(String url, Object requestBody, int expectedStatus) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        post(url).accept(MediaType.APPLICATION_JSON)
                                .content(asJson(requestBody))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performPOST(String url, ResultMatcher expectedStatus) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        post(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performPOST(String url, int expectedStatus) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        post(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performPUT(String url, Object requestBody) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        put(url).accept(MediaType.APPLICATION_JSON)
                                .content(asJson(requestBody))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performPUT(String url, Object requestBody, ResultMatcher expectedStatus) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        put(url).accept(MediaType.APPLICATION_JSON)
                                .content(asJson(requestBody))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performPUT(String url, Object requestBody, int expectedStatus) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        put(url).accept(MediaType.APPLICATION_JSON)
                                .content(asJson(requestBody))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performPATCH(String url) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        patch(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performPATCH(String url, int expectedStatus) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        patch(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performPATCH(String url, ResultMatcher expectedStatus) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        patch(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performPATCH(String url, Object requestBody) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        patch(url).accept(MediaType.APPLICATION_JSON)
                                .content(asJson(requestBody))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performPATCH(String url, Object requestBody, ResultMatcher expectedStatus) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        patch(url).accept(MediaType.APPLICATION_JSON)
                                .content(asJson(requestBody))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performPATCH(String url, Object requestBody, int expectedStatus) throws Exception {
        asJson(requestBody);
        MockHttpServletResponse response = mockMvc.perform(
                        patch(url).accept(MediaType.APPLICATION_JSON)
                                .content(asJson(requestBody))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performDELETE(String url) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        delete(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performDELETE(String url, ResultMatcher expectedStatus) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        delete(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    public String performDELETE(String url, int expectedStatus) throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        delete(url).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andReturn()
                .getResponse();
        return response.getContentAsString();
    }

    String asJson(final Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }
}
