import { Directive, EventEmitter, HostListener, Input, Output } from '@angular/core';

@Directive({
  selector: '[appConfirm]',
  standalone: true
})
export class ConfirmDirective {
  @Input('appConfirm') message: string = 'Êtes-vous sûr ?';
  @Output() confirmed = new EventEmitter<void>();

  @HostListener('click', ['$event'])
  onClick(event: Event) {
    if (confirm(this.message)) {
      this.confirmed.emit();
    }
  }
}
