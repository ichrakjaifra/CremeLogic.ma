import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Tache } from '../models/tache.model';

@Injectable({
  providedIn: 'root'
})
export class TacheService {
  private baseUrl = `${environment.apiUrl}/taches`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<Tache[]> {
    return this.http.get<Tache[]>(this.baseUrl);
  }

  getByUtilisateur(userId: number): Observable<Tache[]> {
    return this.http.get<Tache[]>(`${this.baseUrl}/utilisateur/${userId}`);
  }

  create(tache: Partial<Tache>): Observable<Tache> {
    return this.http.post<Tache>(this.baseUrl, tache);
  }

  update(id: number, tache: Partial<Tache>): Observable<Tache> {
    return this.http.put<Tache>(`${this.baseUrl}/${id}`, tache);
  }

  updateStatut(id: number, statut: string): Observable<Tache> {
    let params = new HttpParams().set('statut', statut);
    return this.http.patch<Tache>(`${this.baseUrl}/${id}/statut`, {}, { params });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
