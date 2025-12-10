// src/app/components/noticia-create/noticia-create.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NoticiaService } from '../../services/noticia.service';
import { CorresponsalService } from '../../services/corresponsal.service';
import { Corresponsal } from '../../models/corresponsal.model';

@Component({
  selector: 'app-noticia-create',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './noticia-create.component.html',
  styleUrls: ['./noticia-create.component.css']
})
export class NoticiaCreateComponent implements OnInit {
  titulo = '';
  contenido = '';
  corresponsalId?: number;
  corresponsales: Corresponsal[] = [];
  fotos: File[] = [];
  video?: File;
  loading = false;
  errorMessage = '';
  palabrasCount = 0;

  constructor(
    private noticiaService: NoticiaService,
    private corresponsalService: CorresponsalService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarCorresponsales();
  }

  cargarCorresponsales(): void {
    this.corresponsalService.obtenerActivos().subscribe({
      next: (data: Corresponsal[]) => {
        this.corresponsales = data;
      },
      error: (err: any) => {
        console.error('Error:', err);
        this.errorMessage = 'Error al cargar corresponsales';
      }
    });
  }

  onTituloChange(): void {
    this.titulo = this.titulo.trim().replace(/\s{2,}/g, ' ');
  }

  onContenidoChange(): void {
    this.contenido = this.contenido.trim().replace(/\s{2,}/g, ' ');
    this.palabrasCount = this.contarPalabras();
  }

  onFotosSeleccionadas(event: any): void {
    const files: FileList = event.target.files;
    if (files.length > 3) {
      alert('Máximo 3 fotos permitidas');
      event.target.value = '';
      return;
    }
    
    this.fotos = [];
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      if (!file.type.startsWith('image/')) {
        alert(`El archivo ${file.name} no es una imagen válida`);
        continue;
      }
      this.fotos.push(file);
    }
  }

  onVideoSeleccionado(event: any): void {
    const file = event.target.files[0];
    if (file && file.type.startsWith('video/')) {
      this.video = file;
    } else if (file) {
      alert('Por favor, seleccione un archivo de video válido');
      event.target.value = '';
    }
  }

  contarPalabras(): number {
    if (!this.contenido) return 0;
    return this.contenido.trim().split(/\s+/).length;
  }

  validarFormulario(): string[] {
    const errores: string[] = [];
    
    // Validar título
    if (!this.titulo || this.titulo.trim().length < 5) {
      errores.push('El título debe tener al menos 5 caracteres');
    }
    if (this.titulo.length > 200) {
      errores.push('El título no puede exceder 200 caracteres');
    }
    if (/[^a-zA-ZáéíóúÁÉÍÓÚñÑ0-9\s.,;:¡!¿?()\-]/.test(this.titulo)) {
      errores.push('El título contiene caracteres no permitidos');
    }
    if (/\s{2,}/.test(this.titulo)) {
      errores.push('El título no puede tener espacios en blanco consecutivos');
    }
    
    // Validar contenido
    const palabras = this.palabrasCount;
    if (palabras < 1500) {
      errores.push(`El contenido debe tener al menos 1500 palabras. Actual: ${palabras}`);
    }
    if (palabras > 2500) {
      errores.push(`El contenido no debe exceder 2500 palabras. Actual: ${palabras}`);
    }
    if (!this.contenido.trim()) {
      errores.push('El contenido es requerido');
    }
    
    // Validar corresponsal
    if (!this.corresponsalId) {
      errores.push('Seleccione un corresponsal');
    }
    
    // Validar fotos
    if (this.fotos.length < 1) {
      errores.push('Se requiere al menos 1 foto');
    }
    
    return errores;
  }

  onSubmit(): void {
    const errores = this.validarFormulario();
    if (errores.length > 0) {
      alert('Errores en el formulario:\n' + errores.join('\n'));
      return;
    }

    const formData = new FormData();
    formData.append('titulo', this.titulo.trim());
    formData.append('contenido', this.contenido.trim());
    formData.append('corresponsalId', this.corresponsalId!.toString());
    
    // NO enviar publishDate - será automático del backend

    // Agregar fotos
    this.fotos.forEach((foto) => {
      formData.append('fotos', foto);
    });

    // Agregar video (opcional)
    if (this.video) {
      formData.append('video', this.video);
    }

    this.loading = true;
    this.noticiaService.crearNoticia(formData).subscribe({
      next: () => {
        alert('✅ Noticia creada exitosamente');
        this.router.navigate(['/noticias']);
      },
      error: (err: any) => {
        console.error('Error:', err);
        this.errorMessage = err.error?.message || 'Error al crear noticia';
        this.loading = false;
      }
    });
  }

  cancelar(): void {
    if (confirm('¿Cancelar creación de noticia?')) {
      this.router.navigate(['/noticias']);
    }
  }

  limpiarFotos(): void {
    this.fotos = [];
    const input = document.getElementById('fotosInput') as HTMLInputElement;
    if (input) input.value = '';
  }

  limpiarVideo(): void {
    this.video = undefined;
    const input = document.getElementById('videoInput') as HTMLInputElement;
    if (input) input.value = '';
  }
}