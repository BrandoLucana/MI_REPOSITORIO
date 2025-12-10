// src/app/services/reporte.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private apiUrl = 'http://localhost:8085/api';

  constructor(private http: HttpClient) {}

  // Generar reporte de corresponsales (si existe en backend)
  generarReporteCorresponsales(): Observable<Blob> {
    // Si no hay endpoint específico, puedes usar este genérico
    return this.http.get(`${this.apiUrl}/corresponsales/report`, {
      responseType: 'blob'
    });
  }

  // Método genérico para descargar archivos
  descargarArchivo(url: string, filename: string): void {
    this.http.get(url, { responseType: 'blob' }).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    });
  }
}