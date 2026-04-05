import { Component, OnInit, inject, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { OrdreProductionService } from '../../../core/services/ordre-production.service';
import { NotificationService } from '../../../core/services/notification.service';
import { OrdreProduction, SuiviEtape } from '../../../core/models/ordre-production.model';
import { Recette } from '../../../core/models/recette.model';
import { RecetteService } from '../../../core/services/recette.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-production-execution',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './production-execution.component.html',
  styleUrls: ['./production-execution.component.css']
})
export class ProductionExecutionComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private productionService = inject(OrdreProductionService);
  private recetteService = inject(RecetteService);
  private notification = inject(NotificationService);

  ordre?: OrdreProduction;
  recette?: Recette;
  loading = true;
  startTime?: Date;
  timerInterval: any;
  elapsedTime = 0; // In seconds

  ngOnInit() {
    this.loadData();
  }

  ngOnDestroy() {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
  }

  loadData() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) return;

    this.loading = true;
    this.productionService.getById(id).subscribe({
      next: (data) => {
        this.ordre = data;
        if (data.recetteId) {
            this.loadRecette(data.recetteId);
        }
        this.loading = false;
        if (data.statut === 'EN_COURS') {
            this.startGlobalTimer();
        }
      },
      error: () => {
        this.loading = false;
        this.notification.error('Impossible de charger l\'ordre de production.');
      }
    });
  }

  loadRecette(recetteId: number) {
      this.recetteService.getById(recetteId).subscribe({
          next: (data) => this.recette = data
      });
  }

  startProduction() {
    if (!this.ordre) return;
    this.productionService.demarrer(this.ordre.id, { dateDebutReelle: new Date() }).subscribe({
      next: (updated) => {
        this.ordre = updated;
        this.startGlobalTimer();
        this.notification.success('Production démarrée !');
      }
    });
  }

  toggleStep(suivi: SuiviEtape) {
    if (!this.ordre) return;
    
    let nextStatut: 'A_FAIRE' | 'EN_COURS' | 'TERMINEE' = 'A_FAIRE';
    if (suivi.statut === 'A_FAIRE') nextStatut = 'EN_COURS';
    else if (suivi.statut === 'EN_COURS') nextStatut = 'TERMINEE';
    else return; // Already finished

    this.productionService.updateStatutEtape(this.ordre.id, suivi.id, nextStatut).subscribe({
      next: (updated) => {
        this.ordre = updated;
        this.notification.info(`Étape: ${suivi.descriptionEtape} -> ${nextStatut}`);
      }
    });
  }

  finishProduction() {
    if (!this.ordre) return;
    this.productionService.terminer(this.ordre.id, { dateDebutReelle: new Date() }).subscribe({
      next: () => {
        this.notification.success('Production terminée avec succès !');
        this.router.navigate(['/employe/dashboard']);
      }
    });
  }

  reportProblem() {
      this.notification.warning('Fonctionnalité de signalement en cours de développement.');
  }

  startGlobalTimer() {
      if (this.timerInterval) return;
      this.startTime = new Date();
      this.timerInterval = setInterval(() => {
          this.elapsedTime++;
      }, 1000);
  }

  formatTime(seconds: number): string {
      const hrs = Math.floor(seconds / 3600);
      const mins = Math.floor((seconds % 3600) / 60);
      const secs = seconds % 60;
      return `${hrs > 0 ? hrs + ':' : ''}${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }

  get progress(): number {
      if (this.ordre?.statut === 'TERMINEE') return 100;
      if (!this.ordre?.suivisEtapes?.length) return 0;
      const finished = this.ordre.suivisEtapes.filter(s => s.statut === 'TERMINEE').length;
      return Math.round((finished / this.ordre.suivisEtapes.length) * 100);
  }
}
