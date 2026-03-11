import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { UserListComponent } from './features/users/user-list/user-list.component';
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
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];
