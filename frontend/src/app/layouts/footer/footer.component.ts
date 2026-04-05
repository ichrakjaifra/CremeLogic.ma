import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './footer.component.html',
  styles: [`
    .footer {
      font-family: 'Roboto', sans-serif;
    }
  `]
})
export class FooterComponent {}
