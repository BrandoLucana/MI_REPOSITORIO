package vg.Brando.Flores.hackathon.rest;  // Capitalizado exacto

import vg.Brando.Flores.hackathon.model.Corresponsal;  // Capitalizado
import vg.Brando.Flores.hackathon.service.CorresponsalService;  // Capitalizado
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/corresponsales")
@CrossOrigin(origins = "*")
public class CorresponsalController {

    @Autowired
    private CorresponsalService service;

    @GetMapping("/activos")
    public List<Corresponsal> getActivos() {
        return service.listActivos();
    }

    @GetMapping
    public List<Corresponsal> getAll() {
        return service.listAll();
    }

    @PostMapping
    public Corresponsal create(@RequestBody Corresponsal corresponsal) {
        return service.save(corresponsal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Corresponsal> update(@PathVariable Integer id, @RequestBody Corresponsal updated) {
        Corresponsal result = service.update(id, updated);
        if (result != null) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.softDelete(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Integer id) {
        service.restore(id);
        return ResponseEntity.ok().build();
    }
}