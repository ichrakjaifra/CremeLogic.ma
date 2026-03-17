import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Fournisseur } from '../models/commande-achat.model';

@Injectable({
  providedIn: 'root'
})
export class FournisseurService {
  private baseUrl = `${environment.apiUrl}/fournisseurs`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<Fournisseur[]> {
    return this.http.get<Fournisseur[]>(this.baseUrl);
  }

  getById(id: number): Observable<Fournisseur> {
    return this.http.get<Fournisseur>(`${this.baseUrl}/${id}`);
  }

  create(fournisseur: Partial<Fournisseur>): Observable<Fournisseur> {
    return this.http.post<Fournisseur>(this.baseUrl, fournisseur);
  }

  update(id: number, fournisseur: Partial<Fournisseur>): Observable<Fournisseur> {
    return this.http.put<Fournisseur>(`${this.baseUrl}/${id}`, fournisseur);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getActifs(): Observable<Fournisseur[]> {
    return this.http.get<Fournisseur[]>(`${this.baseUrl}/actifs`);
  }

  search(keyword: string): Observable<Fournisseur[]> {
    let params = new HttpParams().set('keyword', keyword);
    return this.http.get<Fournisseur[]>(`${this.baseUrl}/search`, { params });
  }

  evaluer(id: number, note: number, commentaire: string): Observable<void> {
    let params = new HttpParams().set('note', note.toString()).set('commentaire', commentaire);
    return this.http.post<void>(`${this.baseUrl}/${id}/evaluer`, {}, { params });
  }

  getByVille(ville: string): Observable<Fournisseur[]> {
    return this.http.get<Fournisseur[]>(`${this.baseUrl}/ville/${ville}`);
  }

  getMontantTotalCommandes(id: number): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/${id}/montant-commandes`);
  }

  getNombreCommandes(id: number): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/${id}/nombre-commandes`);
  }

  toggleActif(id: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${id}/toggle-actif`, {});
  }
}
