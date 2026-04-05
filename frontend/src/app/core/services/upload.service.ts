import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class UploadService {
  private baseUrl = `${environment.apiUrl}/upload`;

  constructor(private http: HttpClient) { }

  uploadImage(file: File): Observable<{ url: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ApiResponse<string>>(this.baseUrl, formData).pipe(
      map(response => ({ url: response.data }))
    );
  }
}
