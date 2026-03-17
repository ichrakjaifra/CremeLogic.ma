import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AdminStats, ChefStats, MagasinierStats, EmployeStats } from '../models/dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private baseUrl = `${environment.apiUrl}/dashboard`;

  constructor(private http: HttpClient) { }

  getAdminStats(): Observable<AdminStats> {
    return this.http.get<AdminStats>(`${this.baseUrl}/admin`);
  }

  getChefStats(): Observable<ChefStats> {
    return this.http.get<ChefStats>(`${this.baseUrl}/chef`);
  }

  getMagasinierStats(): Observable<MagasinierStats> {
    return this.http.get<MagasinierStats>(`${this.baseUrl}/magasinier`);
  }

  getEmployeStats(): Observable<EmployeStats> {
    return this.http.get<EmployeStats>(`${this.baseUrl}/employe`);
  }
}
