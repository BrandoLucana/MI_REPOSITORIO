// src/app/components/corresponsal-register/corresponsal-register.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CorresponsalService } from '../../services/corresponsal.service';

@Component({
  selector: 'app-corresponsal-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './corresponsal-register.component.html',
  styleUrls: ['./corresponsal-register.component.css']
})
export class CorresponsalRegisterComponent {
  fullName = '';
  numeroDocumento = '';
  ubigeo = '';
  centroPoblado = '';
  loading = false;
  errorMessage = '';

  constructor(
    private corresponsalService: CorresponsalService,
    private router: Router
  ) {}

  validarNombre(nombre: string): string[] {
    const errores: string[] = [];
    if (!nombre || nombre.trim().length < 3) {
      errores.push('El nombre debe tener al menos 3 caracteres');
    }
    if (/\d/.test(nombre)) {
      errores.push('El nombre no puede contener números');
    }
    if (/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s]/.test(nombre)) {
      errores.push('El nombre no puede contener caracteres especiales');
    }
    if (/\s{2,}/.test(nombre)) {
      errores.push('El nombre no puede tener espacios en blanco consecutivos');
    }
    return errores;
  }

  validarDocumento(doc: string): string[] {
    const errores: string[] = [];
    if (!doc) {
      errores.push('El DNI es requerido');
    } else {
      if (doc.length < 8 || doc.length > 12) {
        errores.push('El DNI debe tener entre 8 y 12 dígitos');
      }
      if (!/^\d+$/.test(doc)) {
        errores.push('El DNI solo puede contener números');
      }
    }
    return errores;
  }

  validarUbigeo(ubigeo: string): string[] {
    const errores: string[] = [];
    if (!ubigeo || ubigeo.length !== 6) {
      errores.push('El ubigeo debe tener exactamente 6 dígitos');
    } else if (!/^\d{6}$/.test(ubigeo)) {
      errores.push('El ubigeo solo puede contener números');
    }
    return errores;
  }

  validarCentroPoblado(centro: string): string[] {
    const errores: string[] = [];
    if (!centro || centro.trim().length < 3) {
      errores.push('El centro poblado debe tener al menos 3 caracteres');
    }
    if (/[^a-zA-ZáéíóúÁÉÍÓÚñÑ\s\d.,-]/.test(centro)) {
      errores.push('El centro poblado contiene caracteres no permitidos');
    }
    if (/\s{2,}/.test(centro)) {
      errores.push('El centro poblado no puede tener espacios en blanco consecutivos');
    }
    return errores;
  }

  onSubmit(): void {
    // Validar todos los campos
    const nombreErrores = this.validarNombre(this.fullName);
    const docErrores = this.validarDocumento(this.numeroDocumento);
    const ubigeoErrores = this.validarUbigeo(this.ubigeo);
    const centroErrores = this.validarCentroPoblado(this.centroPoblado);
    
    const todosErrores = [...nombreErrores, ...docErrores, ...ubigeoErrores, ...centroErrores];
    
    if (todosErrores.length > 0) {
      alert('Errores en el formulario:\n' + todosErrores.join('\n'));
      return;
    }

    const corresponsal = {
      fullName: this.fullName.trim().replace(/\s{2,}/g, ' '),
      numeroDocumento: parseInt(this.numeroDocumento),
      ubigeo: this.ubigeo,
      centroPoblado: this.centroPoblado.trim().replace(/\s{2,}/g, ' ')
    };

    this.loading = true;
    this.corresponsalService.registrar(corresponsal).subscribe({
      next: () => {
        alert('✅ Corresponsal registrado exitosamente');
        this.router.navigate(['/corresponsales']);
      },
      error: (err) => {
        console.error('Error:', err);
        this.errorMessage = 'Error al registrar corresponsal';
        this.loading = false;
      }
    });
  }

  cancelar(): void {
    if (confirm('¿Cancelar registro?')) {
      this.router.navigate(['/corresponsales']);
    }
  }

  onNombreChange(): void {
    this.fullName = this.fullName.trim().replace(/\s{2,}/g, ' ');
  }

  onDocumentoChange(): void {
    this.numeroDocumento = this.numeroDocumento.replace(/\D/g, '');
  }

  onUbigeoChange(): void {
    this.ubigeo = this.ubigeo.replace(/\D/g, '');
    if (this.ubigeo.length > 6) {
      this.ubigeo = this.ubigeo.substring(0, 6);
    }
  }

  onCentroPobladoChange(): void {
    this.centroPoblado = this.centroPoblado.trim().replace(/\s{2,}/g, ' ');
  }
}