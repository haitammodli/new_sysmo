import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Agence } from '../models/agence.model';

@Injectable({
  providedIn: 'root'
})
export class AgenceService {
  private apiUrl = 'http://localhost:8080/api/agences';

  constructor(private http: HttpClient) {}

  createAgence(agence: Agence): Observable<Agence> {
    return this.http.post<Agence>(this.apiUrl, agence);
  }

  getAgences(): Observable<Agence[]> {
    return this.http.get<Agence[]>(this.apiUrl);
  }

  getAgenceById(code: number): Observable<Agence> {
    return this.http.get<Agence>(`${this.apiUrl}/${code}`);
  }

  searchAgences(query: string): Observable<Agence[]> {
    return this.http.get<Agence[]>(`${this.apiUrl}/search`, { params: { query } });
  }

  updateAgence(code: number, agence: Agence): Observable<Agence> {
    return this.http.put<Agence>(`${this.apiUrl}/${code}`, agence);
  }

  deleteAgence(code: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${code}`);
  }
}