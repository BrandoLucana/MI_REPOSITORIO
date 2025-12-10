// src/app/components/reportes/reportes.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NoticiaService } from '../../services/noticia.service';
import { Noticia } from '../../models/noticia.model';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reportes.component.html',
  styleUrls: ['./reportes.component.css']
})
export class ReportesComponent implements OnInit {
  noticias: Noticia[] = [];
  generandoReporte = false;

  constructor(private noticiaService: NoticiaService) {}

  ngOnInit(): void {
    this.cargarNoticias();
  }

  cargarNoticias(): void {
    this.noticiaService.obtenerTodas().subscribe({
      next: (data: any) => this.noticias = data,
      error: (err: any) => console.error('Error cargando noticias:', err)
    });
  }

  generarReporteCompleto(): void {
    this.generandoReporte = true;
    
    this.noticiaService.generarReporteCompleto().subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `reporte_completo_${new Date().getTime()}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        this.generandoReporte = false;
        alert('📄 Reporte PDF generado y descargado correctamente');
      },
      error: (err: any) => {
        console.error('Error:', err);
        this.generandoReporte = false;
        alert('❌ Error al generar el reporte PDF');
      }
    });
  }

  // Métodos para estadísticas
  contarNoticiasActivas(): number {
    return this.noticias.filter(n => n.active).length;
  }

  contarNoticiasInactivas(): number {
    return this.noticias.filter(n => !n.active).length;
  }

  contarNoticiasHoy(): number {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    
    return this.noticias.filter(n => {
      if (!n.createdAt) return false;
      const fechaNoticia = new Date(n.createdAt);
      fechaNoticia.setHours(0, 0, 0, 0);
      return fechaNoticia.getTime() === hoy.getTime();
    }).length;
  }

  contarPalabras(texto: string): number {
    if (!texto) return 0;
    return texto.trim().split(/\s+/).length;
  }

  formatearFecha(fecha: Date | string | undefined): string {
    if (!fecha) return 'N/A';
    try {
      const date = new Date(fecha);
      return date.toLocaleDateString('es-ES');
    } catch (e) {
      return 'Fecha inválida';
    }
  }

  getFechaHoraActual(): string {
    const ahora = new Date();
    return ahora.toLocaleString('es-ES');
  }
}