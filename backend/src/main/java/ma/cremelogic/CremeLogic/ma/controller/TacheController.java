package ma.cremelogic.CremeLogic.ma.controller;

import lombok.RequiredArgsConstructor;
import ma.cremelogic.CremeLogic.ma.entity.Tache;
import ma.cremelogic.CremeLogic.ma.service.TacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taches")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TacheController {

    private final TacheService tacheService;

    @GetMapping
    public List<Tache> getAll() {
        return tacheService.getAll();
    }

    @GetMapping("/utilisateur/{id}")
    public List<Tache> getByUtilisateur(@PathVariable Long id) {
        return tacheService.getByUtilisateur(id);
    }

    @PostMapping
    public Tache create(@RequestBody Tache tache) {
        return tacheService.create(tache);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tache> update(@PathVariable Long id, @RequestBody Tache tache) {
        return ResponseEntity.ok(tacheService.update(id, tache));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<Tache> updateStatut(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(tacheService.updateStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tacheService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
