import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { ValentineComponent } from './valentine/valentine.component';
import { CelebrationComponent } from './celebration/celebration';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'valentine', component: ValentineComponent },
  { path: 'celebration', component: CelebrationComponent }
];
