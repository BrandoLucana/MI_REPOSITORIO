// app.config.ts SIN animations
import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
// Remover importación de provideAnimations
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
    // Remover provideAnimations() de aquí
    importProvidersFrom(CommonModule, FormsModule, ReactiveFormsModule)
  ]
};