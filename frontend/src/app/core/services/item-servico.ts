import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ItemServicoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/itens-servicos`;

  findAll(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  findByTipo(tipoItemId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/tipo/${tipoItemId}`);
  }

  create(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/interno`, data);
  }

  update(id: number, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/interno/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/interno/${id}`);
  }
}