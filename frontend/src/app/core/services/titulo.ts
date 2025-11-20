import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TituloService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/titulos`;

  findAll(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/interno`);
  }

  findById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  darBaixa(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/interno/${id}/baixa`, {});
  }

  cancelar(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/interno/${id}/cancelar`, {});
  }
}