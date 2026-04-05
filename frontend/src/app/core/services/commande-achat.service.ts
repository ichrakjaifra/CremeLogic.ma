import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CommandeAchat } from '../models/commande-achat.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class CommandeAchatService {
  private baseUrl = `${environment.apiUrl}/commandes-achat`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<CommandeAchat[]> {
    return this.http.get<ApiResponse<CommandeAchat[]>>(this.baseUrl).pipe(
      map(response => response.data || [])
    );
  }

  getById(id: number): Observable<CommandeAchat> {
    return this.http.get<ApiResponse<CommandeAchat>>(`${this.baseUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  create(commande: Partial<CommandeAchat>): Observable<CommandeAchat> {
    return this.http.post<ApiResponse<CommandeAchat>>(this.baseUrl, commande).pipe(
      map(response => response.data)
    );
  }

  update(id: number, commande: Partial<CommandeAchat>): Observable<CommandeAchat> {
    return this.http.put<ApiResponse<CommandeAchat>>(`${this.baseUrl}/${id}`, commande).pipe(
      map(response => response.data)
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getByFournisseur(fournisseurId: number): Observable<CommandeAchat[]> {
    return this.http.get<ApiResponse<CommandeAchat[]>>(`${this.baseUrl}/fournisseur/${fournisseurId}`).pipe(
      map(response => response.data || [])
    );
  }

  getByStatut(statut: string): Observable<CommandeAchat[]> {
    return this.http.get<ApiResponse<CommandeAchat[]>>(`${this.baseUrl}/statut/${statut}`).pipe(
      map(response => response.data || [])
    );
  }

  changerStatut(id: number, statut: string): Observable<CommandeAchat> {
    let params = new HttpParams().set('statut', statut);
    return this.http.patch<ApiResponse<CommandeAchat>>(`${this.baseUrl}/${id}/statut`, {}, { params }).pipe(
      map(response => response.data)
    );
  }

  recevoir(id: number, request: any): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${id}/recevoir`, request);
  }

  getEnRetard(): Observable<CommandeAchat[]> {
    return this.http.get<ApiResponse<CommandeAchat[]>>(`${this.baseUrl}/en-retard`).pipe(
      map(response => response.data || [])
    );
  }

  getMontantPeriode(debut: string, fin: string): Observable<number> {
    let params = new HttpParams().set('debut', debut).set('fin', fin);
    return this.http.get<ApiResponse<number>>(`${this.baseUrl}/montant-periode`, { params }).pipe(
      map(response => response.data)
    );
  }

  annuler(id: number, raison: string): Observable<void> {
    let params = new HttpParams().set('raison', raison);
    return this.http.post<void>(`${this.baseUrl}/${id}/annuler`, {}, { params });
  }

  dupliquer(id: number): Observable<CommandeAchat> {
    return this.http.post<ApiResponse<CommandeAchat>>(`${this.baseUrl}/${id}/dupliquer`, {}).pipe(
      map(response => response.data)
    );
  }
}
