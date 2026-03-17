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
  styles: [`
    .commandes-page { min-height: 100vh; animation: fadeIn 0.5s ease-out; }
    .commande-card { 
      transition: all 0.3s ease;
      border: 1px solid rgba(255, 255, 255, 0.4);
      border-left: 4px solid transparent;
    }
    .commande-card:hover { transform: translateX(5px); }
    .statut-en_attente { border-left-color: #f1c40f; }
    .statut-validee { border-left-color: #3498db; }
    .statut-recue { border-left-color: #2ecc71; }
    .statut-annulee { border-left-color: #e74c3c; }
  `]
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

  ngOnInit() {
    this.loadData();
    this.initForm();
  }

  initForm() {
    this.commandeForm = this.fb.group({
      id: [null],
      fournisseurId: [null, [Validators.required]],
      dateCommande: [new Date(), [Validators.required]],
      dateReceptionPrevue: [null],
      notes: [''],
      lignes: this.fb.array([], [Validators.required, Validators.minLength(1)])
    });
  }

  get lignes() { return this.commandeForm.get('lignes') as FormArray; }

  addLigne() {
    this.lignes.push(this.fb.group({
      ingredientId: [null, [Validators.required]],
      quantite: [1, [Validators.required, Validators.min(0.01)]],
      prixUnitaire: [0, [Validators.required, Validators.min(0)]]
    }));
  }

  removeLigne(index: number) { this.lignes.removeAt(index); }

  loadData() {
    this.commandeService.getAll().subscribe(data => { this.commandes = data; this.applyFilters(); });
    this.fournisseurService.getAll().subscribe(data => this.fournisseurs = data);
    this.ingredientService.getAll().subscribe(data => this.ingredients = data);
  }

  applyFilters() {
    this.filteredCommandes = this.commandes.filter(c => {
      const matchSearch = !this.searchTerm || c.nomFournisseur.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchStatut = this.filterStatut === 'ALL' || c.statut === this.filterStatut;
      return matchSearch && matchStatut;
    });
  }

  openModal(commande?: CommandeAchat) {
    this.isEditing = !!commande;
    this.showModal = true;
    this.lignes.clear();
    
    if (commande) {
      this.commandeForm.patchValue(commande);
      commande.lignesCommande.forEach(l => {
        this.lignes.push(this.fb.group({
          ingredientId: [l.ingredientId, Validators.required],
          quantite: [l.quantite, Validators.required],
          prixUnitaire: [l.prixUnitaire, Validators.required]
        }));
      });
    } else {
      this.commandeForm.reset({ dateCommande: new Date() });
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

  recevoirCommande(id: number) {
    this.commandeService.recevoir(id).subscribe({
      next: () => { this.notification.success('Commande reçue et stock mis à jour', 'Succès'); this.loadData(); }
    });
  }

  deleteCommande(id: number) {
    if (confirm('Supprimer cette commande ?')) {
      this.commandeService.delete(id).subscribe(() => { this.notification.success('Supprimée', 'OK'); this.loadData(); });
    }
  }
}
