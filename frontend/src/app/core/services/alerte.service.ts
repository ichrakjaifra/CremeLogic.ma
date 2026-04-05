import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Alerte } from '../models/alerte.model';

@Injectable({
  providedIn: 'root'
})
export class AlerteService {
  private apiUrl = `${environment.apiUrl}/alertes`;
  private alertesSubject = new BehaviorSubject<Alerte[]>([]);
  public alertes$ = this.alertesSubject.asObservable();
  
  private unreadCountSubject = new BehaviorSubject<number>(0);
  public unreadCount$ = this.unreadCountSubject.asObservable();

  constructor(private http: HttpClient) { }

  loadInitialAlertes(): void {
    this.http.get<Alerte[]>(`${this.apiUrl}/non-resolues`).subscribe(alertes => {
      this.alertesSubject.next(alertes);
      this.unreadCountSubject.next(alertes.length);
    });
  }

  addAlerte(alerte: Alerte): void {
    const currentAlertes = this.alertesSubject.value;
    this.alertesSubject.next([alerte, ...currentAlertes]);
    this.unreadCountSubject.next(this.unreadCountSubject.value + 1);
  }

  resoudreAlerte(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/resoudre`, {}).pipe(
      tap(() => {
        const remaining = this.alertesSubject.value.filter(a => a.id !== id);
        this.alertesSubject.next(remaining);
        this.unreadCountSubject.next(Math.max(0, this.unreadCountSubject.value - 1));
      })
    );
  }

  resoudreToutes(): void {
    // Optional: implement backend endpoint for bulk resolve
    this.alertesSubject.value.forEach(a => {
        this.resoudreAlerte(a.id).subscribe();
    });
  }
}
