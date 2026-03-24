import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { UserListComponent } from './features/users/user-list/user-list.component';
import { ExpeditionsListComponent } from './features/expeditions/expeditions-list/expeditions-list.component';
import { AddExpeditionComponent } from './features/expeditions/add-expedition/add-expedition.component';
import { ViewExpeditionComponent } from './features/expeditions/view-expedition/view-expedition.component';
import { RoleGuard } from './core/guards/role.guard';
import { ReferenceManagementComponent } from './features/reference-data/reference-management/reference-management.component';
import { DashboardOverviewComponent } from './features/dashboard/dashboard-overview/dashboard-overview.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { 
    path: 'dashboard', 
    component: DashboardComponent,
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      { path: 'overview', component: DashboardOverviewComponent },
      { 
        path: 'users', 
        component: UserListComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN', 'RESPONSABLEMODIFICATION'] }
      },
      { path: 'expeditions', component: ExpeditionsListComponent },
      { path: 'expeditions/add', component: AddExpeditionComponent },
      { path: 'expeditions/:id', component: ViewExpeditionComponent },
      { path: 'reference-data', component: ReferenceManagementComponent }
    ]
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];
