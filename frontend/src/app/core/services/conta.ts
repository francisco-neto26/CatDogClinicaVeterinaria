import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ContaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/contas`;

  findAll(clienteId?: number): Observable<any[]> {
    let params = new HttpParams();
    if (clienteId) {
        params = params.set('clienteId', clienteId.toString());
    }
    return this.http.get<any[]>(`${this.apiUrl}/interno`, { params });
  }

  abrirConta(agendamentoId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/interno/abrir/${agendamentoId}`, {});
  }

  adicionarItem(contaId: number, itemServicoId: number, quantidade: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/interno/${contaId}/itens`, { itemServicoId, quantidade });
  }

  removerItem(contaId: number, itemId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/interno/${contaId}/itens/${itemId}`);
  }

  fecharConta(contaId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/interno/${contaId}/fechar`, {});
  }

  findById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }
}