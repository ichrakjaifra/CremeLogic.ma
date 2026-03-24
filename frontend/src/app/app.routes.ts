import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';

export const routes: Routes = [
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
      },
      { path: '', redirectTo: 'login', pathMatch: 'full' }
    ]
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'admin',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
        loadChildren: () => [
          { path: 'dashboard', loadComponent: () => import('./features/admin/dashboard/dashboard.component').then(m => m.AdminDashboardComponent) },
          { path: 'utilisateurs', loadComponent: () => import('./features/admin/utilisateurs/utilisateurs.component').then(m => m.UtilisateursComponent) },
          { path: 'produits', loadComponent: () => import('./features/chef/produits/produits.component').then(m => m.ProduitsComponent) }
        ]
      },
      {
        path: 'chef',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'CHEF'] },
        loadChildren: () => [
          { path: 'dashboard', loadComponent: () => import('./features/chef/dashboard/dashboard.component').then(m => m.ChefDashboardComponent) },
          { path: 'recettes', loadComponent: () => import('./features/chef/recettes/recettes.component').then(m => m.RecettesComponent) },
          { path: 'produits', loadComponent: () => import('./features/chef/produits/produits.component').then(m => m.ProduitsComponent) },
          { path: 'production', loadComponent: () => import('./features/chef/production/production.component').then(m => m.ProductionComponent) },
          { path: 'ingredients', loadComponent: () => import('./features/magasinier/ingredients/ingredients.component').then(m => m.IngredientsComponent) }
        ]
      },
      {
        path: 'magasinier',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'MAGASINIER'] },
        loadChildren: () => [
          { path: 'dashboard', loadComponent: () => import('./features/magasinier/dashboard/dashboard.component').then(m => m.MagasinierDashboardComponent) },
          { path: 'ingredients', loadComponent: () => import('./features/magasinier/ingredients/ingredients.component').then(m => m.IngredientsComponent) },
          { path: 'fournisseurs', loadComponent: () => import('./features/magasinier/fournisseurs/fournisseurs.component').then(m => m.FournisseursComponent) },
          { path: 'commandes-achat', loadComponent: () => import('./features/magasinier/commandes-achat/commandes-achat.component').then(m => m.CommandesAchatComponent) },
          { path: 'mouvements-stock', loadComponent: () => import('./features/magasinier/mouvements-stock/mouvements-stock.component').then(m => m.MouvementsStockComponent) }
        ]
      },
      {
        path: 'employe',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'EMPLOYE'] },
        loadChildren: () => [
          { path: 'dashboard', loadComponent: () => import('./features/employe/dashboard/dashboard.component').then(m => m.EmployeDashboardComponent) },
          { path: 'ventes', loadComponent: () => import('./features/employe/ventes/ventes.component').then(m => m.VentesComponent) }
        ]
      },
      {
        path: 'profil',
        loadComponent: () => import('./features/profil/profil.component').then(m => m.ProfilComponent)
      },
      { path: '', redirectTo: 'admin/dashboard', pathMatch: 'full' } // Temporary default
    ]
  },
  { path: '**', redirectTo: 'auth/login' }
];
