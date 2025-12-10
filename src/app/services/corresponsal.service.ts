// src/app/services/corresponsal.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Corresponsal } from '../models/corresponsal.model';

@Injectable({
  providedIn: 'root'
})
export class CorresponsalService {
  private apiUrl = 'http://localhost:8085/api/corresponsales';

  constructor(private http: HttpClient) {}

  obtenerTodos(): Observable<Corresponsal[]> {
    return this.http.get<Corresponsal[]>(this.apiUrl);
  }

  obtenerActivos(): Observable<Corresponsal[]> {
    return this.http.get<Corresponsal[]>(`${this.apiUrl}/activos`);
  }

  registrar(corresponsal: Corresponsal): Observable<Corresponsal> {
    return this.http.post<Corresponsal>(this.apiUrl, corresponsal);
  }

  actualizar(id: number, corresponsal: Corresponsal): Observable<Corresponsal> {
    return this.http.put<Corresponsal>(`${this.apiUrl}/${id}`, corresponsal);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  restaurar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/restore`, {});
  }
}