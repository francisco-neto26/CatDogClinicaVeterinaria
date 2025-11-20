import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login';
import { RegisterComponent } from './features/auth/register/register';
import { DashboardComponent } from './features/dashboard/dashboard';
import { authGuard } from './core/guards/auth-guard';
import { ProfileComponent } from './features/profile/profile';


export const routes: Routes = [

    {
        path: '',
        component: DashboardComponent,
        canActivate: [authGuard]
    },
    {
        path: 'login',
        component: LoginComponent

    },
    {
        path: 'register',
        component: RegisterComponent

    },
    {
        path: 'perfil',
        component: ProfileComponent,
        canActivate: [authGuard]
    },

    {
        path: '**',
        redirectTo: ''
    }

];