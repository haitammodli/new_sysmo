import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users';
  private authUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  getUserByEmail(email: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/email/${email}`);
  }

  getUsersByRole(role: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/role/${role}`);
  }

  searchUsers(keyword: string): Observable<User[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<User[]>(`${this.apiUrl}/search`, { params });
  }

  getUsersByAgence(agenceId: number): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/agence/${agenceId}`);
  }

  getBlacklistedClients(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/clients/blacklisted`);
  }

  createUser(userData: any): Observable<any> {
    // We use responseType: 'text' because your Spring Boot AuthController
    // returns a plain String ("User registered successfully!") instead of JSON.
    return this.http.post(`${this.authUrl}/register`, userData, { responseType: 'text' });
  }

  updateUser(id: number, user: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, user);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
