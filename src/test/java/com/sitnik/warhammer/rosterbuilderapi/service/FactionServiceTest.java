package com.sitnik.warhammer.rosterbuilderapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitnik.warhammer.rosterbuilderapi.entity.Faction;
import com.sitnik.warhammer.rosterbuilderapi.repository.FactionRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FactionService.class)
@AutoConfigureMockMvc(addFilters = false)
class FactionServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FactionRepository factionRepository;
    @Autowired
    private FactionService factionService;

    @Test
    void findAllReturnsEmptyList() throws Exception {
        // When
        when(factionRepository.findAll()).thenReturn(List.of());

        // Then
        List<Faction> result = factionService.findAll();

        assertThat(result).isEmpty();
        verify(factionRepository).findAll();

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
        List<Faction> result = factionService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Space Marines");
        assertThat(result.get(1).getName()).isEqualTo("Orcs");
        verify(factionRepository).findAll();
    }

    @Test
    void findByIdReturnsFaction() throws Exception {
        // Given
        long id = 1L;
        Faction faction = new Faction(id, "Space Marines", "Ultramarines");

        // When
        when(factionRepository.findById(Long.toString(id))).thenReturn(Optional.of(faction));

        // Then
        Faction result = factionService.findById(id);
        assertThat(result).isEqualTo(faction);
        verify(factionRepository).findById(Long.toString(id));
    }
//
    @Test
    void findByIdThrowsEntityNotFoundWhenFactionDoesNotExist() throws Exception {
        // Given
        long id = 1L;

        // Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> factionService.findById(id)
        );

        assertThat(exception.getMessage())
                .contains("Faction with id " + id + " not found");
        verify(factionRepository).findById(Long.toString(id));
    }
