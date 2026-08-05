package com.example.accommodation.repository;

import com.example.accommodation.model.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    Accommodation findWithLockById(Long id);
}
