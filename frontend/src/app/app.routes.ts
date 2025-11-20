import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login';
import { RegisterComponent } from './features/auth/register/register';
import { DashboardComponent } from './features/dashboard/dashboard';
import { authGuard } from './core/guards/auth-guard';
import { ProfileComponent } from './features/profile/profile';
import { AnimalListComponent } from './features/animais/animal-list/animal-list';
import { TipoItemListComponent } from './features/configuracoes/tipo-item-list/tipo-item-list';
import { ItemServicoListComponent } from './features/configuracoes/item-servico-list/item-servico-list';
import { UsuarioListComponent } from './features/configuracoes/usuario-list/usuario-list';
import { AgendamentoListComponent } from './features/clinica/agendamento-list/agendamento-list';
import { ClienteListComponent } from './features/recepcao/cliente-list/cliente-list';
import { ContaListComponent } from './features/financeiro/conta-list/conta-list';
import { TituloListComponent } from './features/financeiro/titulo-list/titulo-list';


export const routes: Routes = [

    { 
        path: '', 
        component: DashboardComponent, 
        canActivate: [authGuard] 
    },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    
    { 
        path: 'perfil', 
        component: ProfileComponent, 
        canActivate: [authGuard] 
    },

    // --- ANIMAIS (Admin e Cliente) ---
    { 
        path: 'animais', 
        component: AnimalListComponent, 
        canActivate: [authGuard] 
    },
    { 
        path: 'meus-animais', 
        component: AnimalListComponent, 
        canActivate: [authGuard] 
    },

    // --- AGENDA / CLÍNICA ---
    { 
        path: 'agendamentos', // Acesso Geral
        component: AgendamentoListComponent, 
        canActivate: [authGuard] 
    },
    { 
        path: 'novo-agendamento', // Atalho Cliente
        component: AgendamentoListComponent, 
        canActivate: [authGuard] 
    },
    { 
        path: 'meus-agendamentos', // Atalho Cliente
        component: AgendamentoListComponent, 
        canActivate: [authGuard] 
    },

    // --- RECEPÇÃO ---
    { 
        path: 'clientes', 
        component: ClienteListComponent, 
        canActivate: [authGuard] 
    },

    // --- FINANCEIRO ---
    { 
        path: 'contas', 
        component: ContaListComponent, 
        canActivate: [authGuard] 
    },
    { 
        path: 'titulos', 
        component: TituloListComponent, 
        canActivate: [authGuard] 
    },

    // --- CONFIGURAÇÕES (ADMIN) ---
    { 
        path: 'tipos-itens', 
        component: TipoItemListComponent, 
        canActivate: [authGuard] 
    },
    { 
        path: 'itens-servicos', 
        component: ItemServicoListComponent, 
        canActivate: [authGuard] 
    },
    { 
        path: 'usuarios', 
        component: UsuarioListComponent, 
        canActivate: [authGuard] 
    },
    {
        path: '**',
        redirectTo: ''
    }
    

];