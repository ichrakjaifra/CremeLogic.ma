import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Produit } from '../models/produit.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class ProduitService {
  private baseUrl = `${environment.apiUrl}/produits`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<Produit[]> {
    return this.http.get<ApiResponse<Produit[]>>(this.baseUrl).pipe(
      map(response => (response.data || []).map(p => this.mapImageUrl(p)))
    );
  }

  getById(id: number): Observable<Produit> {
    return this.http.get<ApiResponse<Produit>>(`${this.baseUrl}/${id}`).pipe(
      map(response => this.mapImageUrl(response.data))
    );
  }

  private mapImageUrl(p: Produit): Produit {
    if (p && p.imageUrl) {
      if (p.imageUrl.includes('http')) {
        try {
          const url = new URL(p.imageUrl);
          p.imageUrl = url.pathname;
        } catch (e) {
          // Fallback if URL is invalid
          const index = p.imageUrl.indexOf('/api/uploads/');
          if (index !== -1) {
            p.imageUrl = p.imageUrl.substring(index);
          }
        }
      } else if (!p.imageUrl.startsWith('/api') && p.imageUrl.includes('uploads/')) {
        // If it starts with /uploads/ but misses /api
        const index = p.imageUrl.indexOf('/uploads/');
        p.imageUrl = '/api' + p.imageUrl.substring(index);
      }
    }
    return p;
  }

  create(produit: Partial<Produit>): Observable<Produit> {
    return this.http.post<ApiResponse<Produit>>(this.baseUrl, produit).pipe(
      map(response => this.mapImageUrl(response.data))
    );
  }

  update(id: number, produit: Partial<Produit>): Observable<Produit> {
    return this.http.put<ApiResponse<Produit>>(`${this.baseUrl}/${id}`, produit).pipe(
      map(response => this.mapImageUrl(response.data))
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getByCategorie(categorie: string): Observable<Produit[]> {
    return this.http.get<ApiResponse<Produit[]>>(`${this.baseUrl}/categorie/${categorie}`).pipe(
      map(response => (response.data || []).map(p => this.mapImageUrl(p)))
    );
  }

  getStockFaible(): Observable<Produit[]> {
    return this.http.get<ApiResponse<Produit[]>>(`${this.baseUrl}/stock-faible`).pipe(
      map(response => (response.data || []).map(p => this.mapImageUrl(p)))
    );
  }

  search(keyword: string): Observable<Produit[]> {
    let params = new HttpParams().set('keyword', keyword);
    return this.http.get<ApiResponse<Produit[]>>(`${this.baseUrl}/search`, { params }).pipe(
      map(response => (response.data || []).map(p => this.mapImageUrl(p)))
    );
  }

  ajusterStock(id: number, quantite: number, type: string): Observable<Produit> {
    let params = new HttpParams().set('quantite', quantite.toString()).set('type', type);
    return this.http.post<ApiResponse<Produit>>(`${this.baseUrl}/${id}/ajuster-stock`, {}, { params }).pipe(
      map(response => response.data)
    );
  }

  lierRecette(produitId: number, recetteId: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${produitId}/lier-recette/${recetteId}`, {});
  }

  getPlusVendus(limit: number = 5): Observable<Produit[]> {
    let params = new HttpParams().set('limit', limit.toString());
    return this.http.get<ApiResponse<Produit[]>>(`${this.baseUrl}/plus-vendus`, { params }).pipe(
      map(response => response.data || [])
    );
  }

  getValeurStockTotal(): Observable<number> {
    return this.http.get<ApiResponse<number>>(`${this.baseUrl}/valeur-stock-total`).pipe(
      map(response => response.data)
    );
  }
}
