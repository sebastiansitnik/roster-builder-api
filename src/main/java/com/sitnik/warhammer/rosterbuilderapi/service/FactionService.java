package com.sitnik.warhammer.rosterbuilderapi.service;

import com.sitnik.warhammer.rosterbuilderapi.entity.Faction;
import com.sitnik.warhammer.rosterbuilderapi.repository.FactionRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FactionService {

    private final FactionRepository factionRepository;

    public List<Faction> findAll() {
        return factionRepository.findAll();
    }

    public Faction findById(Long id) {
        return factionRepository.findById(Long.toString(id))
                .orElseThrow(() -> new EntityNotFoundException("Faction with id " + id + " not found"));
    }

    public Faction create(Faction faction) {
        if (factionRepository.existsByName(faction.getName())){
            throw new EntityExistsException("Faction with name " + faction.getName() + " already exists");
        }
        return factionRepository.save(faction);
    }

    public List<Faction> bulk(List<Faction> factions) {
        List<String> duplicates = factions.stream()
                .map(Faction::getName)
                .filter(factionRepository::existsByName)
                .toList();

        if (!duplicates.isEmpty()) {
            throw new EntityExistsException("Factions with name " + String.join(", ", duplicates) + " already exists" );
        }
        return factionRepository.saveAll(factions);
    }

    public Faction update(Long id, Faction faction) {
        if (!factionRepository.existsById(Long.toString(id))) {
            throw new EntityNotFoundException("Faction with id " + id + " not found, cannot update");
        }
        faction.setId(id);
        return factionRepository.save(faction);
    }

    public void delete(Long id) {
        String idString = Long.toString(id);

        if (!factionRepository.existsById(idString)) {
            throw new EntityNotFoundException("Faction with id " + id + " not found, cannot delete");
        }
        factionRepository.deleteById(idString);
    }

    public List<Faction> searchByNameContaining(String name) {
        return factionRepository.findFactionsByNameContainsIgnoreCase(name);
    }
}
