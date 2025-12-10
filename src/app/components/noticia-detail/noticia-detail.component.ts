// src/app/components/noticia-detail/noticia-detail.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router'; // AGREGAR ESTA IMPORTACIÓN
import { ActivatedRoute } from '@angular/router';
import { NoticiaService } from '../../services/noticia.service';
import { Noticia } from '../../models/noticia.model';

@Component({
  selector: 'app-noticia-detail',
  standalone: true,
  imports: [CommonModule, RouterModule], // AGREGAR RouterModule AQUÍ
  templateUrl: './noticia-detail.component.html',
  styleUrls: ['./noticia-detail.component.css']
})
export class NoticiaDetailComponent implements OnInit {
  noticia?: Noticia;
  loading = true;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private noticiaService: NoticiaService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.cargarNoticia(parseInt(id));
    }
  }

  cargarNoticia(id: number): void {
    this.loading = true;
    this.noticiaService.obtenerPorId(id).subscribe({
      next: (data: any) => {
        this.noticia = data;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error:', err);
        this.errorMessage = 'Error al cargar la noticia';
        this.loading = false;
      }
    });
  }

  contarPalabras(texto: string): number {
    if (!texto) return 0;
    return texto.trim().split(/\s+/).length;
  }

  formatearFecha(fecha: Date | string | undefined): string {
    if (!fecha) return 'N/A';
    const date = new Date(fecha);
    return date.toLocaleDateString('es-ES') + ' ' + 
           date.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
  }
}