import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  name: string = '';
  showQuestion: boolean = false;
  currentGif: string = 'gatitofeliz.gif';
  noCount: number = 0;
  showName: boolean = false;
  noButtonText: string = 'No';
  siBtnFontSize: string = '16px';
  siBtnPadding: string = '10px 20px';
  noBtnBackground: string = '#FF5733';
  siBtnBackground: string = '#009705';
  showHappy1: boolean = false;
  showHappy2: boolean = false;
  showHappy3: boolean = false;
  showHappy4: boolean = false;
  showError: boolean = false;
  errorMessage: string = '';
  showCelebration: boolean = false;

  private noMessages: string[] = [
    '¡Oh no! ¿Estás segura?',
    '¡¿Realmente estas segura?!',
    'Estás segura de verdad, ¿eh?',
    '¿Eres positiva?',
    'se lo dire a mi mamà',
    'Solo piensa en ello',
    'Si dices que no estaré muy triste',
    'Estaré muy triste',
    'Estaré muy muy muy triste',
    'Estaré muy muy muy muy triste.',
    'Vale, ya dejaré de preguntar...',
    'Es broma, POR FAVOR DI SÍ',
    'Estaré muy muy muy muy muy triste.',
    'Estás rompiendo mi corazón :(',
    'NO... ya di que si',
    'Anda Siiiiiiiiiiiiiiiiiiiiiiiiiii',
    'por favooooooor',
    'yo te amo',
    'ah,te engañe',
    'Anda Siiiiiiiiiiiiiiiiiiiiiiiiiii',
    'no me ire hasta que digas que si',
    'por favooooooor'

  ];

  constructor(private router: Router) {}

  onSubmit() {
    if (this.name.toLowerCase() === 'jassira') {
      this.router.navigate(['/valentine']);
    } else {
      this.showError = true;
      this.errorMessage = '¡Ups! Solo Jassira puede entrar aqui y en mi corazòn ,vete porque ella es celosa 👻';
    }
  }

  closeError() {
    this.showError = false;
    this.name = '';
  }

  onSi() {
    this.showQuestion = false;
    this.showHappy1 = true;

    setTimeout(() => {
      this.showHappy1 = false;
      this.showHappy2 = true;
    }, 1000);

    setTimeout(() => {
      this.showHappy2 = false;
      this.showHappy3 = true;
    }, 2000);

    setTimeout(() => {
      this.showHappy3 = false;
      this.showHappy4 = true;
    }, 3000);

    setTimeout(() => {
      this.showHappy4 = false;
      this.showCelebration = true;
    }, 4000);
  }

  onNo() {
    this.noCount++;
    const gifSequence = ['gatito llornado.gif', 'gatito_llorando-2.gif', 'gatito llornado.gif'];
    
    // Array de tamaños expandido para permitir crecimiento indefinido
    const sizeSteps = [
      { font: '40px', pad: '20px 40px' },
      { font: '50px', pad: '30px 50px' },
      { font: '60px', pad: '40px 60px' },
      { font: '70px', pad: '50px 70px' },
      { font: '80px', pad: '60px 80px' },
      { font: '90px', pad: '70px 90px' },
      { font: '100px', pad: '80px 100px' },
      { font: '120px', pad: '90px 120px' },
      { font: '140px', pad: '100px 140px' },
      { font: '160px', pad: '110px 160px' },
      { font: '180px', pad: '120px 180px' },
      { font: '200px', pad: '130px 200px' },
      { font: '220px', pad: '140px 220px' },
      { font: '240px', pad: '150px 240px' },
      { font: '260px', pad: '160px 260px' },
      { font: '280px', pad: '170px 280px' },
      { font: '300px', pad: '180px 300px' },
      { font: '320px', pad: '200px 320px' },
      { font: '340px', pad: '220px 340px' },
      { font: '360px', pad: '240px 360px' },
      { font: '380px', pad: '260px 380px' },
      { font: '400px', pad: '280px 400px' }
    ];

    // Reciclar los mensajes cuando chegue al final (no resetear)
    const messageIndex = (this.noCount - 1) % this.noMessages.length;
    this.noButtonText = this.noMessages[messageIndex];
    this.currentGif = gifSequence[Math.min(this.noCount - 1, 2)];
    
    // Continuar creciendo los botones indefinidamente
    if (this.noCount <= sizeSteps.length) {
      const step = sizeSteps[this.noCount - 1];
      this.siBtnFontSize = step.font;
      this.siBtnPadding = step.pad;
    } else {
      // Si supera los pasos definidos, continuar creciendo con incrementos
      const baseFontSize = 400;
      const increment = 20;
      const extraSteps = this.noCount - sizeSteps.length;
      this.siBtnFontSize = (baseFontSize + (extraSteps * increment)) + 'px';
      this.siBtnPadding = ((280 + (extraSteps * increment)) + 'px ' + (400 + (extraSteps * increment)) + 'px');
    }
    
    if (this.noCount > 1) {
      this.noBtnBackground = '#F1330A';
    }
  }

  resetAll() {
    this.name = '';
    this.showQuestion = false;
    this.currentGif = 'gatitofeliz.gif';
    this.noCount = 0;
    this.showName = false;
    this.noButtonText = 'No';
    this.siBtnFontSize = '16px';
    this.siBtnPadding = '10px 20px';
    this.noBtnBackground = '#FF5733';
    this.siBtnBackground = '#009705';
    this.showHappy1 = false;
    this.showHappy2 = false;
    this.showHappy3 = false;
    this.showHappy4 = false;
    this.showError = false;
    this.showCelebration = false;
  }
}