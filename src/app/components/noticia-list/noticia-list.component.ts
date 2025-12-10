// src/app/components/noticia-list/noticia-list.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NoticiaService } from '../../services/noticia.service';
import { Noticia } from '../../models/noticia.model';

@Component({
  selector: 'app-noticia-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './noticia-list.component.html',
  styleUrls: ['./noticia-list.component.css']
})
export class NoticiaListComponent implements OnInit {
  noticias: Noticia[] = [];
  noticiasFiltradas: Noticia[] = [];
  loading = true;
  errorMessage = '';
  filtroEstado: 'todas' | 'activas' | 'inactivas' = 'todas';
  busqueda = '';
  noticiasHoy = 0;
  
  // Para modal de edición
  noticiaEditando?: Noticia;
  editando = false;
  editTitulo = '';
  editContenido = '';
  editFotos: File[] = [];
  editVideo?: File;
  editPalabrasCount = 0;
  generandoReporte = false;

  constructor(private noticiaService: NoticiaService) {}

  ngOnInit(): void {
    this.cargarNoticias();
  }

  cargarNoticias(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.noticiaService.obtenerTodas().subscribe({
      next: (data: Noticia[]) => {
        this.noticias = data;
        this.calcularNoticiasHoy();
        this.aplicarFiltros();
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error al cargar noticias:', err);
        this.errorMessage = 'Error al cargar noticias';
        this.loading = false;
        
        if (err.status === 0) {
          this.errorMessage = 'Error de conexión con el servidor. Verifica que el backend esté corriendo en http://localhost:8085';
        } else if (err.status === 404) {
          this.errorMessage = 'Endpoint no encontrado. Verifica la URL del API';
        }
      }
    });
  }

  calcularNoticiasHoy(): void {
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);
    
