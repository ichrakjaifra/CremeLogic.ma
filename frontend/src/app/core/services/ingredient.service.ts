import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Ingredient, MouvementStock } from '../models/ingredient.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class IngredientService {
  private baseUrl = `${environment.apiUrl}/ingredients`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<Ingredient[]> {
    return this.http.get<ApiResponse<Ingredient[]>>(this.baseUrl).pipe(
      map(response => response.data || [])
    );
  }

  getById(id: number): Observable<Ingredient> {
    return this.http.get<ApiResponse<Ingredient>>(`${this.baseUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  create(ingredient: Partial<Ingredient>): Observable<Ingredient> {
    return this.http.post<ApiResponse<Ingredient>>(this.baseUrl, ingredient).pipe(
      map(response => response.data)
    );
  }

  update(id: number, ingredient: Partial<Ingredient>): Observable<Ingredient> {
    return this.http.put<ApiResponse<Ingredient>>(`${this.baseUrl}/${id}`, ingredient).pipe(
      map(response => response.data)
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getStockFaible(): Observable<Ingredient[]> {
    return this.http.get<Ingredient[]>(`${this.baseUrl}/stock-faible`);
  }

  getExpirant(): Observable<Ingredient[]> {
    return this.http.get<ApiResponse<Ingredient[]>>(`${this.baseUrl}/expirant`).pipe(
      map(response => response.data || [])
    );
  }

  search(keyword: string): Observable<Ingredient[]> {
    let params = new HttpParams().set('keyword', keyword);
    return this.http.get<ApiResponse<Ingredient[]>>(`${this.baseUrl}/search`, { params }).pipe(
      map(response => response.data || [])
    );
  }

  ajusterStock(id: number, quantite: number, type: string, raison: string): Observable<Ingredient> {
    let params = new HttpParams()
      .set('quantite', quantite.toString())
      .set('type', type)
      .set('raison', raison);
    return this.http.post<ApiResponse<Ingredient>>(`${this.baseUrl}/${id}/ajuster-stock`, {}, { params }).pipe(
      map(response => response.data)
    );
  }

  getConsommationMoyenne(id: number, jours: number = 30): Observable<number> {
    let params = new HttpParams().set('jours', jours.toString());
    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/${id}/consommation-moyenne`, { params }).pipe(
      map(response => response.data?.consommationMoyenne || 0)
    );
  }

  getValeurStockTotal(): Observable<number> {
    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/valeur-stock-total`).pipe(
      map(response => response.data?.valeurStockTotal || 0)
    );
  }

  getByFournisseur(fournisseurId: number): Observable<Ingredient[]> {
    return this.http.get<ApiResponse<Ingredient[]>>(`${this.baseUrl}/fournisseur/${fournisseurId}`).pipe(
      map(response => response.data || [])
    );
  }

  getMouvements(ingredientId?: number): Observable<MouvementStock[]> {
    const url = ingredientId 
      ? `${environment.apiUrl}/stocks/mouvements/ingredient/${ingredientId}` 
      : `${environment.apiUrl}/stocks/mouvements`;
    return this.http.get<MouvementStock[]>(url);
  }

  enregistrerMouvement(request: any): Observable<MouvementStock> {
    const type = request.type.toLowerCase(); // 'entree', 'sortie', 'perte', 'ajustement'
    return this.http.post<MouvementStock>(`${environment.apiUrl}/stocks/mouvements/${type}`, request);
  }

  getStatistiquesMouvement(ingredientId: number, debut: string, fin: string): Observable<any> {
    let params = new HttpParams().set('debut', debut).set('fin', fin);
    return this.http.get<any>(`${environment.apiUrl}/stocks/mouvements/statistiques/${ingredientId}`, { params });
  }
}
