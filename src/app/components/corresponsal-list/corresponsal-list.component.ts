// src/app/components/corresponsal-list/corresponsal-list.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CorresponsalService } from '../../services/corresponsal.service';
import { Corresponsal } from '../../models/corresponsal.model';

@Component({
  selector: 'app-corresponsal-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './corresponsal-list.component.html',
  styleUrls: ['./corresponsal-list.component.css']
})
export class CorresponsalListComponent implements OnInit {
  corresponsales: Corresponsal[] = [];
  loading = true;
  errorMessage = '';

  constructor(private corresponsalService: CorresponsalService) {}

  ngOnInit(): void {
    this.cargarCorresponsales();
  }

  cargarCorresponsales(): void {
    this.loading = true;
    
    this.corresponsalService.obtenerTodos().subscribe({
      next: (data: Corresponsal[]) => {
        this.corresponsales = data;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error:', err);
        this.errorMessage = 'Error al cargar corresponsales';
        this.loading = false;
      }
    });
  }

  cambiarEstado(corresponsal: Corresponsal): void {
    if (!corresponsal.id) return;
    
    if (corresponsal.isActive) {
      // Desactivar
      if (confirm('¿Desactivar este corresponsal?')) {
        this.corresponsalService.eliminar(corresponsal.id).subscribe({
          next: () => {
            corresponsal.isActive = false;
          },
          error: (err: any) => {
            console.error('Error:', err);
            alert('Error al desactivar');
          }
        });
      }
    } else {
      // Restaurar
      this.corresponsalService.restaurar(corresponsal.id).subscribe({
        next: () => {
          corresponsal.isActive = true;
        },
        error: (err: any) => {
          console.error('Error:', err);
          alert('Error al restaurar');
        }
      });
    }
  }

  contarActivos(): number {
    return this.corresponsales.filter(c => c.isActive).length;
  }

  contarInactivos(): number {
    return this.corresponsales.filter(c => !c.isActive).length;
  }

  formatearFecha(fecha: Date | string | undefined): string {
    if (!fecha) return 'N/A';
    return new Date(fecha).toLocaleDateString('es-ES');
  }
}