import { Component, OnInit, inject, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { DashboardService } from '../../../core/services/dashboard.service';
import { AdminStats } from '../../../core/models/dashboard.model';
import { FormatPricePipe } from '../../../shared/pipes/format-price.pipe';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormatPricePipe, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit, AfterViewInit {
  private dashboardService = inject(DashboardService);
  stats?: AdminStats;
  today = new Date();
  chart: any;

  ngOnInit() {
    this.loadStats();
  }

  loadStats() {
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
        labels: [],
        datasets: [
          {
            label: 'Ventes',
            data: [],
            backgroundColor: '#D4AF37',
            borderRadius: 5,
            order: 2
          },
          {
            label: 'Bénéfices',
            type: 'line',
            data: [],
            borderColor: '#8B4513',
            borderWidth: 3,
            fill: false,
            tension: 0.4,
            pointBackgroundColor: '#8B4513',
            order: 1
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
              display: true,
              color: 'rgba(0,0,0,0.05)'
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
    if (this.stats && this.chart) {
      if (this.stats.ventesParMois) {
        const labels = Object.keys(this.stats.ventesParMois).map(l => {
            const [year, month] = l.split('-');
            const date = new Date(parseInt(year), parseInt(month) - 1);
            return date.toLocaleString('default', { month: 'short' });
        });
        const salesData = Object.values(this.stats.ventesParMois);
        const profitData = this.stats.beneficesParMois ? Object.values(this.stats.beneficesParMois) : salesData.map(v => v * 0.2);

        this.chart.data.labels = labels;
        this.chart.data.datasets[0].data = salesData;
        this.chart.data.datasets[1].data = profitData;
        this.chart.update();
      }
    }
  }
}
