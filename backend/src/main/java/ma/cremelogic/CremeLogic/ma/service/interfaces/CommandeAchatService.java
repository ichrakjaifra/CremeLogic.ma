package ma.cremelogic.CremeLogic.ma.service.interfaces;

import ma.cremelogic.CremeLogic.ma.dto.request.CommandeAchatRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.ReceptionCommandeRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.CommandeAchatResponse;
import java.time.LocalDate;
import java.util.List;
import java.math.BigDecimal;

public interface CommandeAchatService {
    CommandeAchatResponse createCommande(CommandeAchatRequest request);

    CommandeAchatResponse updateCommande(Long id, CommandeAchatRequest request);

    CommandeAchatResponse getCommande(Long id);

    List<CommandeAchatResponse> getAllCommandes();

    List<CommandeAchatResponse> getCommandesByFournisseur(Long fournisseurId);

    List<CommandeAchatResponse> getCommandesByStatut(String statut);

    void deleteCommande(Long id);

    CommandeAchatResponse changerStatut(Long id, String statut);

    CommandeAchatResponse recevoirCommande(Long id, ReceptionCommandeRequest request);

    List<CommandeAchatResponse> getCommandesEnRetard();

    BigDecimal getMontantTotalCommandesPeriode(LocalDate debut, LocalDate fin);

    CommandeAchatResponse annulerCommande(Long id, String raison);

    CommandeAchatResponse dupliquerCommande(Long id);
}
