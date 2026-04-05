import { Component, OnInit, inject, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProduitService } from '../../../core/services/produit.service';
import { RecetteService } from '../../../core/services/recette.service';
import { UploadService } from '../../../core/services/upload.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Produit, PRODUCT_CATEGORIES } from '../../../core/models/produit.model';
import { Recette } from '../../../core/models/recette.model';
import { FormatPricePipe } from '../../../shared/pipes/format-price.pipe';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-produits',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, FormatPricePipe, EmptyStateComponent],
  templateUrl: './produits.component.html',
  styleUrls: ['./produits.component.css']
})
export class ProduitsComponent implements OnInit {
  private produitService = inject(ProduitService);
  private recetteService = inject(RecetteService);
  private uploadService = inject(UploadService);
  private notification = inject(NotificationService);
  private fb = inject(FormBuilder);

  produits: Produit[] = [];
  filteredProduits: Produit[] = [];
  recettes: Recette[] = [];
  
  searchTerm: string = '';
  selectedCategorie: string = 'TOUT';
  categories: string[] = ['TOUT', ...PRODUCT_CATEGORIES];

  showModal = false;
  showStockModal = false;
  isEditing = false;
  produitForm!: FormGroup;
  stockForm!: FormGroup;
  selectedFile: File | null = null;
  imagePreview: string | null = null;
  loading = false;
  selectedProduit: Produit | null = null;

  ngOnInit() {
    this.loadProduits();
    this.loadRecettes();
    this.initForm();
  }

  initForm() {
    this.produitForm = this.fb.group({
      id: [null],
      nom: ['', [Validators.required]],
      description: [''],
      prixVente: [0, [Validators.required, Validators.min(0)]],
      categorie: ['GATEAUX', [Validators.required]],
      stockDisponible: [0, [Validators.required, Validators.min(0)]],
      stockMinimum: [5, [Validators.required, Validators.min(0)]],
      stockMaximum: [100, [Validators.required, Validators.min(0)]],
      uniteMesure: ['UNITE'],
      recetteId: [null],
      statut: ['ACTIF'],
      imageUrl: ['']
    });

    this.stockForm = this.fb.group({
      quantite: [0, [Validators.required, Validators.min(0.1)]],
      type: ['ENTREE', [Validators.required]]
    });
  }

  loadProduits() {
    this.produitService.getAll().subscribe({
      next: (data) => {
        this.produits = data;
        this.applyFilters();
      }
    });
  }

  loadRecettes() {
    this.recetteService.getAll().subscribe(data => this.recettes = data);
  }

  applyFilters() {
    if (!this.produits || !Array.isArray(this.produits)) {
      this.filteredProduits = [];
      return;
    }
    this.filteredProduits = this.produits.filter(p => {
      const matchSearch = !this.searchTerm || p.nom.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchCat = this.selectedCategorie === 'TOUT' || p.categorie === this.selectedCategorie;
      return matchSearch && matchCat;
    });
  }

  openModal(produit?: Produit) {
    this.isEditing = !!produit;
    this.showModal = true;
    this.selectedFile = null;
    this.imagePreview = produit?.imageUrl || null;
    
    if (produit) {
      this.produitForm.patchValue(produit);
    } else {
      this.produitForm.reset({
        prixVente: 0,
        categorie: 'GATEAUX',
        stockDisponible: 0,
        stockMinimum: 5,
        stockMaximum: 100,
        uniteMesure: 'UNITE',
        statut: 'ACTIF'
      });
    }
  }

  closeModal() {
    this.showModal = false;
    this.showStockModal = false;
  }

  openStockModal(produit: Produit) {
    this.selectedProduit = produit;
    this.showStockModal = true;
    this.stockForm.reset({
      quantite: 0,
      type: 'ENTREE'
    });
  }

  confirmAjusterStock() {
    if (this.stockForm.invalid || !this.selectedProduit) return;
    this.loading = true;
    const { quantite, type } = this.stockForm.value;
    this.produitService.ajusterStock(this.selectedProduit.id, quantite, type).subscribe({
      next: () => {
        this.notification.success('Stock mis à jour', 'Succès');
        this.loadProduits();
        this.closeModal();
        this.loading = false;
      },
      error: () => {
        this.notification.error('Erreur lors de l\'ajustement', 'Erreur');
        this.loading = false;
      }
    });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = (e: any) => this.imagePreview = e.target.result;
      reader.readAsDataURL(file);
    }
  }

  saveProduit() {
    if (this.produitForm.invalid) return;

    this.loading = true;
    if (this.selectedFile) {
      this.uploadService.uploadImage(this.selectedFile).subscribe({
        next: (res) => this.submitForm(res.url),
        error: () => {
          this.notification.error('Erreur lors de l\'upload de l\'image', 'Erreur');
          this.loading = false;
        }
      });
    } else {
      this.submitForm();
    }
  }

  private submitForm(imageUrl?: string) {
    const data = this.produitForm.value;
    if (imageUrl) data.imageUrl = imageUrl;

    const obs = this.isEditing 
      ? this.produitService.update(data.id, data)
      : this.produitService.create(data);

    obs.subscribe({
      next: () => {
        this.notification.success(`Produit ${this.isEditing ? 'mis à jour' : 'ajouté'} avec succès`, 'Succès');
        this.loadProduits();
        this.closeModal();
        this.loading = false;
      },
      error: () => {
        this.notification.error('Erreur lors de l\'enregistrement', 'Erreur');
        this.loading = false;
      }
    });
  }

  deleteProduit(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce produit ?')) {
      this.produitService.delete(id).subscribe({
        next: () => {
          this.notification.success('Produit supprimé', 'Succès');
          this.loadProduits();
        }
      });
    }
  }
}