    this.noticiasHoy = this.noticias.filter(n => {
      if (!n.createdAt) return false;
      const fechaNoticia = new Date(n.createdAt);
      fechaNoticia.setHours(0, 0, 0, 0);
      return fechaNoticia.getTime() === hoy.getTime();
    }).length;
  }

  aplicarFiltros(): void {
    let resultado = [...this.noticias];
    
    if (this.filtroEstado === 'activas') {
      resultado = resultado.filter(n => n.active);
    } else if (this.filtroEstado === 'inactivas') {
      resultado = resultado.filter(n => !n.active);
    }
    
    if (this.busqueda.trim()) {
      const termino = this.busqueda.toLowerCase();
      resultado = resultado.filter(n => 
        n.titulo.toLowerCase().includes(termino) ||
        n.contenido.toLowerCase().includes(termino) ||
        (n.corresponsal?.fullName && n.corresponsal.fullName.toLowerCase().includes(termino))
      );
    }
    
    resultado.sort((a, b) => 
      new Date(b.createdAt || '').getTime() - new Date(a.createdAt || '').getTime()
    );
    
    this.noticiasFiltradas = resultado;
  }

  abrirModalEditar(noticia: Noticia): void {
    if (!noticia.id || !noticia.active) {
      alert('No se puede editar noticias inactivas');
      return;
    }
    
    this.noticiaEditando = noticia;
    this.editTitulo = noticia.titulo;
    this.editContenido = noticia.contenido;
    this.editPalabrasCount = this.contarPalabras(noticia.contenido);
    this.editando = true;
    this.editFotos = [];
    this.editVideo = undefined;
  }

  cerrarModalEditar(): void {
    this.editando = false;
    this.noticiaEditando = undefined;
    this.editTitulo = '';
    this.editContenido = '';
    this.editFotos = [];
    this.editVideo = undefined;
  }

  onEditFotosSeleccionadas(event: any): void {
    const files: FileList = event.target.files;
    if (files.length > 3) {
      alert('Máximo 3 fotos permitidas');
      event.target.value = '';
      return;
    }
    
    this.editFotos = [];
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      if (file.type.startsWith('image/')) {
        this.editFotos.push(file);
      }
    }
  }

  onEditVideoSeleccionado(event: any): void {
    const file = event.target.files[0];
    if (file && file.type.startsWith('video/')) {
      this.editVideo = file;
    } else if (file) {
      alert('Por favor, seleccione un archivo de video válido');
      event.target.value = '';
    }
  }

  contarPalabras(texto: string): number {
    if (!texto) return 0;
    return texto.trim().split(/\s+/).length;
  }

  onEditContenidoChange(): void {
    this.editContenido = this.editContenido.trim().replace(/\s{2,}/g, ' ');
    this.editPalabrasCount = this.contarPalabras(this.editContenido);
  }

  validarEdicion(): string[] {
    const errores: string[] = [];
    
    if (!this.editTitulo || this.editTitulo.trim().length < 5) {
      errores.push('El título debe tener al menos 5 caracteres');
    }
    if (this.editTitulo.length > 200) {
      errores.push('El título no puede exceder 200 caracteres');
    }
    if (/[^a-zA-ZáéíóúÁÉÍÓÚñÑ0-9\s.,;:¡!¿?()\-]/.test(this.editTitulo)) {
      errores.push('El título contiene caracteres no permitidos');
    }
    
    const palabras = this.editPalabrasCount;
    if (palabras < 1500) {
      errores.push(`El contenido debe tener al menos 1500 palabras. Actual: ${palabras}`);
    }
    if (palabras > 2500) {
      errores.push(`El contenido no debe exceder 2500 palabras. Actual: ${palabras}`);
    }
    
    return errores;
  }

  guardarEdicion(): void {
    if (!this.noticiaEditando?.id) return;
    
    const errores = this.validarEdicion();
    if (errores.length > 0) {
      alert('Errores en la edición:\n' + errores.join('\n'));
      return;
    }
    
    const formData = new FormData();
    formData.append('titulo', this.editTitulo.trim());
    formData.append('contenido', this.editContenido.trim());
    
    // Agregar nuevas fotos si las hay
    this.editFotos.forEach((foto) => {
      formData.append('fotos', foto);
    });
    
    // Agregar nuevo video si lo hay
    if (this.editVideo) {
      formData.append('video', this.editVideo);
    }
    
    console.log('Actualizando noticia ID:', this.noticiaEditando.id);
    
    this.noticiaService.actualizar(this.noticiaEditando.id, formData).subscribe({
      next: (updated: any) => {
        console.log('Noticia actualizada exitosamente:', updated);
        
        // Actualizar en la lista
        const index = this.noticias.findIndex(n => n.id === this.noticiaEditando?.id);
        if (index !== -1) {
          this.noticias[index] = { ...this.noticias[index], ...updated };
          this.aplicarFiltros();
        }
        
        alert('✅ Noticia actualizada correctamente');
        this.cerrarModalEditar();
      },
      error: (err: any) => {
        console.error('Error al actualizar la noticia:', err);
        
        let mensajeError = '❌ Error al actualizar la noticia';
        
        if (err.status === 0) {
          mensajeError = '❌ Error de conexión con el servidor. Verifica que el backend esté corriendo';
        } else if (err.status === 404) {
          mensajeError = '❌ Endpoint no encontrado. Verifica la configuración del API';
        } else if (err.status === 400) {
          mensajeError = '❌ Datos inválidos: ' + (err.error?.message || 'Verifica la información enviada');
        } else if (err.status === 413) {
          mensajeError = '❌ Archivos demasiado grandes. Reduce el tamaño de las imágenes/video';
        } else if (err.status === 500) {
          mensajeError = '❌ Error interno del servidor';
        }
        
        alert(mensajeError);
      }
    });
  }

  cambiarEstado(noticia: Noticia): void {
    if (!noticia.id) return;
    
    if (noticia.active) {
      if (confirm('¿Está seguro de desactivar esta noticia?')) {
        this.noticiaService.desactivar(noticia.id).subscribe({
          next: () => {
            noticia.active = false;
            this.aplicarFiltros();
            this.calcularNoticiasHoy();
            alert('✅ Noticia desactivada correctamente');
          },
          error: (err: any) => {
            console.error('Error al desactivar:', err);
            alert('❌ Error al desactivar la noticia: ' + (err.error?.message || ''));
          }
        });
      }
    } else {
      this.noticiaService.restaurar(noticia.id).subscribe({
        next: () => {
          noticia.active = true;
          this.aplicarFiltros();
          this.calcularNoticiasHoy();
          alert('✅ Noticia activada correctamente');
        },
        error: (err: any) => {
          console.error('Error al restaurar:', err);
          alert('❌ Error al activar la noticia: ' + (err.error?.message || ''));
        }
      });
    }
  }

  generarReporteCompleto(): void {
    this.generandoReporte = true;
    
    this.noticiaService.generarReporteCompleto().subscribe({
      next: (blob: Blob) => {
        // Crear y descargar el PDF
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
        console.error('Error al generar reporte:', err);
        this.generandoReporte = false;
        
        let mensajeError = '❌ Error al generar el reporte PDF';
        if (err.status === 0) {
          mensajeError = '❌ Error de conexión con el servidor. Verifica que el backend esté corriendo';
        } else if (err.status === 404) {
          mensajeError = '❌ Endpoint /report no encontrado en el backend';
        } else if (err.status === 500) {
          mensajeError = '❌ Error interno al generar el PDF en el servidor';
        }
        
        alert(mensajeError);
      }
    });
  }

  contarActivas(): number {
    return this.noticias.filter(n => n.active).length;
  }

  contarInactivas(): number {
    return this.noticias.filter(n => !n.active).length;
  }

  formatearFecha(fecha: Date | string | undefined): string {
    if (!fecha) return 'N/A';
    try {
      const date = new Date(fecha);
      return date.toLocaleDateString('es-ES') + ' ' + 
             date.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
    } catch (e) {
      return 'Fecha inválida';
    }
  }

  onFiltroChange(): void {
    this.aplicarFiltros();
  }

  onBuscar(): void {
    this.aplicarFiltros();
  }

  limpiarBusqueda(): void {
    this.busqueda = '';
    this.aplicarFiltros();
  }
}