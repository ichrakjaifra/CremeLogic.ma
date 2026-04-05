import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Fournisseur } from '../models/commande-achat.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class FournisseurService {
  private baseUrl = `${environment.apiUrl}/fournisseurs`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<Fournisseur[]> {
    return this.http.get<ApiResponse<Fournisseur[]>>(this.baseUrl).pipe(
      map(response => response.data || [])
    );
  }

  getById(id: number): Observable<Fournisseur> {
    return this.http.get<ApiResponse<Fournisseur>>(`${this.baseUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  create(fournisseur: Partial<Fournisseur>): Observable<Fournisseur> {
    return this.http.post<ApiResponse<Fournisseur>>(this.baseUrl, fournisseur).pipe(
      map(response => response.data)
    );
  }

  update(id: number, fournisseur: Partial<Fournisseur>): Observable<Fournisseur> {
    return this.http.put<ApiResponse<Fournisseur>>(`${this.baseUrl}/${id}`, fournisseur).pipe(
      map(response => response.data)
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getActifs(): Observable<Fournisseur[]> {
    return this.http.get<ApiResponse<Fournisseur[]>>(`${this.baseUrl}/actifs`).pipe(
      map(response => response.data || [])
    );
  }

  search(keyword: string): Observable<Fournisseur[]> {
    let params = new HttpParams().set('keyword', keyword);
    return this.http.get<ApiResponse<Fournisseur[]>>(`${this.baseUrl}/search`, { params }).pipe(
      map(response => response.data || [])
    );
  }

  evaluer(id: number, note: number, commentaire: string): Observable<void> {
    let params = new HttpParams().set('note', note.toString()).set('commentaire', commentaire);
    return this.http.post<void>(`${this.baseUrl}/${id}/evaluer`, {}, { params });
  }

  getByVille(ville: string): Observable<Fournisseur[]> {
    return this.http.get<ApiResponse<Fournisseur[]>>(`${this.baseUrl}/ville/${ville}`).pipe(
      map(response => response.data || [])
    );
  }

  getMontantTotalCommandes(id: number): Observable<number> {
    return this.http.get<ApiResponse<number>>(`${this.baseUrl}/${id}/montant-commandes`).pipe(
      map(response => response.data)
    );
  }

  getNombreCommandes(id: number): Observable<number> {
    return this.http.get<ApiResponse<number>>(`${this.baseUrl}/${id}/nombre-commandes`).pipe(
      map(response => response.data)
    );
  }

  toggleActif(id: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${id}/toggle-actif`, {});
  }
}
