package com.sitnik.warhammer.rosterbuilderapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitnik.warhammer.rosterbuilderapi.entity.Faction;
import com.sitnik.warhammer.rosterbuilderapi.service.FactionService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class FactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FactionService factionService;

    @Test
    void findAllReturnsEmptyList() throws Exception {
        // When
        when(factionService.findAll()).thenReturn(List.of());

        // Then
        mockMvc.perform(get("/api/factions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void findAllReturnsListWithFactions() throws Exception {
        // Given
        Faction marines = new Faction(1L, "Space Marines", "Ultramarines Chapter");
        Faction orcs = new Faction(2L, "Orcs", "Waaagh!");
        List<Faction> factions = List.of(marines, orcs);

        // When
        when(factionService.findAll()).thenReturn(factions);

        // Then
        mockMvc.perform(get("/api/factions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Space Marines"))
                .andExpect(jsonPath("$[1].name").value("Orcs"));
    }

    @Test
    void findByIdReturnsFaction() throws Exception {
        // Given
        long id = 1L;
        Faction faction = new Faction(id, "Space Marines", "Ultramarines");

        // When
        when(factionService.findById(id)).thenReturn(faction);

        // Then
        mockMvc.perform(get("/api/factions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Space Marines"));
    }

    @Test
    void findByIdReturnsNotFound() throws Exception {
        // Given
        long id = 1L;

        // When
        when(factionService.findById(id)).thenThrow(new EntityNotFoundException("Faction not found"));

        // Then
        mockMvc.perform(get("/api/factions/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReturnsSavedFaction() throws Exception {
        // Given
        Faction inputFaction = new Faction(null, "Chaos Space Marines", "Traitors");
        Faction savedFaction = new Faction(1L, "Chaos Space Marines", "Traitors");

        // When
        when(factionService.create(any(Faction.class))).thenReturn(savedFaction);

        // Then
        mockMvc.perform(post("/api/factions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputFaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Chaos Space Marines"));
    }

    @Test
    void createWithInvalidDataReturnsBadRequest() throws Exception {
        // Given
        Faction invalidFaction = new Faction(null, null, "Description");

        // Then
        mockMvc.perform(post("/api/factions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFaction)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bulkCreatesMultipleFactions() throws Exception {
        // Given
        Faction marines = new Faction(null, "Space Marines", "Ultramarines");
        Faction orks = new Faction(null, "Orks", "Waaagh!");
        List<Faction> input = List.of(marines, orks);

        Faction savedMarines = new Faction(1L, "Space Marines", "Ultramarines");
        Faction savedOrks = new Faction(2L, "Orks", "Waaagh!");
        List<Faction> saved = List.of(savedMarines, savedOrks);

        // When
        when(factionService.bulk(anyList())).thenReturn(saved);

        // Then
        mockMvc.perform(post("/api/factions/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Space Marines"))
                .andExpect(jsonPath("$[1].name").value("Orks"));
    }
    @Test
    void bulkDuplicateReturnsConflict() throws Exception {
        List<Faction> duplicate = List.of(new Faction(1L, "Duplicate", "desc"));

        doThrow(new EntityExistsException("Duplicate factions exist"))
                .when(factionService).bulk(anyList());

        mockMvc.perform(post("/api/factions/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Duplicate factions exist"));
    }

    @Test
    void updateFactionSuccessfully() throws Exception {
        // Given
        Faction updatedFaction = new Faction(1L, "Ultramarines", "Updated description");

        when(factionService.update(eq(updatedFaction.getId()), any(Faction.class)))  // eq(id)!
                .thenReturn(updatedFaction);

        mockMvc.perform(put("/api/factions/{id}", updatedFaction.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFaction)))
                .andExpect(status().isOk());
    }

    @Test
    void updateNonExistentFactionReturnsNotFound() throws Exception {
        // Given
        Long id = 999L;
        Faction faction = new Faction(id, "Nonexistent", "Test");
        when(factionService.update(id,faction))
                .thenThrow(new EntityNotFoundException("Not found"));

        mockMvc.perform(put("/api/factions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faction)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateExistentFactionWithTooShortDescription() throws Exception {
        // Given
        Long id = 1L;
        Faction invalidFaction = new Faction(id, "Ultramarines", "a");  // @Size(min=3)!

        // Then
        mockMvc.perform(put("/api/factions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFaction)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("size must be between 2 and 500"));  // ControllerAdvice!
    }

    @Test
    void deleteExistingFactionReturnsNoContent() throws Exception {
        long id = 1L;

        mockMvc.perform(delete("/api/factions/{id}", id))
                .andExpect(status().isNoContent());

        verify(factionService).delete(id);
    }

    @Test
    void deleteNonExistentFactionReturnsNotFound() throws Exception {
        long id = 999L;

        doThrow(new EntityNotFoundException("Not found"))
                .when(factionService).delete(id);

        mockMvc.perform(delete("/api/factions/{id}", id))
                .andExpect(status().isNotFound());

        verify(factionService).delete(id);
    }

    @Test
    void searchByNameReturnsResults() throws Exception {
        // Given
        String name = "marines";
        List<Faction> factions = List.of(
                new Faction(1L, "Space Marines", "Elite troops"),
                new Faction(2L, "Blood Marines", "Another faction")
        );

        // When
        when(factionService.searchByNameContaining(name))
                .thenReturn(factions);

        // Then
        mockMvc.perform(get("/api/factions/search")
                        .param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Space Marines"))
                .andExpect(jsonPath("$[1].name").value("Blood Marines"));
    }

    @Test
    void searchByNameReturnsNoContentWhenEmpty() throws Exception {
        String name = "marines";
        List<Faction> factions = List.of(
                new Faction(1L, "Space Marines", "Elite troops"),
                new Faction(2L, "Blood Marines", "Another faction")
        );

        // When
        when(factionService.searchByNameContaining(name))
                .thenReturn(Collections.emptyList());

        // Then
        mockMvc.perform(get("/api/factions/search")
                        .param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}