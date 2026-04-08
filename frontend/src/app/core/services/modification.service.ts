import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ModificationService {
  private apiUrl = 'http://localhost:8080/api/modifications';

  constructor(private http: HttpClient) {}

  // 1. Soumettre une demande
  soumettreDemande(payload: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/soumettre`, payload);
  }

  // 2. Traiter une demande (Approuver/Rejeter)
  traiterDemande(id: number, statut: string, agentId: number): Observable<any> {
    const params = new HttpParams()
      .set('statut', statut)
      .set('agentId', agentId.toString());
    return this.http.put(`${this.apiUrl}/${id}/traiter`, {}, { params });
  }

  // 3. Obtenir toutes les modifications
  getAllModifications(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  // 4. Obtenir par ID
  getModificationById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  // 5. Recherche intelligente (N° Expédition ou N° Déclaration)
  searchModifications(critere: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/search?critere=${critere}`);
  }
}