import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AgendamentoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/agendamentos`;

  // Método unificado
  create(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, data);
  }

  listMine(page: number = 0, size: number = 10): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'dataHora,desc');
    return this.http.get<any>(`${this.apiUrl}/cliente`, { params });
  }

  cancel(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/cliente/${id}/cancelar`, {});
  }

  listAll(page: number = 0, size: number = 10): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'dataHora,desc');
    return this.http.get<any>(`${this.apiUrl}/interno`, { params });
  }

  assignVeterinarian(agendamentoId: number, funcionarioId: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/interno/${agendamentoId}/atribuir/${funcionarioId}`, {});
  }

  complete(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/interno/${id}/concluir`, {});
  }

  findById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }
}