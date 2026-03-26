package com.sitnik.warhammer.rosterbuilderapi.repository;

import com.sitnik.warhammer.rosterbuilderapi.entity.Roster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RosterRepository extends JpaRepository<Roster, String> {

}
