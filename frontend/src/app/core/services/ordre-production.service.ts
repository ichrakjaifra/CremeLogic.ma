import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { OrdreProduction } from '../models/ordre-production.model';

@Injectable({
  providedIn: 'root'
})
export class OrdreProductionService {
  private baseUrl = `${environment.apiUrl}/ordres-production`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<OrdreProduction[]> {
    return this.http.get<OrdreProduction[]>(this.baseUrl);
  }

  getById(id: number): Observable<OrdreProduction> {
    return this.http.get<OrdreProduction>(`${this.baseUrl}/${id}`);
  }

  create(ordre: Partial<OrdreProduction>): Observable<OrdreProduction> {
    return this.http.post<OrdreProduction>(this.baseUrl, ordre);
  }

  update(id: number, ordre: Partial<OrdreProduction>): Observable<OrdreProduction> {
    return this.http.put<OrdreProduction>(`${this.baseUrl}/${id}`, ordre);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getByProduit(produitId: number): Observable<OrdreProduction[]> {
    return this.http.get<OrdreProduction[]>(`${this.baseUrl}/produit/${produitId}`);
  }

  getByStatut(statut: string): Observable<OrdreProduction[]> {
    return this.http.get<OrdreProduction[]>(`${this.baseUrl}/statut/${statut}`);
  }

  changerStatut(id: number, statut: string): Observable<OrdreProduction> {
    let params = new HttpParams().set('statut', statut);
    return this.http.patch<OrdreProduction>(`${this.baseUrl}/${id}/statut`, {}, { params });
  }

  demarrer(id: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${id}/demarrer`, {});
  }

  terminer(id: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${id}/terminer`, {});
  }

  annuler(id: number, raison: string): Observable<void> {
    let params = new HttpParams().set('raison', raison);
    return this.http.post<void>(`${this.baseUrl}/${id}/annuler`, {}, { params });
  }

  getEnRetard(): Observable<OrdreProduction[]> {
    return this.http.get<OrdreProduction[]>(`${this.baseUrl}/en-retard`);
  }

  getCoutPeriode(debut: string, fin: string): Observable<number> {
    let params = new HttpParams().set('debut', debut).set('fin', fin);
    return this.http.get<number>(`${this.baseUrl}/cout-periode`, { params });
  }

  getQuantiteProduite(produitId: number, debut: string, fin: string): Observable<number> {
    let params = new HttpParams()
      .set('produitId', produitId.toString())
      .set('debut', debut)
      .set('fin', fin);
    return this.http.get<number>(`${this.baseUrl}/quantite-produite`, { params });
  }

  dupliquer(id: number): Observable<OrdreProduction> {
    return this.http.post<OrdreProduction>(`${this.baseUrl}/${id}/dupliquer`, {});
  }

  consommerIngredients(id: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${id}/consommer-ingredients`, {});
  }
}
