import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReferenceData } from '../models/ReferenceData.model';



@Injectable({
  providedIn: 'root'
})
export class ReferenceDataService {
  private apiUrl = 'http://localhost:8080/api/reference-data';

  constructor(private http: HttpClient) {}

  addReferenceData(data: ReferenceData): Observable<ReferenceData> {
    return this.http.post<ReferenceData>(this.apiUrl, data);
  }

   getAllReferenceData(): Observable<ReferenceData[]> {
      return this.http.get<ReferenceData[]>(this.apiUrl);
    }

    getReferenceDataById(id: string): Observable<ReferenceData> {
      return this.http.get<ReferenceData>(`${this.apiUrl}/${id}`);
    }


  getByCategorie(categorie: string): Observable<ReferenceData[]> {
    return this.http.get<ReferenceData[]>(`${this.apiUrl}/category/${categorie}`);
  }

  toggleStatus(id: number): Observable<ReferenceData> {
    return this.http.patch<ReferenceData>(`${this.apiUrl}/${id}/toggle`, {});
  }

}
