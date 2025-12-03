package vg.Brando.Flores.hackathon.service;  // Capitalizado exacto

import vg.Brando.Flores.hackathon.model.Corresponsal;  // Capitalizado
import vg.Brando.Flores.hackathon.repository.CorresponsalRepository;  // Capitalizado
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CorresponsalService {

    @Autowired
    private CorresponsalRepository repository;

    public Corresponsal save(Corresponsal corresponsal) {
        return repository.save(corresponsal);
    }

    public List<Corresponsal> listActivos() {
        return repository.findByActive();
    }

    public List<Corresponsal> listAll() {
        return repository.findAll();
    }

    public Corresponsal update(Integer id, Corresponsal updated) {
        Optional<Corresponsal> existing = repository.findById(id);
        if (existing.isPresent()) {
            Corresponsal c = existing.get();
            c.setFullName(updated.getFullName());
            c.setNumeroDocumento(updated.getNumeroDocumento());
            c.setUbigeo(updated.getUbigeo());
            c.setCentroPoblado(updated.getCentroPoblado());
            c.setIsActive(updated.getIsActive());
            c.setUpdatedAt(LocalDateTime.now());
            return repository.save(c);
        }
        return null;
    }

    public void softDelete(Integer id) {
        Optional<Corresponsal> existing = repository.findById(id);
        if (existing.isPresent()) {
            Corresponsal c = existing.get();
            c.setIsActive(false);
            c.setDeletedAt(LocalDateTime.now());
            repository.save(c);
        }
    }

    public void restore(Integer id) {
        Optional<Corresponsal> existing = repository.findById(id);
        if (existing.isPresent()) {
            Corresponsal c = existing.get();
            c.setIsActive(true);
            c.setDeletedAt(null);
            c.setUpdatedAt(LocalDateTime.now());
            repository.save(c);
        }
    }
}