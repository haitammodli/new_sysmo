import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Expedition {
  numeroexpedition?: number;
  numerodeclaration?: number;
  dateCreation?: string;
  dateLivraison?: string;
  statut?: string;
  
  expiditeurId?: number;
  expiditeurNom?: string;
  distinataireId?: number;
  distinataireNom?: string;
  ramasseurId?: number;
  ramasseurNom?: string;
  
  agenceId?: number;
  agenceNom?: string;
  natureId?: number;
  typeId?: number;

  colis?: number;
  poid?: number;
  volume?: number;
  etiquette?: number;
  encombrement?: number;
  valeurDeclaree?: number;
  fond?: number;
  ht?: number;
  tva?: number;
  ttc?: number;
  bl?: boolean;
  numerobl?: string;
  facture?: boolean;
  numerofacture?: string;
  comment?: string;
  ref_regl?: number;
  ps?: number;

  modeReglId?: number;
  portId?: number;
  catprodId?: number;
  creditId?: number;
  unitId?: number;
  livraisonId?: number;
  taxationId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ExpeditionService {
  private apiUrl = 'http://localhost:8080/api/expeditions';

  constructor(private http: HttpClient) {}

  getAllExpeditions(): Observable<Expedition[]> {
    return this.http.get<Expedition[]>(this.apiUrl);
  }

  getExpeditionById(id: number): Observable<Expedition> {
    return this.http.get<Expedition>(`${this.apiUrl}/${id}`);
  }

  createExpedition(data: any): Observable<Expedition> {
    return this.http.post<Expedition>(this.apiUrl, data);
  }
}
