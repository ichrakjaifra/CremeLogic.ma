import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AdminStats, ChefStats, MagasinierStats, EmployeStats } from '../models/dashboard.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private baseUrl = `${environment.apiUrl}/dashboard`;

  constructor(private http: HttpClient) { }

  getAdminStats(): Observable<AdminStats> {
    return this.http.get<ApiResponse<AdminStats>>(`${this.baseUrl}/admin`).pipe(
      map(response => response.data)
    );
  }

  getChefStats(): Observable<ChefStats> {
    return this.http.get<ApiResponse<ChefStats>>(`${this.baseUrl}/chef`).pipe(
      map(response => response.data)
    );
  }

  getMagasinierStats(): Observable<MagasinierStats> {
    return this.http.get<ApiResponse<MagasinierStats>>(`${this.baseUrl}/magasinier`).pipe(
      map(response => response.data)
    );
  }

  getEmployeStats(): Observable<EmployeStats> {
    return this.http.get<ApiResponse<EmployeStats>>(`${this.baseUrl}/employe`).pipe(
      map(response => response.data)
    );
  }
}