//
    @Test
    void createFactionReturnsSavedFactionWhenNameIsUnique() {
        // Given
        Faction inputFaction = new Faction(null, "Orks", "Waaagh!");
        Faction savedFaction = new Faction(1L, "Orks", "Waaagh!");

        given(factionRepository.existsByName("Orks")).willReturn(false);
        given(factionRepository.save(inputFaction)).willReturn(savedFaction);

        // When
        Faction result = factionService.create(inputFaction);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Orks");
        verify(factionRepository).existsByName("Orks");
        verify(factionRepository).save(inputFaction);
    }

    @Test
    void createFactionWhenNameExistsThrowsEntityExistsException() {
        // Given
        Faction faction = new Faction(null, "Space Marines", "Ultramarines");
        given(factionRepository.existsByName("Space Marines")).willReturn(true);

        // When & Then
        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> factionService.create(faction)
        );

        assertThat(exception.getMessage())
                .contains("Faction with name Space Marines already exists");
        verify(factionRepository).existsByName("Space Marines");
        verify(factionRepository, never()).save(any());
    }

    @Test
    void bulkReturnsSavedFactionsWhenAllNamesUnique() {
        // Given
        List<Faction> inputFactions = List.of(
                new Faction(null, "Orks", "Waaagh!"),
                new Faction(null, "Eldar", "Craftworld")
        );
        List<Faction> savedFactions = List.of(
                new Faction(1L, "Orks", "Waaagh!"),
                new Faction(2L, "Eldar", "Craftworld")
        );

        given(factionRepository.existsByName("Orks")).willReturn(false);
        given(factionRepository.existsByName("Eldar")).willReturn(false);
        given(factionRepository.saveAll(inputFactions)).willReturn(savedFactions);

        // When
        List<Faction> result = factionService.bulk(inputFactions);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Orks");
        verify(factionRepository).existsByName("Orks");
        verify(factionRepository).existsByName("Eldar");
        verify(factionRepository).saveAll(inputFactions);
    }

    @Test
    void bulkThrowsEntityExistsExceptionWhenAnyDuplicateExists() {
        // Given
        List<Faction> factionsWithDuplicate = List.of(
                new Faction(null, "Orks", "Waaagh!")
        );

        given(factionRepository.existsByName("Orks")).willReturn(true);

        // Then
        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> factionService.bulk(factionsWithDuplicate)
        );

        assertThat(exception.getMessage())
                .contains("Factions with name Orks already exists");

        verify(factionRepository).existsByName("Orks");
        verify(factionRepository, never()).saveAll(anyList());
    }

    @Test
    void bulkDetectsMultipleDuplicates() {
        // Given
        List<Faction> factionsWithMultipleDups = List.of(
                new Faction(null, "Orks", ""),
                new Faction(null, "Marines", ""),
                new Faction(null, "Eldar", "")
        );

        given(factionRepository.existsByName("Orks")).willReturn(true);
        given(factionRepository.existsByName("Marines")).willReturn(true);
        given(factionRepository.existsByName("Eldar")).willReturn(false);

        // Then
        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> factionService.bulk(factionsWithMultipleDups)
        );

        assertThat(exception.getMessage())
                .contains("Orks, Marines");
        verify(factionRepository, never()).saveAll(anyList());
    }

    @Test
    void updateFactionReturnsUpdatedFactionWhenExists() {
        // Given
        Long id = 1L;
        Faction inputFaction = new Faction(null, "Ultramarines", "Updated description");
        Faction savedFaction = new Faction(id, "Ultramarines", "Updated description");

        given(factionRepository.existsById("1")).willReturn(true);
        given(factionRepository.save(argThat(f -> f.getId().equals(id))))
                .willReturn(savedFaction);

        // When
        Faction result = factionService.update(id, inputFaction);

        // Then
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Ultramarines");
        verify(factionRepository).existsById("1");
        verify(factionRepository).save(argThat(f -> f.getId().equals(id)));
    }

    @Test
    void updateFactionThrowsEntityNotFoundWhenFactionDoesNotExist() {
        // Given
        Long id = 999L;
        Faction faction = new Faction(null, "Nonexistent", "Test");

        given(factionRepository.existsById("999")).willReturn(false);

        // Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> factionService.update(id, faction)
        );

        assertThat(exception.getMessage())
                .contains("Faction with id " + id + " not found, cannot update");
        verify(factionRepository).existsById("999");
        verify(factionRepository, never()).save(any());
    }

    @Test
    void updateSetsCorrectIdOnFactionBeforeSave() {
        // Given
        Long id = 2L;
        Faction faction = new Faction(null, "Blood Angels", "Updated");

        given(factionRepository.existsById("2")).willReturn(true);
        given(factionRepository.save(any(Faction.class)))
                .willAnswer(invocation -> {
                    Faction saved = invocation.getArgument(0);
                    assertThat(saved.getId()).isEqualTo(id);
                    return saved;
                });

        // Then
        factionService.update(id, faction);

        verify(factionRepository).save(argThat(f -> f.getId().equals(id)));
    }

    @Test
    void deleteFactionWhenFactionExists() {
        // Given
        Long id = 1L;
        given(factionRepository.existsById("1")).willReturn(true);

        // Then
        factionService.delete(id);

        verify(factionRepository).existsById("1");
        verify(factionRepository).deleteById("1");
    }

    @Test
    void deleteFactionThrowsEntityNotFoundWhenFactionDoesNotExist() {
        // Given
        Long id = 999L;
        given(factionRepository.existsById("999")).willReturn(false);

        // Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> factionService.delete(id)
        );

        assertThat(exception.getMessage())
                .contains("Faction with id " + id + " not found, cannot delete");
        verify(factionRepository).existsById("999");
        verify(factionRepository, never()).deleteById(anyString());
    }

    @Test
    void searchByNameContainingReturnsMatchingFactions() {
        // Given
        String name = "marine";
        List<Faction> matchingFactions = List.of(
                new Faction(1L, "Space Marines", "Ultramarines"),
                new Faction(2L, "Blood Marines", "Death Company")
        );

        given(factionRepository.findFactionsByNameContainsIgnoreCase("marine"))
                .willReturn(matchingFactions);

        // Then
        List<Faction> result = factionService.searchByNameContaining("marine");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Faction::getName)
                .contains("Space Marines", "Blood Marines");
        verify(factionRepository).findFactionsByNameContainsIgnoreCase("marine");
    }

    @Test
    void searchByNameContainingReturnsEmptyListWhenNoMatches() {
        // Given
        given(factionRepository.findFactionsByNameContainsIgnoreCase("xyz"))
                .willReturn(List.of());

        // Then
        List<Faction> result = factionService.searchByNameContaining("xyz");

        assertThat(result).isEmpty();
        verify(factionRepository).findFactionsByNameContainsIgnoreCase("xyz");
    }
}