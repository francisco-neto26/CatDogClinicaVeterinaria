import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { TieredMenuModule } from 'primeng/tieredmenu';
import { Store } from '@ngxs/store';
import { AuthState } from '../../../store/auth/auth.state';
import { OpenTab } from '../../../store/layout/layout.actions';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    TieredMenuModule
  ],
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.scss']
})
export class SidebarComponent implements OnInit {
  items: MenuItem[] | undefined;
  userRole: string = '';

  private store = inject(Store);
  private router = inject(Router);

  ngOnInit() {
    this.decodeToken();
    this.buildMenu();
  }

  decodeToken() {
    const token = this.store.selectSnapshot(AuthState.token);
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.userRole = payload.role;
      } catch (e) {
        console.error(e);
      }
    }
  }

  private createMenuItem(label: string, icon: string, routerLink: string): MenuItem {
    return {
      label,
      icon,
      routerLink,
      command: () => {
        this.store.dispatch(new OpenTab({ label, icon, routerLink }));
        this.router.navigate([routerLink]);
      }
    };
  }

  buildMenu() {
    const isFuncionario = this.userRole === 'ROLE_FUNCIONARIO' || this.userRole === 'ROLE_MEDICO_VETERINARIO';

    this.items = [
      this.createMenuItem('Dashboard', 'pi pi-th-large', '/')
    ];

    if (isFuncionario) {
      this.items.push(
        { separator: true },
        {
          label: 'Recepção',
          icon: 'pi pi-users',
          items: [
            this.createMenuItem('Clientes', 'pi pi-id-card', '/clientes'),
            this.createMenuItem('Pacientes', 'pi pi-heart', '/animais') 
          ]
        },
        {
          label: 'Clínica',
          icon: 'pi pi-heart-fill',
          items: [
             this.createMenuItem('Agenda', 'pi pi-calendar', '/agendamentos')
          ]
        },
        {
          label: 'Financeiro',
          icon: 'pi pi-dollar',
          items: [
            this.createMenuItem('Contas', 'pi pi-receipt', '/contas'),
            this.createMenuItem('Títulos', 'pi pi-wallet', '/titulos')
          ]
        },
        {
          label: 'Configurações',
          icon: 'pi pi-cog',
          items: [
            this.createMenuItem('Tipos Serviço', 'pi pi-tags', '/tipos-itens'),
            this.createMenuItem('Catálogo', 'pi pi-list', '/itens-servicos'),
            this.createMenuItem('Usuários', 'pi pi-user-edit', '/usuarios')
          ]
        }
      );
    } else {
      this.items.push(
        { separator: true },
        {
          label: 'Área do Tutor',
          icon: 'pi pi-home',
          items: [          
            this.createMenuItem('Meus Animais', 'pi pi-heart', '/meus-animais'),
            this.createMenuItem('Nova Consulta', 'pi pi-calendar-plus', '/novo-agendamento'),
            this.createMenuItem('Histórico', 'pi pi-history', '/meus-agendamentos')
          ]
        }
      );
    }
  }
}