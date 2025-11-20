import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AnimalService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/animais`;

  findAll(page: number = 0, size: number = 10): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'id,desc');

    return this.http.get<any>(this.apiUrl, { params });
  }

  findById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  create(data: any, file?: File): Observable<any> {
    const formData = new FormData();
    
    const animalBlob = new Blob([JSON.stringify(data)], { type: 'application/json' });
    formData.append('animal', animalBlob);

    if (file) {
      formData.append('foto', file);
    }

    return this.http.post(this.apiUrl, formData);
  }

  update(id: number, data: any, file?: File): Observable<any> {
    const formData = new FormData();
    const animalBlob = new Blob([JSON.stringify(data)], { type: 'application/json' });
    formData.append('animal', animalBlob);

    if (file) {
      formData.append('foto', file);
    }

    return this.http.put(`${this.apiUrl}/${id}`, formData);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}