import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { OrdreProduction } from '../models/ordre-production.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class OrdreProductionService {
  private baseUrl = `${environment.apiUrl}/ordres-production`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<OrdreProduction[]> {
    return this.http.get<ApiResponse<OrdreProduction[]>>(this.baseUrl).pipe(
      map(response => response.data || [])
    );
  }

  getById(id: number): Observable<OrdreProduction> {
    return this.http.get<ApiResponse<OrdreProduction>>(`${this.baseUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  create(ordre: Partial<OrdreProduction>): Observable<OrdreProduction> {
    return this.http.post<ApiResponse<OrdreProduction>>(this.baseUrl, ordre).pipe(
      map(response => response.data)
    );
  }

  update(id: number, ordre: Partial<OrdreProduction>): Observable<OrdreProduction> {
    return this.http.put<ApiResponse<OrdreProduction>>(`${this.baseUrl}/${id}`, ordre).pipe(
      map(response => response.data)
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getByProduit(produitId: number): Observable<OrdreProduction[]> {
    return this.http.get<ApiResponse<OrdreProduction[]>>(`${this.baseUrl}/produit/${produitId}`).pipe(
      map(response => response.data || [])
    );
  }

  getByStatut(statut: string): Observable<OrdreProduction[]> {
    return this.http.get<ApiResponse<OrdreProduction[]>>(`${this.baseUrl}/statut/${statut}`).pipe(
      map(response => response.data || [])
    );
  }

  changerStatut(id: number, statut: string): Observable<OrdreProduction> {
    let params = new HttpParams().set('statut', statut);
    return this.http.patch<ApiResponse<OrdreProduction>>(`${this.baseUrl}/${id}/statut`, {}, { params }).pipe(
      map(response => response.data)
    );
  }

  demarrer(id: number, request: any): Observable<OrdreProduction> {
    return this.http.post<ApiResponse<OrdreProduction>>(`${this.baseUrl}/${id}/demarrer`, request).pipe(
      map(response => response.data)
    );
  }

  terminer(id: number, request: any): Observable<OrdreProduction> {
    return this.http.post<ApiResponse<OrdreProduction>>(`${this.baseUrl}/${id}/terminer`, request).pipe(
      map(response => response.data)
    );
  }

  updateStatutEtape(id: number, suiviEtapeId: number, statut: string): Observable<OrdreProduction> {
    let params = new HttpParams().set('statut', statut);
    return this.http.patch<ApiResponse<OrdreProduction>>(`${this.baseUrl}/${id}/etapes/${suiviEtapeId}`, {}, { params }).pipe(
      map(response => response.data)
    );
  }

  annuler(id: number, raison: string): Observable<void> {
    let params = new HttpParams().set('raison', raison);
    return this.http.post<void>(`${this.baseUrl}/${id}/annuler`, {}, { params });
  }

  getEnRetard(): Observable<OrdreProduction[]> {
    return this.http.get<ApiResponse<OrdreProduction[]>>(`${this.baseUrl}/en-retard`).pipe(
      map(response => response.data || [])
    );
  }

  getCoutPeriode(debut: string, fin: string): Observable<number> {
    let params = new HttpParams().set('debut', debut).set('fin', fin);
    return this.http.get<ApiResponse<number>>(`${this.baseUrl}/cout-periode`, { params }).pipe(
      map(response => response.data)
    );
  }

  getQuantiteProduite(produitId: number, debut: string, fin: string): Observable<number> {
    let params = new HttpParams()
      .set('produitId', produitId.toString())
      .set('debut', debut)
      .set('fin', fin);
    return this.http.get<ApiResponse<number>>(`${this.baseUrl}/quantite-produite`, { params }).pipe(
      map(response => response.data)
    );
  }

  dupliquer(id: number): Observable<OrdreProduction> {
    return this.http.post<ApiResponse<OrdreProduction>>(`${this.baseUrl}/${id}/dupliquer`, {}).pipe(
      map(response => response.data)
    );
  }

  consommerIngredients(id: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${id}/consommer-ingredients`, {});
  }
}
