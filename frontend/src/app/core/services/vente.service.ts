import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Vente } from '../models/vente.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class VenteService {
  private baseUrl = `${environment.apiUrl}/ventes`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<Vente[]> {
    return this.http.get<ApiResponse<Vente[]>>(this.baseUrl).pipe(
      map(response => response.data || [])
    );
  }

  getById(id: number): Observable<Vente> {
    return this.http.get<ApiResponse<Vente>>(`${this.baseUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  create(vente: Partial<Vente>): Observable<Vente> {
    return this.http.post<ApiResponse<Vente>>(this.baseUrl, vente).pipe(
      map(response => response.data)
    );
  }

  annuler(id: number, raison: string): Observable<void> {
    let params = new HttpParams().set('raison', raison);
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { params });
  }

  getByPeriode(debut: string, fin: string): Observable<Vente[]> {
    let params = new HttpParams().set('debut', debut).set('fin', fin);
    return this.http.get<ApiResponse<Vente[]>>(`${this.baseUrl}/periode`, { params }).pipe(
      map(response => response.data || [])
    );
  }

  getByCaissier(caissierId: number): Observable<Vente[]> {
    return this.http.get<ApiResponse<Vente[]>>(`${this.baseUrl}/caissier/${caissierId}`).pipe(
      map(response => response.data || [])
    );
  }

  searchByClient(keyword: string): Observable<Vente[]> {
    let params = new HttpParams().set('keyword', keyword);
    return this.http.get<ApiResponse<Vente[]>>(`${this.baseUrl}/client`, { params }).pipe(
      map(response => response.data || [])
    );
  }

  getFacture(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${id}/facture`, { responseType: 'blob' });
  }

  getChiffreAffaires(debut: string, fin: string): Observable<number> {
    let params = new HttpParams().set('debut', debut).set('fin', fin);
    return this.http.get<ApiResponse<number>>(`${this.baseUrl}/chiffre-affaires`, { params }).pipe(
      map(response => response.data)
    );
  }

  getNombreVentes(debut: string, fin: string): Observable<number> {
    let params = new HttpParams().set('debut', debut).set('fin', fin);
    return this.http.get<ApiResponse<number>>(`${this.baseUrl}/nombre-ventes`, { params }).pipe(
      map(response => response.data)
    );
  }

  getRecentes(limit: number = 10): Observable<Vente[]> {
    let params = new HttpParams().set('limit', limit.toString());
    return this.http.get<ApiResponse<Vente[]>>(`${this.baseUrl}/recentes`, { params }).pipe(
      map(response => response.data || [])
    );
  }

  getParCategorie(debut: string, fin: string): Observable<any> {
    let params = new HttpParams().set('debut', debut).set('fin', fin);
    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/par-categorie`, { params }).pipe(
      map(response => response.data)
    );
  }

  getProduitsPlusVendus(debut: string, fin: string, limit: number = 5): Observable<any> {
    let params = new HttpParams()
      .set('debut', debut)
      .set('fin', fin)
      .set('limit', limit.toString());
    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/produits-plus-vendus`, { params }).pipe(
      map(response => response.data)
    );
  }
}
