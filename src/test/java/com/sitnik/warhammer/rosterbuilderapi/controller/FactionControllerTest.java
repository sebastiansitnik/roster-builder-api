package com.sitnik.warhammer.rosterbuilderapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitnik.warhammer.rosterbuilderapi.entity.Faction;
import com.sitnik.warhammer.rosterbuilderapi.repository.FactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
    private FactionRepository factionRepository;

    @Test
    void findAllReturnsEmptyList() throws Exception {
        // When
        when(factionRepository.findAll()).thenReturn(List.of());

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
        when(factionRepository.findAll()).thenReturn(factions);

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
        when(factionRepository.findById(Long.toString(id))).thenReturn(Optional.of(faction));

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
        when(factionRepository.findById(Long.toString(id))).thenReturn(Optional.empty());

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
        when(factionRepository.save(any(Faction.class))).thenReturn(savedFaction);

        // Then
        mockMvc.perform(post("/api/factions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputFaction)))
                .andExpect(status().isOk())
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
                .andExpect(status().isBadRequest());  // 400 (validation @NotNull)
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

        when(factionRepository.saveAll(anyList())).thenReturn(saved);

        // When/Then
        mockMvc.perform(post("/api/factions/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Space Marines"))
                .andExpect(jsonPath("$[1].name").value("Orks"));
    }
// TODO - Need to handle Duplicates
//    @Test
//    void bulkHandlesDuplicateName() throws Exception {
//        // Given
//        Faction existing = new Faction(null, "Space Marines", "Ultramarines");  // Duplikat!
//        Faction newFaction = new Faction(null, "Orks", "Waaagh!");
//        List<Faction> input = List.of(existing, newFaction);
//
//        // When
//        doThrow(new DataIntegrityViolationException("Duplicate name"))
//                .when(factionRepository).saveAll(anyList());
//
//        // Then
//        mockMvc.perform(post("/api/factions/bulk")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(input)))
//                .andExpect(status().isBadRequest());  // 400 lub 409 Conflict
//    }

    @Test
    void updateFactionSuccessfully() throws Exception {
        // Given
        Long id = 1L;
        Faction updatedFaction = new Faction(id, "Ultramarines", "Updated description");
        when(factionRepository.existsById("1"))
                .thenReturn(true);
        when(factionRepository.save(any(Faction.class))).thenReturn(updatedFaction);

        mockMvc.perform(put("/api/factions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFaction)))
                .andExpect(status().isOk());
    }

    @Test
    void updateNonExistentFactionReturnsNotFound() throws Exception {
        // Given
        Long id = 999L;
        Faction faction = new Faction(id, "Nonexistent", "Test");
        when(factionRepository.existsById("999"))  // String!
                .thenReturn(false);

        mockMvc.perform(put("/api/factions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faction)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateExistentFactionWithTooShortDescription() throws Exception {
        // Given
        Long id = 1L;
        Faction invalidFaction = new Faction(id, "Ultramarines", "a");  // @Size(min=3)
        when(factionRepository.existsById("1")).thenReturn(true);

        mockMvc.perform(put("/api/factions/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFaction)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteExistingFactionReturnsOk() throws Exception {
        // Given
        long id = 1L;
        when(factionRepository.existsById("1")).thenReturn(true);
        doNothing().when(factionRepository).deleteById("1");

        // Then
        mockMvc.perform(delete("/api/factions/{id}", id))
                .andExpect(status().isOk());

        verify(factionRepository).deleteById("1");
    }

    @Test
    void deleteNonExistentFactionReturnsNotFound() throws Exception {
        // Given
        long id = 999L;
        when(factionRepository.existsById("999")).thenReturn(false);

        // Then
        mockMvc.perform(delete("/api/factions/{id}", id))
                .andExpect(status().isNotFound());

        verify(factionRepository, never()).deleteById(anyString());
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
        when(factionRepository.findFactionsByNameContainsIgnoreCase(name))
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
        // Given
        String name = "unknown";
        when(factionRepository.findFactionsByNameContainsIgnoreCase(name))
                .thenReturn(List.of());

        // Then
        mockMvc.perform(get("/api/factions/search")
                        .param("name", name))
                .andExpect(status().isNoContent());
    }
}