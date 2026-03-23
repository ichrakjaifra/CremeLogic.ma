import { Component, OnInit, inject, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VenteService } from '../../../../core/services/vente.service';
import { Chart, registerables } from 'chart.js';
import { FormatPricePipe } from '../../../../shared/pipes/format-price.pipe';

Chart.register(...registerables);

@Component({
  selector: 'app-vente-stats',
  standalone: true,
  imports: [CommonModule, FormatPricePipe],
  templateUrl: './vente-stats.component.html',
  styleUrls: ['./vente-stats.component.css']
})
export class VenteStatsComponent implements OnInit, AfterViewInit {
  private venteService = inject(VenteService);

  @ViewChild('productsChart') productsChartRef!: ElementRef;
  @ViewChild('categoriesChart') categoriesChartRef!: ElementRef;

  stats = {
    revenueToday: 0,
    salesCount: 0,
    avgBasket: 0,
    growth: 12.5
  };

  loading = true;

  ngOnInit() {
    this.loadStats();
  }

  ngAfterViewInit() {}

  loadStats() {
    const today = new Date();
    today.setHours(0,0,0,0);
    const now = new Date();
    const startStr = today.toISOString();
    const endStr = now.toISOString();

    this.venteService.getChiffreAffaires(startStr, endStr).subscribe((data: number) => this.stats.revenueToday = data || 0);
    this.venteService.getNombreVentes(startStr, endStr).subscribe((data: number) => {
        this.stats.salesCount = data || 0;
        this.stats.avgBasket = this.stats.salesCount > 0 ? this.stats.revenueToday / this.stats.salesCount : 0;
    });

    this.loadChartsData();
  }

  loadChartsData() {
    const monthAgo = new Date();
    monthAgo.setMonth(monthAgo.getMonth() - 1);
    const startStr = monthAgo.toISOString();
    const endStr = new Date().toISOString();

    this.venteService.getProduitsPlusVendus(startStr, endStr, 5).subscribe((data: any[]) => {
      this.initProductsChart(data);
    });

    this.venteService.getParCategorie(startStr, endStr).subscribe((data: any) => {
      this.initCategoriesChart(data);
      this.loading = false;
    });
  }

  initProductsChart(data: any[]) {
    if (!this.productsChartRef) return;
    const ctx = this.productsChartRef.nativeElement.getContext('2d');
    new Chart(ctx, {
      type: 'bar',
      data: {
        labels: data.map(i => i.nom),
        datasets: [{
          label: 'Ventes Par Produit',
          data: data.map(i => i.quantiteVendue || i.nombreVentes || 15),
          backgroundColor: '#8d6e63',
          borderRadius: 8
        }]
      },
      options: {
        responsive: true,
        plugins: { legend: { display: false } },
        scales: { y: { beginAtZero: true } }
      }
    });
  }

  initCategoriesChart(data: any) {
    if (!this.categoriesChartRef) return;
    const ctx = this.categoriesChartRef.nativeElement.getContext('2d');
    const labels = Object.keys(data);
    const values = Object.values(data);

    new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{
          data: values,
          backgroundColor: ['#5d4037', '#8d6e63', '#a1887f', '#d7ccc8', '#f5f5f5', '#4e342e', '#3e2723'],
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { position: 'bottom', labels: { boxWidth: 12, padding: 15 } }
        },
        cutout: '70%'
      }
    });
  }
}
