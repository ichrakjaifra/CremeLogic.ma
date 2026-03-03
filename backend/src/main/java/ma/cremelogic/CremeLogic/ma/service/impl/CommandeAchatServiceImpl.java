package ma.cremelogic.CremeLogic.ma.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.cremelogic.CremeLogic.ma.dto.request.CommandeAchatRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.LigneCommandeRequest;
import ma.cremelogic.CremeLogic.ma.dto.request.ReceptionCommandeRequest;
import ma.cremelogic.CremeLogic.ma.dto.response.CommandeAchatResponse;
import ma.cremelogic.CremeLogic.ma.dto.response.LigneCommandeResponse;
import ma.cremelogic.CremeLogic.ma.entity.*;
import ma.cremelogic.CremeLogic.ma.enums.StatutCommande;
import ma.cremelogic.CremeLogic.ma.exception.ResourceNotFoundException;
import ma.cremelogic.CremeLogic.ma.exception.ValidationException;
import ma.cremelogic.CremeLogic.ma.repository.*;
import ma.cremelogic.CremeLogic.ma.service.interfaces.AlerteService;
import ma.cremelogic.CremeLogic.ma.service.interfaces.CommandeAchatService;
import ma.cremelogic.CremeLogic.ma.service.interfaces.MouvementStockService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandeAchatServiceImpl implements CommandeAchatService {

    private final CommandeAchatRepository commandeAchatRepository;
    private final FournisseurRepository fournisseurRepository;
    private final IngredientRepository ingredientRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MouvementStockService mouvementStockService;
    private final AlerteService alerteService;
    private final HistoriqueService historiqueService;

    @Override
    @Transactional
    public CommandeAchatResponse createCommande(CommandeAchatRequest request) {
        Fournisseur fournisseur = fournisseurRepository.findById(request.getFournisseurId())
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "id", request.getFournisseurId()));

        Utilisateur createur = getCurrentUser();

        CommandeAchat commande = CommandeAchat.builder()
                .fournisseur(fournisseur)
                .dateLivraisonPrevue(request.getDateLivraisonPrevue())
                .notes(request.getNotes())
                .createur(createur)
                .statut(StatutCommande.EN_ATTENTE)
                .build();

        // Ajouter les lignes de commande
        for (LigneCommandeRequest ligneRequest : request.getLignesCommande()) {
            Ingredient ingredient = ingredientRepository.findById(ligneRequest.getIngredientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ingrédient", "id", ligneRequest.getIngredientId()));

            LigneCommande ligne = LigneCommande.builder()
                    .commande(commande)
                    .ingredient(ingredient)
                    .quantiteCommandee(ligneRequest.getQuantiteCommandee())
                    .quantiteRecue(BigDecimal.ZERO)
                    .prixUnitaire(ligneRequest.getPrixUnitaire())
                    .build();

            commande.getLignesCommande().add(ligne);
        }

        commande.calculerMontantTotal();
        CommandeAchat saved = commandeAchatRepository.save(commande);

        // Historique
        historiqueService.enregistrerCreation("COMMANDE_ACHAT", saved.getId(),
                "Création de la commande: " + saved.getNumeroCommande() +
                        " pour " + fournisseur.getNom());

        log.info("Commande d'achat créée: {} pour {}", saved.getNumeroCommande(), fournisseur.getNom());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CommandeAchatResponse updateCommande(Long id, CommandeAchatRequest request) {
        CommandeAchat commande = commandeAchatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", "id", id));

        if (commande.getStatut() != StatutCommande.EN_ATTENTE) {
            throw new ValidationException("Impossible de modifier une commande qui n'est pas en attente");
        }

        Fournisseur fournisseur = fournisseurRepository.findById(request.getFournisseurId())
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "id", request.getFournisseurId()));

        commande.setFournisseur(fournisseur);
        commande.setDateLivraisonPrevue(request.getDateLivraisonPrevue());
        commande.setNotes(request.getNotes());

        // Mettre à jour les lignes
        commande.getLignesCommande().clear();

        for (LigneCommandeRequest ligneRequest : request.getLignesCommande()) {
            Ingredient ingredient = ingredientRepository.findById(ligneRequest.getIngredientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ingrédient", "id", ligneRequest.getIngredientId()));

            LigneCommande ligne = LigneCommande.builder()
                    .commande(commande)
                    .ingredient(ingredient)
                    .quantiteCommandee(ligneRequest.getQuantiteCommandee())
                    .quantiteRecue(BigDecimal.ZERO)
                    .prixUnitaire(ligneRequest.getPrixUnitaire())
                    .build();

            commande.getLignesCommande().add(ligne);
        }

        commande.calculerMontantTotal();
        CommandeAchat updated = commandeAchatRepository.save(commande);

        historiqueService.enregistrerModification("COMMANDE_ACHAT", id,
                "Mise à jour de la commande");

        return mapToResponse(updated);
    }

    @Override
    public CommandeAchatResponse getCommande(Long id) {
        CommandeAchat commande = commandeAchatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", "id", id));
        return mapToResponse(commande);
    }

    @Override
    public List<CommandeAchatResponse> getAllCommandes() {
        return commandeAchatRepository.findAllByOrderByDateCreationDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommandeAchatResponse> getCommandesByFournisseur(Long fournisseurId) {
        return commandeAchatRepository.findByFournisseurIdOrderByDateCreationDesc(fournisseurId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommandeAchatResponse> getCommandesByStatut(String statut) {
        try {
            StatutCommande statutEnum = StatutCommande.valueOf(statut.toUpperCase());
            return commandeAchatRepository.findByStatutOrderByDateCreationDesc(statutEnum).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Statut invalide: " + statut);
        }
    }

    @Override
    @Transactional
    public void deleteCommande(Long id) {
        CommandeAchat commande = commandeAchatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", "id", id));

        if (commande.getStatut() != StatutCommande.EN_ATTENTE) {
            throw new ValidationException("Impossible de supprimer une commande qui n'est pas en attente");
        }

        commandeAchatRepository.delete(commande);
        historiqueService.enregistrerSuppression("COMMANDE_ACHAT", id,
                "Suppression de la commande: " + commande.getNumeroCommande());
    }

    @Override
    @Transactional
    public CommandeAchatResponse changerStatut(Long id, String statut) {
        CommandeAchat commande = commandeAchatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", "id", id));

        StatutCommande ancienStatut = commande.getStatut();
        StatutCommande nouveauStatut;

        try {
            nouveauStatut = StatutCommande.valueOf(statut.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Statut invalide: " + statut);
        }

        commande.setStatut(nouveauStatut);
        CommandeAchat updated = commandeAchatRepository.save(commande);

        historiqueService.enregistrerModification("COMMANDE_ACHAT", id,
                String.format("Changement statut: %s -> %s", ancienStatut, nouveauStatut));

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public CommandeAchatResponse recevoirCommande(Long id, ReceptionCommandeRequest request) {
        CommandeAchat commande = commandeAchatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", "id", id));

        if (commande.getStatut() == StatutCommande.LIVREE) {
            throw new ValidationException("Cette commande est déjà livrée");
        }

        commande.setDateLivraisonReelle(LocalDate.parse(request.getDateLivraisonReelle()));

        // Traiter chaque ligne reçue
        for (ReceptionLigne reception : request.getReceptions()) {
            LigneCommande ligne = commande.getLignesCommande().stream()
                    .filter(l -> l.getId().equals(reception.getLigneId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Ligne de commande", "id", reception.getLigneId()));

            BigDecimal quantiteRecue = reception.getQuantiteRecue();

            if (quantiteRecue.compareTo(ligne.getQuantiteCommandee().subtract(ligne.getQuantiteRecue())) > 0) {
                throw new ValidationException("Quantité reçue supérieure à la quantité restante pour l'ingrédient: " +
                        ligne.getIngredient().getNom());
            }

            ligne.setQuantiteRecue(ligne.getQuantiteRecue().add(quantiteRecue));

            // Enregistrer l'entrée en stock
            mouvementStockService.enregistrerEntree(
                    ligne.getIngredient().getId(),
                    quantiteRecue,
                    ligne.getPrixUnitaire(),
                    "Réception commande: " + commande.getNumeroCommande(),
                    commande.getId()
            );
        }

        // Vérifier si toutes les lignes sont complètement reçues
        boolean toutesRecues = commande.getLignesCommande().stream()
                .allMatch(l -> l.getQuantiteRecue().compareTo(l.getQuantiteCommandee()) >= 0);

        if (toutesRecues) {
            commande.setStatut(StatutCommande.LIVREE);
        } else {
            commande.setStatut(StatutCommande.EN_COURS);
        }

        commande.calculerMontantTotal();
        CommandeAchat updated = commandeAchatRepository.save(commande);

        historiqueService.enregistrerModification("COMMANDE_ACHAT", id,
                "Réception partielle de la commande");

        log.info("Réception enregistrée pour la commande {}", commande.getNumeroCommande());
        return mapToResponse(updated);
    }

    @Override
    public List<CommandeAchatResponse> getCommandesEnRetard() {
        return commandeAchatRepository.findCommandesEnRetard().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getMontantTotalCommandesPeriode(LocalDate debut, LocalDate fin) {
        Double montant = commandeAchatRepository.getMontantTotalCommandesPeriode(debut, fin);
        return montant != null ? BigDecimal.valueOf(montant) : BigDecimal.ZERO;
    }

    @Override
    @Transactional
    public CommandeAchatResponse annulerCommande(Long id, String raison) {
        CommandeAchat commande = commandeAchatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", "id", id));

        if (commande.getStatut() == StatutCommande.LIVREE) {
            throw new ValidationException("Impossible d'annuler une commande déjà livrée");
        }

        commande.setStatut(StatutCommande.ANNULEE);
        commande.setNotes(commande.getNotes() + "\nANNULATION: " + raison);

        CommandeAchat updated = commandeAchatRepository.save(commande);

        // Créer une alerte pour l'annulation
        alerteService.creerAlerteCommandeAnnulee(commande, raison);

        historiqueService.enregistrerModification("COMMANDE_ACHAT", id,
                "Annulation de la commande: " + raison);

        log.info("Commande annulée: {} - Raison: {}", commande.getNumeroCommande(), raison);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public CommandeAchatResponse dupliquerCommande(Long id) {
        CommandeAchat commandeOriginale = commandeAchatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", "id", id));

        Utilisateur createur = getCurrentUser();

        CommandeAchat nouvelleCommande = CommandeAchat.builder()
                .fournisseur(commandeOriginale.getFournisseur())
                .dateLivraisonPrevue(commandeOriginale.getDateLivraisonPrevue())
                .notes("DUPLICATA de " + commandeOriginale.getNumeroCommande() + "\n" + commandeOriginale.getNotes())
                .createur(createur)
                .statut(StatutCommande.EN_ATTENTE)
                .build();

        // Copier les lignes
        for (LigneCommande ligneOriginale : commandeOriginale.getLignesCommande()) {
            LigneCommande ligne = LigneCommande.builder()
                    .commande(nouvelleCommande)
                    .ingredient(ligneOriginale.getIngredient())
                    .quantiteCommandee(ligneOriginale.getQuantiteCommandee())
                    .quantiteRecue(BigDecimal.ZERO)
                    .prixUnitaire(ligneOriginale.getPrixUnitaire())
                    .build();

            nouvelleCommande.getLignesCommande().add(ligne);
        }

        nouvelleCommande.calculerMontantTotal();
        CommandeAchat saved = commandeAchatRepository.save(nouvelleCommande);

        historiqueService.enregistrerCreation("COMMANDE_ACHAT", saved.getId(),
                "Duplication de la commande: " + commandeOriginale.getNumeroCommande());

        return mapToResponse(saved);
    }

    private Utilisateur getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));
    }

    private CommandeAchatResponse mapToResponse(CommandeAchat commande) {
        List<LigneCommandeResponse> lignes = commande.getLignesCommande().stream()
                .map(l -> LigneCommandeResponse.builder()
                        .id(l.getId())
                        .commandeId(commande.getId())
                        .ingredientId(l.getIngredient().getId())
                        .ingredientNom(l.getIngredient().getNom())
                        .ingredientCode(l.getIngredient().getCodeIngredient())
                        .quantiteCommandee(l.getQuantiteCommandee())
                        .quantiteRecue(l.getQuantiteRecue())
                        .prixUnitaire(l.getPrixUnitaire())
                        .montantTotal(l.getMontantTotal())
                        .uniteMesure(l.getIngredient().getUniteMesure().getSymbole())
                        .build())
                .collect(Collectors.toList());

        return CommandeAchatResponse.builder()
                .id(commande.getId())
                .numeroCommande(commande.getNumeroCommande())
                .fournisseurId(commande.getFournisseur().getId())
                .fournisseurNom(commande.getFournisseur().getNom())
                .dateCommande(commande.getDateCommande())
                .dateLivraisonPrevue(commande.getDateLivraisonPrevue())
                .dateLivraisonReelle(commande.getDateLivraisonReelle())
                .statut(commande.getStatut())
                .notes(commande.getNotes())
                .montantTotal(commande.getMontantTotal())
                .createurId(commande.getCreateur() != null ? commande.getCreateur().getId() : null)
                .createurNom(commande.getCreateur() != null ?
                        commande.getCreateur().getNom() + " " + commande.getCreateur().getPrenom() : null)
                .dateCreation(commande.getDateCreation())
                .dateModification(commande.getDateModification())
                .lignesCommande(lignes)
                .enRetard(commande.estEnRetard())
                .build();
    }
}