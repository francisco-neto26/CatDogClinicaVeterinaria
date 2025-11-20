import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/usuarios`;

  currentUserPhoto = signal<string | null>(null);

  getMe(): Observable<any> {
    return this.http.get(`${this.apiUrl}/me`);
  }

  updateProfile(data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}`, data);
  }

  updatePhoto(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('foto', file);
    return this.http.patch(`${this.apiUrl}/foto`, formData).pipe(
      tap((res: any) => {
        if (res.fotoUrl) {
            this.currentUserPhoto.set(res.fotoUrl);
        }
      })
    );
  }
  
  findAll(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/interno`);
  }

  findById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/interno/${id}`);
  }

  create(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/interno`, data);
  }
}