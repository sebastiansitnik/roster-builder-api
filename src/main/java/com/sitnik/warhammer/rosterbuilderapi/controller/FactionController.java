package com.sitnik.warhammer.rosterbuilderapi.controller;

import com.sitnik.warhammer.rosterbuilderapi.entity.Faction;
import com.sitnik.warhammer.rosterbuilderapi.repository.FactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/factions")
public class FactionController {

    private final FactionRepository factionRepository;

    @GetMapping
    public ResponseEntity<List<Faction>> findAll() {
        List<Faction> factions = factionRepository.findAll();
        return ResponseEntity.ok(factions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faction> findById(@PathVariable long id) {
        Optional<Faction> faction = factionRepository.findById(Long.toString(id));
        return faction.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Faction> create(@Valid @RequestBody Faction faction) {
        Faction saved = factionRepository.save(faction);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Faction>> bulk(@RequestBody List<Faction> factions) {
        factionRepository.saveAll(factions);
        return ResponseEntity.ok(factions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Faction> update(@PathVariable Long id, @Valid @RequestBody Faction faction) {
        if (!factionRepository.existsById(Long.toString(id))) {
            return ResponseEntity.notFound().build();
        }
        faction.setId(id);
        return ResponseEntity.ok(factionRepository.save(faction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        if (factionRepository.existsById(Long.toString(id))) {
            factionRepository.deleteById(Long.toString(id));
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Faction>> searchByName(@RequestParam String name) {
        List<Faction> factions = factionRepository.findFactionsByNameContainsIgnoreCase(name);
        if (factions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(factions);
    }
}
