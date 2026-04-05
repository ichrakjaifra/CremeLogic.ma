import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private baseUrl = `${environment.apiUrl}/profil`;

  constructor(private http: HttpClient) { }

  updateProfile(data: any): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/update`, data);
  }

  changePassword(data: any): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/change-password`, data);
  }

  getHistorique(page: number = 0, size: number = 10): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/historique?page=${page}&size=${size}`);
  }
}
