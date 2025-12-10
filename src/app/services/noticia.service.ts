import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Noticia } from '../models/noticia.model';

@Injectable({
  providedIn: 'root'
})
export class NoticiaService {
  private apiUrl = 'http://localhost:8085/api';

  constructor(private http: HttpClient) {}

  // ✅ AGREGA ESTE MÉTODO QUE FALTA
  crearNoticia(datos: FormData): Observable<Noticia> {
    return this.http.post<Noticia>(`${this.apiUrl}/noticias`, datos);
  }

  // Los otros métodos que ya tienes...
  obtenerTodas(): Observable<Noticia[]> {
    return this.http.get<Noticia[]>(`${this.apiUrl}/noticias`);
  }

  obtenerPorId(id: number): Observable<Noticia> {
    return this.http.get<Noticia>(`${this.apiUrl}/noticias/${id}`);
  }

  actualizar(id: number, datos: FormData): Observable<Noticia> {
    return this.http.put<Noticia>(`${this.apiUrl}/noticias/${id}`, datos);
  }

  // Si ya tienes un método "crear", quizás se llama diferente:
  // Si ya existe "crear()", entonces en el componente usa "crear()" en vez de "crearNoticia()"
  crear(datos: FormData): Observable<Noticia> {
    return this.http.post<Noticia>(`${this.apiUrl}/noticias`, datos);
  }

  desactivar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/noticias/${id}`);
  }

  restaurar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/noticias/${id}/restore`, {});
  }

  generarReporteCompleto(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/noticias/report`, {
      responseType: 'blob'
    });
  }
}