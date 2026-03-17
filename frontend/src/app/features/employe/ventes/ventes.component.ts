import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProduitService } from '../../../core/services/produit.service';
import { VenteService } from '../../../core/services/vente.service';
import { Produit } from '../../../core/models/produit.model';
import { Vente, LigneVente } from '../../../core/models/vente.model';
import { FormatPricePipe } from '../../../shared/pipes/format-price.pipe';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-ventes',
  standalone: true,
  imports: [CommonModule, FormsModule, FormatPricePipe],
  templateUrl: './ventes.component.html',
  styles: [`
    .sales-page {
      height: calc(100vh - 60px);
      overflow: hidden;
      animation: fadeIn 0.5s ease-out;
    }
    .glass-search {
      background: rgba(255, 255, 255, 0.4);
      backdrop-filter: blur(10px);
      border-radius: 20px;
      border: 1px solid rgba(255, 255, 255, 0.6);
    }
    .search-box {
      border-radius: 12px;
      overflow: hidden;
      background: white;
    }
    .product-card {
      border: 1px solid rgba(255, 255, 255, 0.4);
      cursor: pointer;
      border-radius: 20px;
    }
    .product-card:hover {
      transform: translateY(-5px);
      border-color: var(--dore-clair);
      background: rgba(253, 244, 227, 0.5);
    }
    .product-card:hover .add-overlay {
      opacity: 1;
    }
    .product-img-container {
      height: 120px;
      background: #FDF4E3;
      border-radius: 15px;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;
      overflow: hidden;
    }
    .stock-badge {
      position: absolute;
      bottom: 0;
      width: 100%;
      background: rgba(0,0,0,0.5);
      color: white;
      font-size: 0.65rem;
      padding: 2px 0;
      text-align: center;
      font-weight: bold;
    }
    .add-overlay {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      color: var(--marron-chocolat);
      opacity: 0;
      transition: all 0.3s;
      z-index: 2;
    }
    .product-name { font-size: 0.9rem; font-family: 'Montserrat', sans-serif; }
    .price-tag { color: var(--dore-sombre); font-size: 1rem; }
    
    .cart-sidebar {
      border-radius: 30px;
      background: rgba(255, 255, 255, 0.7);
      backdrop-filter: blur(15px);
    }
    .cart-item-row {
      background: white;
      border: 1px solid rgba(0,0,0,0.03);
      transition: all 0.2s;
    }
    .cart-item-row:hover {
      background: #FDF4E3;
    }
    .item-icon {
      width: 40px;
      height: 40px;
      background: #FDF4E3;
    }
    .category-scroll {
      overflow-x: auto;
      white-space: nowrap;
      -ms-overflow-style: none;
      scrollbar-width: none;
    }
    .category-scroll::-webkit-scrollbar { display: none; }
    
    .grayscale { filter: grayscale(1); }
    .animate-slide-in {
      animation: slideInRight 0.3s ease-out;
    }
    @keyframes slideInRight {
      from { transform: translateX(20px); opacity: 0; }
      to { transform: translateX(0); opacity: 1; }
    }
  `]
})
export class VentesComponent implements OnInit {
  private produitService = inject(ProduitService);
  private venteService = inject(VenteService);
  private notification = inject(NotificationService);

  products: Produit[] = [];
  filteredProducts: Produit[] = [];
  categories: string[] = ['TOUT', 'PAIN', 'PATISSERIE', 'VIENNOISERIE', 'SALE', 'AUTRE'];
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
    this.filteredProducts = this.products.filter(p => {
      const matchSearch = !this.searchProduct || p.nom.toLowerCase().includes(this.searchProduct.toLowerCase());
      const matchCat = this.selectedCategorie === 'TOUT' || p.categorie === this.selectedCategorie;
      return matchSearch && matchCat && p.actif;
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
        nomProduit: p.nom,
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
