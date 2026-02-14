import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-valentine',
  standalone: true,
  templateUrl: './valentine.component.html',
  styleUrl: './valentine.component.css'
})
export class ValentineComponent implements OnInit {

  private router = inject(Router);

  noButtonClickCount = 0;
  noButtonState = 0;

  ngOnInit() {
    this.initialize();
  }

  initialize() {
    // Mostrar el gif inicial
    const gifContainer = document.getElementById('gifContainer');
    if (gifContainer) gifContainer.style.display = 'block';

    const siBtn = document.getElementById('siBtn');
    const noBtn = document.getElementById('noBtn');

    if (siBtn) {
      siBtn.addEventListener('click', () => this.onSiClick());
    }

    if (noBtn) {
      noBtn.addEventListener('click', () => this.onNoClick());
    }
  }

  onSiClick() {
    // Ocultar el gif triste y mostrar el gif feliz
    this.hideElement('sadGifContainer');
    this.hideElement('sadGifContainer1');
    this.hideElement('sadGifContainer2');
    this.hideElement('gifContainer');
    this.showElement('happyGifContainer');

    // Ocultar los botones y pregunta
    this.hideElement('question');
    this.hideElement('siBtn');
    document.body.classList.add('bg-green');
    this.hideElement('noBtn');

    // Mostrar el mensaje específico
    this.showElement('messageContainer');
    const messageContainer = document.getElementById('messageContainer');
    if (messageContainer) messageContainer.innerHTML = '¡Oh Siii! jajaja';

    // Mostrar otros gifs después de timeouts
    setTimeout(() => {
      this.hideElement('happyGifContainer');
      this.showElement('happyGifContainer2');
    }, 1000);

    setTimeout(() => {
      this.hideElement('happyGifContainer2');
      this.showElement('happyGifContainer3');
    }, 2000);

    setTimeout(() => {
      this.hideElement('happyGifContainer3');
      this.showElement('happyGifContainer4');
    }, 3000);

    setTimeout(() => {
      this.router.navigate(['/celebration']);
    }, 3000);
  }

  onNoClick() {
    this.noButtonClickCount++;
    if (this.noButtonClickCount === 1) {
      // Primer NO: mostrar gatito llorando
      this.hideElement('gifContainer');
      this.showElement('sadGifContainer');
      this.updateNoBtn('¡Oh no! ¿Estás segura?', '#F1330A');
      this.growSiBtn('40px', '20px 40px');
    } else if (this.noButtonClickCount >= 2) {
      // Segundo NO o más: mostrar gatito llorando2 y quedarse
      this.hideElement('sadGifContainer');
      this.showElement('sadGifContainer2');
      this.updateNoBtn('¡¿Realmente estas segura?!', '#F1330A');
      this.growSiBtn('50px', '30px 50px');
      // No cambiar más, se queda en llorando2
    }
  }

  private hideElement(id: string) {
    const el = document.getElementById(id);
    if (el) el.style.display = 'none';
  }

  private showElement(id: string) {
    const el = document.getElementById(id);
    if (el) el.style.display = 'block';
  }

  private updateNoBtn(text: string, color: string) {
    const noBtn = document.getElementById('noBtn') as HTMLButtonElement;
    if (noBtn) {
      noBtn.innerHTML = text;
      noBtn.style.backgroundColor = color;
    }
  }

  private growSiBtn(fontSize: string, padding: string) {
    const siBtn = document.getElementById('siBtn') as HTMLButtonElement;
    if (siBtn) {
      siBtn.style.fontSize = fontSize;
      siBtn.style.padding = padding;
    }
  }
}