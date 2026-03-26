package com.sitnik.warhammer.rosterbuilderapi.repository;

import com.sitnik.warhammer.rosterbuilderapi.entity.Faction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FactionRepository extends JpaRepository<Faction, String> {

    List<Faction> findFactionsByNameContainsIgnoreCase(String name);
}
