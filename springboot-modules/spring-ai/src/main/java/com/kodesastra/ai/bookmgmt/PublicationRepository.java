package com.kodesastra.ai.bookmgmt;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kodesastra.ai.bookmgmt.entity.Publication;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Integer> {
    Optional<Publication> findByName(String name);

}
