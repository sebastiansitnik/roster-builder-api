package com.sitnik.warhammer.rosterbuilderapi.controller;

import com.sitnik.warhammer.rosterbuilderapi.entity.Faction;
import com.sitnik.warhammer.rosterbuilderapi.repository.FactionRepository;
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
    public List<Faction> findAll() {
        return factionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Faction> findById(@PathVariable long id) {
        return factionRepository.findById(Long.toString(id));
    }

    @PostMapping
    public Faction create(@RequestBody Faction faction) {
        return factionRepository.save(faction);
    }

    @PostMapping("/bulk")
    public List<Faction> bulk(@RequestBody List<Faction> factions) {
        return factionRepository.saveAll(factions);
    }

    @PutMapping("/{id}")
    public Faction update(@PathVariable long id,@RequestBody Faction faction) {
        faction.setId(id);
        return factionRepository.save(faction);
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
    public List<Faction> searchByName(@RequestParam String name) {
        return factionRepository.findFactionsByNameContainsIgnoreCase(name);
    }

}
