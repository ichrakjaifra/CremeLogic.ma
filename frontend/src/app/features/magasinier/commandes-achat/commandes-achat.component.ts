import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { CommandeAchatService } from '../../../core/services/commande-achat.service';
import { FournisseurService } from '../../../core/services/fournisseur.service';
import { IngredientService } from '../../../core/services/ingredient.service';
import { NotificationService } from '../../../core/services/notification.service';
import { CommandeAchat, LigneCommandeAchat } from '../../../core/models/commande-achat.model';
import { Fournisseur } from '../../../core/models/fournisseur.model';
import { Ingredient } from '../../../core/models/ingredient.model';
import { FormatPricePipe } from '../../../shared/pipes/format-price.pipe';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { NgxPaginationModule } from 'ngx-pagination';

@Component({
  selector: 'app-commandes-achat',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, FormatPricePipe, EmptyStateComponent, NgxPaginationModule],
  templateUrl: './commandes-achat.component.html',
  styleUrls: ['./commandes-achat.component.css']
})
export class CommandesAchatComponent implements OnInit {
  private commandeService = inject(CommandeAchatService);
  private fournisseurService = inject(FournisseurService);
  private ingredientService = inject(IngredientService);
  private notification = inject(NotificationService);
  private fb = inject(FormBuilder);

  commandes: CommandeAchat[] = [];
  filteredCommandes: CommandeAchat[] = [];
  fournisseurs: Fournisseur[] = [];
  ingredients: Ingredient[] = [];
  
  p: number = 1;
  searchTerm: string = '';
  filterStatut: string = 'ALL';
  
  showModal = false;
  isEditing = false;
  commandeForm!: FormGroup;
  loading = false;
  
  montantTotalPeriode: number = 0;
  dateDebut: string = new Date(new Date().setDate(new Date().getDate() - 30)).toISOString().substring(0, 10);
  dateFin: string = new Date().toISOString().substring(0, 10);

  ngOnInit() {
    this.loadData();
    this.initForm();
  }

  initForm() {
    this.commandeForm = this.fb.group({
      id: [null],
      fournisseurId: [null, [Validators.required]],
      dateCommande: [new Date().toISOString().substring(0, 10), [Validators.required]],
      dateLivraisonPrevue: [null],
      notes: [''],
      lignes: this.fb.array([], [Validators.required, Validators.minLength(1)])
    });
  }

  get lignes() { return this.commandeForm.get('lignes') as FormArray; }

  addLigne() {
    this.lignes.push(this.fb.group({
      ingredientId: [null, [Validators.required]],
      quantiteCommandee: [1, [Validators.required, Validators.min(0.01)]],
      prixUnitaire: [0, [Validators.required, Validators.min(0)]]
    }));
  }

  removeLigne(index: number) { this.lignes.removeAt(index); }

  loadData() {
    this.commandeService.getAll().subscribe(data => { this.commandes = data; this.applyFilters(); });
    this.fournisseurService.getAll().subscribe(data => this.fournisseurs = data);
    this.ingredientService.getAll().subscribe(data => this.ingredients = data);
    this.calculerMontantPeriode();
  }

  calculerMontantPeriode() {
    this.commandeService.getMontantPeriode(this.dateDebut, this.dateFin).subscribe(total => {
      this.montantTotalPeriode = total;
    });
  }

  applyFilters() {
    this.filteredCommandes = this.commandes.filter(c => {
      const matchSearch = !this.searchTerm || 
                         (c.fournisseurNom && c.fournisseurNom.toLowerCase().includes(this.searchTerm.toLowerCase())) ||
                         (c.numeroCommande && c.numeroCommande.toLowerCase().includes(this.searchTerm.toLowerCase()));
      const matchStatut = this.filterStatut === 'ALL' || c.statut === this.filterStatut;
      return matchSearch && matchStatut;
    });
  }

  openModal(commande?: CommandeAchat) {
    this.isEditing = !!commande;
    this.showModal = true;
    this.lignes.clear();
    
    if (commande) {
      this.commandeForm.patchValue({
        id: commande.id,
        fournisseurId: commande.fournisseurId,
        dateCommande: commande.dateCommande ? new Date(commande.dateCommande).toISOString().substring(0, 10) : null,
        dateLivraisonPrevue: commande.dateLivraisonPrevue ? new Date(commande.dateLivraisonPrevue).toISOString().substring(0, 10) : null,
        notes: commande.notes
      });
      commande.lignesCommande.forEach(l => {
        this.lignes.push(this.fb.group({
          ingredientId: [l.ingredientId, Validators.required],
          quantiteCommandee: [l.quantiteCommandee, Validators.required],
          prixUnitaire: [l.prixUnitaire, Validators.required]
        }));
      });
    } else {
      this.commandeForm.reset({ 
        dateCommande: new Date().toISOString().substring(0, 10) 
      });
      this.addLigne();
    }
  }

  saveCommande() {
    if (this.commandeForm.invalid) return;
    this.loading = true;
    const data = this.commandeForm.value;
    const obs = this.isEditing ? this.commandeService.update(data.id, data) : this.commandeService.create(data);
    
    obs.subscribe({
      next: () => {
        this.notification.success('Commande enregistrée', 'Succès');
        this.loadData();
        this.showModal = false;
        this.loading = false;
      },
      error: () => { this.notification.error('Erreur', 'Erreur'); this.loading = false; }
    });
  }

  recevoirCommande(c: CommandeAchat) {
    // For now simple reception, but plan is to add a modal for precise quantities
    const request = {
      dateLivraisonReelle: new Date().toISOString().substring(0, 10),
      lignesRecues: c.lignesCommande.map(l => ({
        ingredientId: l.ingredientId,
        quantiteRecue: l.quantiteCommandee
      }))
    };
    
    this.commandeService.recevoir(c.id, request).subscribe({
      next: () => { this.notification.success('Commande reçue et stock mis à jour', 'Succès'); this.loadData(); }
    });
  }

  duplicateCommande(id: number) {
    this.commandeService.dupliquer(id).subscribe(() => {
      this.notification.success('Commande dupliquée', 'Succès');
      this.loadData();
    });
  }

  cancelCommande(id: number) {
    const raison = prompt('Raison de l\'annulation ?');
    if (raison) {
      this.commandeService.annuler(id, raison).subscribe(() => {
        this.notification.success('Commande annulée', 'Succès');
        this.loadData();
      });
    }
  }

  changerStatut(c: CommandeAchat, nouveauStatut: string) {
    if (confirm(`Changer le statut de la commande en ${nouveauStatut} ?`)) {
      this.commandeService.changerStatut(c.id, nouveauStatut).subscribe(() => {
        this.notification.success('Statut mis à jour', 'Succès');
        this.loadData();
      });
    }
  }

  deleteCommande(id: number) {
    if (confirm('Voulez-vous vraiment supprimer définitivement cette commande ? Cette action est irréversible.')) {
      this.commandeService.delete(id).subscribe({
        next: () => {
          this.notification.success('Commande supprimée', 'Succès');
          this.loadData();
        },
        error: () => this.notification.error('Impossible de supprimer une commande liée à d\'autres enregistrements', 'Erreur')
      });
    }
  }
}
