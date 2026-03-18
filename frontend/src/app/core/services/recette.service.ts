import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Recette } from '../models/recette.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class RecetteService {
  private baseUrl = `${environment.apiUrl}/recettes`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<Recette[]> {
    return this.http.get<ApiResponse<Recette[]>>(this.baseUrl).pipe(
      map(response => response.data || [])
    );
  }

  getById(id: number): Observable<Recette> {
    return this.http.get<ApiResponse<Recette>>(`${this.baseUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  create(recette: Partial<Recette>): Observable<Recette> {
    return this.http.post<ApiResponse<Recette>>(this.baseUrl, recette).pipe(
      map(response => response.data)
    );
  }

  update(id: number, recette: Partial<Recette>): Observable<Recette> {
    return this.http.put<ApiResponse<Recette>>(`${this.baseUrl}/${id}`, recette).pipe(
      map(response => response.data)
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getByCreateur(createurId: number): Observable<Recette[]> {
    return this.http.get<ApiResponse<Recette[]>>(`${this.baseUrl}/createur/${createurId}`).pipe(
      map(response => response.data || [])
    );
  }

  search(keyword: string): Observable<Recette[]> {
    let params = new HttpParams().set('keyword', keyword);
    return this.http.get<ApiResponse<Recette[]>>(`${this.baseUrl}/search`, { params }).pipe(
      map(response => response.data || [])
    );
  }

  dupliquer(id: number): Observable<Recette> {
    return this.http.post<ApiResponse<Recette>>(`${this.baseUrl}/${id}/dupliquer`, {}).pipe(
      map(response => response.data)
    );
  }

  calculerCout(id: number): Observable<number> {
    return this.http.post<ApiResponse<number>>(`${this.baseUrl}/${id}/calculer-cout`, {}).pipe(
      map(response => response.data)
    );
  }

  getByIngredient(ingredientId: number): Observable<Recette[]> {
    return this.http.get<ApiResponse<Recette[]>>(`${this.baseUrl}/ingredient/${ingredientId}`).pipe(
      map(response => response.data || [])
    );
  }

  ajusterPortions(id: number, nouvellesPortions: number): Observable<Recette> {
    let params = new HttpParams().set('nouvellesPortions', nouvellesPortions.toString());
    return this.http.post<ApiResponse<Recette>>(`${this.baseUrl}/${id}/ajuster-portions`, {}, { params }).pipe(
      map(response => response.data)
    );
  }
}
