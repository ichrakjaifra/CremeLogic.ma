import { Component, OnInit, inject, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../../core/services/dashboard.service';
import { AdminStats } from '../../../core/models/dashboard.model';
import { FormatPricePipe } from '../../../shared/pipes/format-price.pipe';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormatPricePipe],
  templateUrl: './dashboard.component.html',
  styles: [`
    .admin-dashboard {
      background-color: var(--bg-global);
      min-height: 100vh;
    }
    .kpi-icon {
      width: 50px;
      height: 50px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.5rem;
    }
    .bg-beige { background-color: #fceec7; }
    .bg-beige-light { background-color: #fff9ea; }
    .bg-error-light { background-color: #fee2e2; }
    
    .bg-success-soft { background-color: #dcfce7; }
    .bg-warning-soft { background-color: #fef9c3; }
    .bg-danger-soft { background-color: #fee2e2; }
    
    .alert-danger-soft { background-color: #fff1f2; }
    
    .chart-container {
      position: relative;
      height: 300px;
      width: 100%;
    }
    .extra-small { font-size: 0.75rem; }
    .supplier-icon {
      width: 40px;
      height: 40px;
      border-radius: 8px;
      background-color: #f3f4f6;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  `]
})
export class AdminDashboardComponent implements OnInit, AfterViewInit {
  private dashboardService = inject(DashboardService);
  stats?: AdminStats;
  today = new Date();
  chart: any;

  ngOnInit() {
    this.dashboardService.getAdminStats().subscribe(stats => {
      this.stats = stats;
      this.updateChart();
    });
  }

  ngAfterViewInit() {
    this.initChart();
  }

  initChart() {
    const ctx = document.getElementById('ventesChart') as HTMLCanvasElement;
    if (!ctx) return;

    this.chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
        datasets: [
          {
            label: 'Ventes',
            data: [25, 45, 30, 55, 40, 85],
            backgroundColor: '#D4AF37',
            borderRadius: 5,
          },
          {
            label: 'Bénéfices',
            type: 'line',
            data: [15, 35, 20, 45, 30, 75],
            borderColor: '#8B4513',
            borderWidth: 3,
            fill: false,
            tension: 0.4,
            pointBackgroundColor: '#8B4513'
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom',
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            grid: {
              display: false
            }
          },
          x: {
            grid: {
              display: false
            }
          }
        }
      }
    });
  }

  updateChart() {
    if (this.stats?.ventesMensuelles && this.chart) {
      this.chart.data.labels = this.stats.ventesMensuelles.map(m => m.mois);
      this.chart.data.datasets[0].data = this.stats.ventesMensuelles.map(m => m.montant);
      this.chart.data.datasets[1].data = this.stats.ventesMensuelles.map(m => m.benefice);
      this.chart.update();
    }
  }
}
