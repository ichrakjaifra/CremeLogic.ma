import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Ingredient, MouvementStock } from '../models/ingredient.model';

@Injectable({
  providedIn: 'root'
})
export class IngredientService {
  private baseUrl = `${environment.apiUrl}/ingredients`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<Ingredient[]> {
    return this.http.get<Ingredient[]>(this.baseUrl);
  }

  getById(id: number): Observable<Ingredient> {
    return this.http.get<Ingredient>(`${this.baseUrl}/${id}`);
  }

  create(ingredient: Partial<Ingredient>): Observable<Ingredient> {
    return this.http.post<Ingredient>(this.baseUrl, ingredient);
  }

  update(id: number, ingredient: Partial<Ingredient>): Observable<Ingredient> {
    return this.http.put<Ingredient>(`${this.baseUrl}/${id}`, ingredient);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getStockFaible(): Observable<Ingredient[]> {
    return this.http.get<Ingredient[]>(`${this.baseUrl}/stock-faible`);
  }

  getExpirant(): Observable<Ingredient[]> {
    return this.http.get<Ingredient[]>(`${this.baseUrl}/expirant`);
  }

  search(keyword: string): Observable<Ingredient[]> {
    let params = new HttpParams().set('keyword', keyword);
    return this.http.get<Ingredient[]>(`${this.baseUrl}/search`, { params });
  }

  ajusterStock(id: number, quantite: number, type: string, raison: string): Observable<Ingredient> {
    let params = new HttpParams()
      .set('quantite', quantite.toString())
      .set('type', type)
      .set('raison', raison);
    return this.http.post<Ingredient>(`${this.baseUrl}/${id}/ajuster-stock`, {}, { params });
  }

  getConsommationMoyenne(id: number, jours: number = 30): Observable<number> {
    let params = new HttpParams().set('jours', jours.toString());
    return this.http.get<number>(`${this.baseUrl}/${id}/consommation-moyenne`, { params });
  }

  getValeurStockTotal(): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/valeur-stock-total`);
  }

  getByFournisseur(fournisseurId: number): Observable<Ingredient[]> {
    return this.http.get<Ingredient[]>(`${this.baseUrl}/fournisseur/${fournisseurId}`);
  }

  getMouvements(ingredientId?: number): Observable<MouvementStock[]> {
    const url = ingredientId ? `${this.baseUrl}/${ingredientId}/mouvements` : `${environment.apiUrl}/mouvements-stock`;
    return this.http.get<MouvementStock[]>(url);
  }
}
