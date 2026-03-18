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
  styleUrls: ['./dashboard.component.css']
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
    if (this.stats?.ventesParMois && this.chart) {
      const labels = Object.keys(this.stats.ventesParMois);
      const data = Object.values(this.stats.ventesParMois);
      
      this.chart.data.labels = labels;
      this.chart.data.datasets[0].data = data;
      // If we had benefit data, we'd add it here. For now using same or 0.
      this.chart.data.datasets[1].data = data.map(v => v * 0.3); // Mocking benefit as 30% of sales if not provided
      this.chart.update();
    }
  }
}
