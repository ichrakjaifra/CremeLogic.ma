import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CommandeAchat } from '../models/commande-achat.model';

@Injectable({
  providedIn: 'root'
})
export class CommandeAchatService {
  private baseUrl = `${environment.apiUrl}/commandes-achat`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<CommandeAchat[]> {
    return this.http.get<CommandeAchat[]>(this.baseUrl);
  }

  getById(id: number): Observable<CommandeAchat> {
    return this.http.get<CommandeAchat>(`${this.baseUrl}/${id}`);
  }

  create(commande: Partial<CommandeAchat>): Observable<CommandeAchat> {
    return this.http.post<CommandeAchat>(this.baseUrl, commande);
  }

  update(id: number, commande: Partial<CommandeAchat>): Observable<CommandeAchat> {
    return this.http.put<CommandeAchat>(`${this.baseUrl}/${id}`, commande);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getByFournisseur(fournisseurId: number): Observable<CommandeAchat[]> {
    return this.http.get<CommandeAchat[]>(`${this.baseUrl}/fournisseur/${fournisseurId}`);
  }

  getByStatut(statut: string): Observable<CommandeAchat[]> {
    return this.http.get<CommandeAchat[]>(`${this.baseUrl}/statut/${statut}`);
  }

  changerStatut(id: number, statut: string): Observable<CommandeAchat> {
    let params = new HttpParams().set('statut', statut);
    return this.http.patch<CommandeAchat>(`${this.baseUrl}/${id}/statut`, {}, { params });
  }

  recevoir(id: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${id}/recevoir`, {});
  }

  getEnRetard(): Observable<CommandeAchat[]> {
    return this.http.get<CommandeAchat[]>(`${this.baseUrl}/en-retard`);
  }

  getMontantPeriode(debut: string, fin: string): Observable<number> {
    let params = new HttpParams().set('debut', debut).set('fin', fin);
    return this.http.get<number>(`${this.baseUrl}/montant-periode`, { params });
  }

  annuler(id: number, raison: string): Observable<void> {
    let params = new HttpParams().set('raison', raison);
    return this.http.post<void>(`${this.baseUrl}/${id}/annuler`, {}, { params });
  }

  dupliquer(id: number): Observable<CommandeAchat> {
    return this.http.post<CommandeAchat>(`${this.baseUrl}/${id}/dupliquer`, {});
  }
}
