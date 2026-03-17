import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { UserListComponent } from './features/users/user-list/user-list.component';
import { ExpeditionsListComponent } from './features/expeditions/expeditions-list/expeditions-list.component';
import { AddExpeditionComponent } from './features/expeditions/add-expedition/add-expedition.component';
import { ViewExpeditionComponent } from './features/expeditions/view-expedition/view-expedition.component';
import { RoleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent },
  { 
    path: 'dashboard/users', 
    component: UserListComponent,
    canActivate: [RoleGuard],
    data: { roles: ['ADMIN', 'RESPONSABLE_MODIFICATION'] }
  },
  { path: 'dashboard/expeditions', component: ExpeditionsListComponent },
  { path: 'dashboard/expeditions/add', component: AddExpeditionComponent },
  { path: 'dashboard/expeditions/:id', component: ViewExpeditionComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];
