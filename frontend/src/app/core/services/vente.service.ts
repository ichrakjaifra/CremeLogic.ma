import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Vente } from '../models/vente.model';

@Injectable({
  providedIn: 'root'
})
export class VenteService {
  private baseUrl = `${environment.apiUrl}/ventes`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<Vente[]> {
    return this.http.get<Vente[]>(this.baseUrl);
  }

  getById(id: number): Observable<Vente> {
    return this.http.get<Vente>(`${this.baseUrl}/${id}`);
  }

  create(vente: Partial<Vente>): Observable<Vente> {
    return this.http.post<Vente>(this.baseUrl, vente);
  }

  annuler(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getByPeriode(debut: string, fin: string): Observable<Vente[]> {
    let params = new HttpParams().set('debut', debut).set('fin', fin);
    return this.http.get<Vente[]>(`${this.baseUrl}/periode`, { params });
  }

  getByCaissier(caissierId: number): Observable<Vente[]> {
    return this.http.get<Vente[]>(`${this.baseUrl}/caissier/${caissierId}`);
  }

  searchByClient(keyword: string): Observable<Vente[]> {
    let params = new HttpParams().set('keyword', keyword);
    return this.http.get<Vente[]>(`${this.baseUrl}/client`, { params });
  }

  getFacture(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${id}/facture`, { responseType: 'blob' });
  }

  getChiffreAffaires(debut: string, fin: string): Observable<number> {
    let params = new HttpParams().set('debut', debut).set('fin', fin);
    return this.http.get<number>(`${this.baseUrl}/chiffre-affaires`, { params });
  }

  getNombreVentes(debut: string, fin: string): Observable<number> {
    let params = new HttpParams().set('debut', debut).set('fin', fin);
    return this.http.get<number>(`${this.baseUrl}/nombre-ventes`, { params });
  }

  getRecentes(limit: number = 10): Observable<Vente[]> {
    let params = new HttpParams().set('limit', limit.toString());
    return this.http.get<Vente[]>(`${this.baseUrl}/recentes`, { params });
  }

  getParCategorie(debut: string, fin: string): Observable<any> {
    let params = new HttpParams().set('debut', debut).set('fin', fin);
    return this.http.get<any>(`${this.baseUrl}/par-categorie`, { params });
  }

  getProduitsPlusVendus(debut: string, fin: string, limit: number = 5): Observable<any> {
    let params = new HttpParams()
      .set('debut', debut)
      .set('fin', fin)
      .set('limit', limit.toString());
    return this.http.get<any>(`${this.baseUrl}/produits-plus-vendus`, { params });
  }
}
