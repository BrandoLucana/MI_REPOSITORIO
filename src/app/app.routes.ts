// src/app/app.routes.ts
import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/noticias', pathMatch: 'full' },
  { 
    path: 'crear-noticia', 
    loadComponent: () => import('./components/noticia-create/noticia-create.component')
      .then(m => m.NoticiaCreateComponent) 
  },
  { 
    path: 'noticias', 
    loadComponent: () => import('./components/noticia-list/noticia-list.component')
      .then(m => m.NoticiaListComponent) 
  },
  { 
    path: 'noticia/:id', 
    loadComponent: () => import('./components/noticia-detail/noticia-detail.component')
      .then(m => m.NoticiaDetailComponent) 
  },
  { 
    path: 'registrar-corresponsal', 
    loadComponent: () => import('./components/corresponsal-register/corresponsal-register.component')
      .then(m => m.CorresponsalRegisterComponent) 
  },
  { 
    path: 'corresponsales', 
    loadComponent: () => import('./components/corresponsal-list/corresponsal-list.component')
      .then(m => m.CorresponsalListComponent) 
  },
  { 
    path: 'reportes', 
    loadComponent: () => import('./components/reportes/reportes.component')
      .then(m => m.ReportesComponent) 
  },
  { path: '**', redirectTo: '/noticias' }
];