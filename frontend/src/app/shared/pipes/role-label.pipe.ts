import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'roleLabel',
  standalone: true
})
export class RoleLabelPipe implements PipeTransform {
  transform(role: string | undefined): string {
    switch (role) {
      case 'ADMIN': return 'Administrateur';
      case 'CHEF': return 'Chef Pâtissier';
      case 'MAGASINIER': return 'Magasinier';
      case 'EMPLOYE': return 'Employé';
      default: return role || '-';
    }
  }
}
