import { Component } from '@angular/core';
import { SeguimientoComponent } from './components/seguimiento/seguimiento.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [SeguimientoComponent],
  template: `<app-seguimiento></app-seguimiento>`,
  styles: [`
    :host { display: block; }
  `]
})
export class App {}
