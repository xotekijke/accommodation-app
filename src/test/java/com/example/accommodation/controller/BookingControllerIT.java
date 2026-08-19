package com.example.accommodation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.accommodation.model.enums.Role;
import com.example.accommodation.repository.UserRepository;
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
class BookingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private String customerToken;
    private Long accommodationId;

    @BeforeEach
    void setUp() throws Exception {
        customerToken = registerAndLogin("booking.customer@example.com");

        String managerToken = registerAndLogin("booking.manager@example.com");
        var manager = userRepository.findByEmail("booking.manager@example.com").orElseThrow();
        manager.setRole(Role.MANAGER);
        userRepository.save(manager);
        managerToken = loginOnly("booking.manager@example.com");

        String response = mockMvc.perform(post("/accommodations")
                        .header("Authorization", "Bearer " + managerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type":"HOUSE","location":"Kyiv","size":"2 rooms",
                                 "amenities":["WiFi"],"dailyRate":100,"availability":3}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        accommodationId = objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(accommodationId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void create_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(accommodationId)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create_nonExistentAccommodation_returns404() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(999999L)))
                .andExpect(status().isNotFound());
    }

    @Test
    void findMyBookings_returnsOnlyOwnBookings() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(accommodationId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/bookings/my")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void search_asCustomer_returns403() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }

    private String bookingJson(Long accommodationId) {
        return String.format("""
                {"checkInDate":"2026-09-01","checkOutDate":"2026-09-05",
                 "accommodationId":%d}
                """, accommodationId);
    }

    private String registerAndLogin(String email) throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {"email":"%s","password":"password123",
                                 "firstName":"Test","lastName":"User"}
                                """, email)))
                .andExpect(status().isCreated());
        return loginOnly(email);
    }

    private String loginOnly(String email) throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
                                {"email":"%s","password":"password123"}
                                """, email)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }
}
