package vg.Brando.Flores.hackathon.repository;  // Capitalizado exacto

import vg.Brando.Flores.hackathon.model.Corresponsal;  // Import capitalizado
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CorresponsalRepository extends JpaRepository<Corresponsal, Integer> {

    @Query("SELECT c FROM Corresponsal c WHERE c.isActive = true AND c.deletedAt IS NULL")
    List<Corresponsal> findByActive();

    @Query("SELECT c FROM Corresponsal c WHERE c.isActive = false AND c.deletedAt IS NULL")
    List<Corresponsal> findByInactive();
}