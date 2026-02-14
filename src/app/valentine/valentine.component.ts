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
    switch (this.noButtonState) {
      case 0:
        this.hideElement('gifContainer');
        this.showElement('sadGifContainer');
        this.updateNoBtn('¡Oh no! ¿Estás segura?', '#F1330A');
        this.growSiBtn('40px', '20px 40px');
        this.noButtonState++;
        break;
      case 1:
        this.updateNoBtn('¡¿Realmente estas segura?!', '#F1330A');
        this.hideElement('sadGifContainer');
        this.showElement('sadGifContainer2');
        this.growSiBtn('50px', '30px 50px');
        this.noButtonState++;
        break;
      case 2:
        this.updateNoBtn('Estás segura de verdad, ¿eh?', '#F1330A');
        this.hideElement('sadGifContainer');
        this.hideElement('sadGifContainer2');
        this.showElement('sadGifContainer1');
        this.growSiBtn('60px', '40px 60px');
        this.noButtonState++;
        break;
      case 3:
        this.updateNoBtn('¿Eres positiva?', '#F1330A');
        this.growSiBtn('70px', '50px 70px');
        this.noButtonState++;
        break;
      case 4:
        this.updateNoBtn('Di que si por favor?', '#F1330A');
        this.growSiBtn('80px', '60px 80px');
        this.noButtonState++;
        break;
      case 5:
        this.updateNoBtn('Solo piensa en ello', '#F1330A');
        this.growSiBtn('90px', '70px 90px');
        this.noButtonState++;
        break;
      case 6:
        this.updateNoBtn('Si dices que no estaré muy triste', '#F1330A');
        this.growSiBtn('100px', '80px 100px');
        this.noButtonState++;
        break;
      case 7:
        this.updateNoBtn('Estaré muy triste', '#F1330A');
        this.growSiBtn('120px', '90px 120px');
        this.noButtonState++;
        break;
      case 8:
        this.updateNoBtn('Estaré muy muy muy triste', '#F1330A');
        this.growSiBtn('140px', '100px 140px');
        this.noButtonState++;
        break;
      case 9:
        this.updateNoBtn('Estaré muy muy muy muy triste.', '#F1330A');
        this.growSiBtn('160px', '110px 160px');
        this.noButtonState++;
        break;
      case 10:
        this.updateNoBtn('Vale, ya dejaré de preguntar...', '#F1330A');
        this.growSiBtn('180px', '120px 180px');
        this.noButtonState++;
        break;
      case 11:
        this.updateNoBtn('Es broma, POR FAVOR DI SÍ', '#F1330A');
        this.growSiBtn('200px', '130px 200px');
        this.noButtonState++;
        break;
      case 12:
        this.updateNoBtn('Estaré muy muy muy muy muy triste.', '#F1330A');
        this.growSiBtn('220px', '140px 220px');
        this.noButtonState++;
        break;
      case 13:
        this.updateNoBtn('Estás rompiendo mi corazón :(', '#F1330A');
        this.growSiBtn('240px', '150px 240px');
        this.noButtonState++;
        break;
      case 14:
        this.updateNoBtn('NO... ya di que si', '#F1330A');
        this.growSiBtn('260px', '160px 260px');
        this.noButtonState++;
        break;
      case 15:
        this.updateNoBtn('Anda Siiiiiiiiiiiiiiiiiiiiiiiiiii', '#F1330A');
        this.growSiBtn('280px', '170px 280px');
        this.noButtonState++;
        break;
      case 16:
        this.updateNoBtn('por favooooooor', '#F1330A');
        this.hideElement('sadGifContainer');
        this.hideElement('sadGifContainer2');
        this.showElement('gifContainer');
        this.hideElement('happyGifContainer');
        this.noButtonState = 0;
        break;
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