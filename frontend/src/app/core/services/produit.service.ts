import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Produit } from '../models/produit.model';

@Injectable({
  providedIn: 'root'
})
export class ProduitService {
  private baseUrl = `${environment.apiUrl}/produits`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<Produit[]> {
    return this.http.get<Produit[]>(this.baseUrl);
  }

  getById(id: number): Observable<Produit> {
    return this.http.get<Produit>(`${this.baseUrl}/${id}`);
  }

  create(produit: Partial<Produit>): Observable<Produit> {
    return this.http.post<Produit>(this.baseUrl, produit);
  }

  update(id: number, produit: Partial<Produit>): Observable<Produit> {
    return this.http.put<Produit>(`${this.baseUrl}/${id}`, produit);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getByCategorie(categorie: string): Observable<Produit[]> {
    return this.http.get<Produit[]>(`${this.baseUrl}/categorie/${categorie}`);
  }

  getStockFaible(): Observable<Produit[]> {
    return this.http.get<Produit[]>(`${this.baseUrl}/stock-faible`);
  }

  search(keyword: string): Observable<Produit[]> {
    let params = new HttpParams().set('keyword', keyword);
    return this.http.get<Produit[]>(`${this.baseUrl}/search`, { params });
  }

  ajusterStock(id: number, quantite: number, type: string): Observable<Produit> {
    let params = new HttpParams().set('quantite', quantite.toString()).set('type', type);
    return this.http.post<Produit>(`${this.baseUrl}/${id}/ajuster-stock`, {}, { params });
  }

  lierRecette(produitId: number, recetteId: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${produitId}/lier-recette/${recetteId}`, {});
  }

  getPlusVendus(limit: number = 5): Observable<Produit[]> {
    let params = new HttpParams().set('limit', limit.toString());
    return this.http.get<Produit[]>(`${this.baseUrl}/plus-vendus`, { params });
  }

  getValeurStockTotal(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/valeur-stock-total`);
  }
}
