import { Directive, Input, TemplateRef, ViewContainerRef, inject } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';

@Directive({
  selector: '[appHasRole]',
  standalone: true
})
export class HasRoleDirective {
  private templateRef = inject(TemplateRef);
  private viewContainer = inject(ViewContainerRef);
  private authService = inject(AuthService);

  @Input() set appHasRole(roles: string | string[]) {
    const userRole = this.authService.getRole();
    const roleList = Array.isArray(roles) ? roles : [roles];

    if (userRole && roleList.includes(userRole)) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }
}
