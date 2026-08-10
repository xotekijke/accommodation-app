package com.example.accommodation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AccommodationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String managerToken;
    private String customerToken;

    @BeforeEach
    void setUp() throws Exception {
        managerToken = registerAndLogin("manager@example.com", "MANAGER");
        customerToken = registerAndLogin("customer@example.com", "CUSTOMER");
    }

    @Test
    void findAll_publicEndpoint_returns200() throws Exception {
        mockMvc.perform(get("/accommodations"))
                .andExpect(status().isOk());
    }

    @Test
    void create_asManager_returns201() throws Exception {
        mockMvc.perform(post("/accommodations")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accommodationJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.location").value("Kyiv"));
    }

    @Test
    void create_asCustomer_returns403() throws Exception {
        mockMvc.perform(post("/accommodations")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accommodationJson()))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/accommodations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accommodationJson()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create_invalidBody_returns400() throws Exception {
        mockMvc.perform(post("/accommodations")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_existingId_returns200() throws Exception {
        Long id = createAccommodationAndGetId();

        mockMvc.perform(get("/accommodations/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void findById_missingId_returns404() throws Exception {
        mockMvc.perform(get("/accommodations/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_asManager_returns200() throws Exception {
        Long id = createAccommodationAndGetId();

        String updatedBody = """
                {"type":"HOUSE","location":"Lviv","size":"3 rooms",
                 "amenities":["WiFi","Parking"],"dailyRate":150,"availability":5}
                """;

        mockMvc.perform(put("/accommodations/" + id)
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("Lviv"));
    }

    @Test
    void delete_asManager_returns204() throws Exception {
        Long id = createAccommodationAndGetId();

        mockMvc.perform(delete("/accommodations/" + id)
                        .header("Authorization", "Bearer " + managerToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/accommodations/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_asCustomer_returns403() throws Exception {
        Long id = createAccommodationAndGetId();

        mockMvc.perform(delete("/accommodations/" + id)
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }

    private Long createAccommodationAndGetId() throws Exception {
        String response = mockMvc.perform(post("/accommodations")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accommodationJson()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private String accommodationJson() {
        return """
                {"type":"HOUSE","location":"Kyiv","size":"2 rooms",
                 "amenities":["WiFi"],"dailyRate":100,"availability":3}
                """;
    }

    private String registerAndLogin(String email, String role) throws Exception {
        String registerBody = String.format("""
                {"email":"%s","password":"password123",
                 "firstName":"Test","lastName":"User"}
                """, email);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        String loginBody = String.format("""
                {"email":"%s","password":"password123"}
                """, email);
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(response);
        return node.get("token").asText();
    }
}
