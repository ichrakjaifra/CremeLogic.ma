import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProduitService } from '../../../core/services/produit.service';
import { VenteService } from '../../../core/services/vente.service';
import { Produit, PRODUCT_CATEGORIES } from '../../../core/models/produit.model';
import { Vente, LigneVente } from '../../../core/models/vente.model';
import { FormatPricePipe } from '../../../shared/pipes/format-price.pipe';
import { NotificationService } from '../../../core/services/notification.service';

import { VenteHistoryComponent } from './vente-history/vente-history.component';
import { VenteStatsComponent } from './vente-stats/vente-stats.component';

@Component({
  selector: 'app-ventes',
  standalone: true,
  imports: [CommonModule, FormsModule, FormatPricePipe, VenteHistoryComponent, VenteStatsComponent],
  templateUrl: './ventes.component.html',
  styleUrls: ['./ventes.component.css']
})
export class VentesComponent implements OnInit {
  private produitService = inject(ProduitService);
  private venteService = inject(VenteService);
  private notification = inject(NotificationService);

  currentView: string = 'CAISSE';

  products: Produit[] = [];
  filteredProducts: Produit[] = [];
  categories: string[] = ['TOUT', ...PRODUCT_CATEGORIES];
  selectedCategorie: string = 'TOUT';
  searchProduct: string = '';
  cart: LigneVente[] = [];
  paymentMode: 'ESPECES' | 'CARTE' | 'AUTRE' = 'ESPECES';
  loading = false;

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.loading = true;
    this.produitService.getAll().subscribe({
      next: (data) => {
        this.products = data;
        this.applyFilters();
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  applyFilters() {
    if (!this.products || !Array.isArray(this.products)) {
      this.filteredProducts = [];
      return;
    }
    this.filteredProducts = this.products.filter(p => {
      const matchSearch = !this.searchProduct || p.nom.toLowerCase().includes(this.searchProduct.toLowerCase());
      const matchCat = this.selectedCategorie === 'TOUT' || p.categorie === this.selectedCategorie;
      return matchSearch && matchCat && p.statut === 'ACTIF';
    });
  }

  addToCart(p: Produit) {
    if (p.stockDisponible <= 0) {
      this.notification.warning(`Stock insuffisant pour ${p.nom}`, 'Attention');
      return;
    }

    const existing = this.cart.find(i => i.produitId === p.id);
    if (existing) {
      if (existing.quantite < p.stockDisponible) {
        existing.quantite++;
      } else {
        this.notification.warning('Stock maximum atteint', 'Attention');
      }
    } else {
      this.cart.push({
        produitId: p.id!,
        produitNom: p.nom,
        quantite: 1,
        prixUnitaire: p.prixVente
      });
    }
  }

  updateQuantity(item: LigneVente, delta: number) {
    const product = this.products.find(p => p.id === item.produitId);
    if (!product) return;

    const newQty = item.quantite + delta;
    if (newQty > 0) {
      if (newQty <= product.stockDisponible) {
        item.quantite = newQty;
      } else {
        this.notification.warning('Stock insuffisant', 'Attention');
      }
    } else {
      this.removeFromCart(item);
    }
  }

  removeFromCart(item: LigneVente) {
    this.cart = this.cart.filter(i => i.produitId !== item.produitId);
  }

  get totalCart(): number {
    return this.cart.reduce((sum, item) => sum + (item.quantite * item.prixUnitaire), 0);
  }

  newVente() {
    this.cart = [];
    this.paymentMode = 'ESPECES';
  }

  validVente() {
    if (this.cart.length === 0) return;
    
    const vente: Partial<Vente> = {
      montantTotal: this.totalCart,
      modePaiement: this.paymentMode,
      lignesVente: this.cart,
      dateVente: new Date()
    };

    this.venteService.create(vente).subscribe({
      next: () => {
        this.notification.success('Vente enregistrée avec succès.', 'Validée');
        this.loadProducts(); // Fresh stock data
        this.newVente();
      },
      error: () => this.notification.error('Erreur lors de l\'enregistrement.', 'Erreur')
    });
  }
}
