package com.sitnik.warhammer.rosterbuilderapi.controller;

import com.sitnik.warhammer.rosterbuilderapi.entity.Faction;
import com.sitnik.warhammer.rosterbuilderapi.service.FactionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/factions")
public class FactionController {

    private final FactionService factionService;

    @GetMapping
    @Operation(summary = "Get All Factions")
    public ResponseEntity<List<Faction>> findAll() {
        List<Faction> factions = factionService.findAll();
        return ResponseEntity.ok(factions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find faction by Id")
    public ResponseEntity<Faction> findById(@PathVariable long id) {
        return ResponseEntity.ok(factionService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create Faction")
    public ResponseEntity<Faction> create(@Valid @RequestBody Faction faction) {
        Faction saved = factionService.create(faction);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Create multiple Factions")
    public ResponseEntity<List<Faction>> bulk(@Valid @RequestBody List<Faction> factions) {
        return ResponseEntity.ok(factionService.bulk(factions));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Faction")
    public ResponseEntity<Faction> update(@PathVariable Long id, @Valid @RequestBody Faction faction) {
        return ResponseEntity.ok(factionService.update(id, faction));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Faction by Id")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        factionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search Factions by name")
    public ResponseEntity<List<Faction>> searchByName(@RequestParam String name) {
        return  ResponseEntity.ok(factionService.searchByNameContaining(name));
    }
}
