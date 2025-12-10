import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule],
  template: `
    <nav class="navbar">
      <div class="nav-container">
        <div class="logo">
          <h1>Diario Ciudadano</h1>
        </div>
        <ul class="nav-menu">
          <li><a href="/noticias">📰 Todas Noticias</a></li>
          <li><a href="/crear-noticia">✏️ Crear Noticia</a></li>
          <li><a href="/registrar-corresponsal">👤 Registrar Corresponsal</a></li>
        </ul>
      </div>
    </nav>

    <main>
      <router-outlet></router-outlet>
    </main>

    <footer>
      <p>&copy; 2025 Diario Ciudadano - Plataforma Colaborativa</p>
    </footer>
  `,
  styles: [`
    .navbar {
      background: #1a237e;
      color: white;
      padding: 1rem 0;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    }
    
    .nav-container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 1rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .logo h1 {
      margin: 0;
      font-size: 1.5rem;
    }
    
    .nav-menu {
      display: flex;
      list-style: none;
      margin: 0;
      padding: 0;
      gap: 2rem;
    }
    
    .nav-menu a {
      color: white;
      text-decoration: none;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      transition: background 0.3s;
    }
    
    .nav-menu a:hover {
      background: rgba(255,255,255,0.1);
    }
    
    main {
      min-height: calc(100vh - 140px);
      padding: 2rem 1rem;
    }
    
    footer {
      background: #1a237e;
      color: white;
      text-align: center;
      padding: 1rem;
      margin-top: 2rem;
    }
    
    @media (max-width: 768px) {
      .nav-container {
        flex-direction: column;
        gap: 1rem;
      }
      
      .nav-menu {
        flex-wrap: wrap;
        justify-content: center;
      }
    }
  `]
})
export class AppComponent {
  title = 'Diario Ciudadano';
}