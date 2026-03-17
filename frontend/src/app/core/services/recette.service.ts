import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Recette } from '../models/recette.model';

@Injectable({
  providedIn: 'root'
})
export class RecetteService {
  private baseUrl = `${environment.apiUrl}/recettes`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<Recette[]> {
    return this.http.get<Recette[]>(this.baseUrl);
  }

  getById(id: number): Observable<Recette> {
    return this.http.get<Recette>(`${this.baseUrl}/${id}`);
  }

  create(recette: Partial<Recette>): Observable<Recette> {
    return this.http.post<Recette>(this.baseUrl, recette);
  }

  update(id: number, recette: Partial<Recette>): Observable<Recette> {
    return this.http.put<Recette>(`${this.baseUrl}/${id}`, recette);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getByCreateur(createurId: number): Observable<Recette[]> {
    return this.http.get<Recette[]>(`${this.baseUrl}/createur/${createurId}`);
  }

  search(keyword: string): Observable<Recette[]> {
    let params = new HttpParams().set('keyword', keyword);
    return this.http.get<Recette[]>(`${this.baseUrl}/search`, { params });
  }

  dupliquer(id: number): Observable<Recette> {
    return this.http.post<Recette>(`${this.baseUrl}/${id}/dupliquer`, {});
  }

  calculerCout(id: number): Observable<number> {
    return this.http.post<number>(`${this.baseUrl}/${id}/calculer-cout`, {});
  }

  getByIngredient(ingredientId: number): Observable<Recette[]> {
    return this.http.get<Recette[]>(`${this.baseUrl}/ingredient/${ingredientId}`);
  }

  ajusterPortions(id: number, nouvellesPortions: number): Observable<Recette> {
    let params = new HttpParams().set('nouvellesPortions', nouvellesPortions.toString());
    return this.http.post<Recette>(`${this.baseUrl}/${id}/ajuster-portions`, {}, { params });
  }
}
