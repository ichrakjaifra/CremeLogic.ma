package ma.cremelogic.CremeLogic.ma.controller;

import lombok.RequiredArgsConstructor;
import ma.cremelogic.CremeLogic.ma.dto.request.MouvementStockRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.MouvementStockResponse;
import ma.cremelogic.CremeLogic.ma.enums.TypeMouvement;
import ma.cremelogic.CremeLogic.ma.service.interfaces.MouvementStockService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stocks/mouvements")
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class MouvementStockController {

    private final MouvementStockService mouvementStockService;

    @GetMapping({"", "/all"})
    public ResponseEntity<List<MouvementStockResponse>> getAllMouvements() {
        log.info("Appel API: Récupération de tous les mouvements de stock");
        List<MouvementStockResponse> responses = mouvementStockService.getAllMouvements();
        log.info("Récupérés: {} mouvements", responses.size());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/entree")
        public ResponseEntity<MouvementStockResponse> enregistrerEntree(@RequestBody MouvementStockRequest request) {
                return ResponseEntity.ok(mouvementStockService.enregistrerEntree(
                                request.getIngredientId(),
                                request.getQuantite(),
                                request.getCoutUnitaire(),
                                request.getRaison(),
                                request.getCommandeId()));
        }

        @PostMapping("/sortie")
        public ResponseEntity<MouvementStockResponse> enregistrerSortie(@RequestBody MouvementStockRequest request) {
                return ResponseEntity.ok(mouvementStockService.enregistrerSortie(
                                request.getIngredientId(),
                                request.getQuantite(),
                                request.getRaison(),
                                request.getOrdreProductionId()));
        }

        @PostMapping("/perte")
        public ResponseEntity<MouvementStockResponse> enregistrerPerte(@RequestBody MouvementStockRequest request) {
                return ResponseEntity.ok(mouvementStockService.enregistrerPerte(
                                request.getIngredientId(),
                                request.getQuantite(),
                                request.getRaison()));
        }

        @PostMapping("/ajustement")
        public ResponseEntity<MouvementStockResponse> enregistrerAjustement(
                        @RequestBody MouvementStockRequest request) {
                return ResponseEntity.ok(mouvementStockService.enregistrerAjustement(
                                request.getIngredientId(),
                                request.getQuantite(),
                                request.getRaison()));
        }

        @GetMapping("/ingredient/{id}")
        public ResponseEntity<List<MouvementStockResponse>> getMouvementsParIngredient(@PathVariable Long id) {
                return ResponseEntity.ok(mouvementStockService.getMouvementsParIngredient(id));
        }

        @GetMapping("/periode")
        public ResponseEntity<List<MouvementStockResponse>> getMouvementsParPeriode(
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
                return ResponseEntity.ok(mouvementStockService.getMouvementsParPeriode(debut, fin));
        }

        @GetMapping("/type/{type}")
        public ResponseEntity<List<MouvementStockResponse>> getMouvementsParType(@PathVariable TypeMouvement type) {
                return ResponseEntity.ok(mouvementStockService.getMouvementsParType(type));
        }

        @GetMapping("/statistiques/{ingredientId}")
        public ResponseEntity<Map<String, java.math.BigDecimal>> getStatistiques(
                        @PathVariable Long ingredientId,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
                return ResponseEntity.ok(mouvementStockService.getStatistiquesConsommation(ingredientId, debut, fin));
        }

        @GetMapping("/historique/{ingredientId}")
        public ResponseEntity<Map<String, Object>> getHistorique(
                        @PathVariable Long ingredientId,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
                return ResponseEntity.ok(mouvementStockService.getHistoriqueComplet(ingredientId, debut, fin));
        }
}