import { Injectable } from '@angular/core';
import { HttpClient ,HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import { Expedition } from '../models/expedition.model';

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
