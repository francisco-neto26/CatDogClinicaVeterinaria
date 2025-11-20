import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { AvatarModule } from 'primeng/avatar';
import { UserService } from '../../../core/services/user';

@Component({
  selector: 'app-cliente-list',
  standalone: true,
  imports: [CommonModule, TableModule, AvatarModule],
  template: `
    <div class="surface-ground px-4 py-5 md:px-6 lg:px-8 min-h-screen">
        <div class="surface-card p-4 shadow-2 border-round">
            <div class="mb-4">
                <div class="text-900 font-medium text-3xl">Clientes</div>
                <div class="text-500 mt-1">Lista de Tutores cadastrados</div>
            </div>
            <p-table [value]="clientes()" [loading]="loading()" responsiveLayout="scroll">
                <ng-template #header>
                    <tr>
                        <th>Cliente</th>
                        <th>Email</th>
                        <th>Telefone</th>
                        <th>CPF</th>
                    </tr>
                </ng-template>
                <ng-template #body let-user>
                    <tr>
                        <td>
                            <div class="flex align-items-center gap-2">
                                <p-avatar [image]="user.fotoUrl" [icon]="!user.fotoUrl ? 'pi pi-user' : ''" shape="circle" />
                                <span class="font-bold">{{ user.pessoa.nome }}</span>
                            </div>
                        </td>
                        <td>{{ user.email }}</td>
                        <td>{{ user.pessoa.telefone }}</td>
                        <td>{{ user.pessoa.cpfcnpj }}</td>
                    </tr>
                </ng-template>
            </p-table>
        </div>
    </div>
  `
})
export class ClienteListComponent implements OnInit {
    private userService = inject(UserService);
    clientes = signal<any[]>([]);
    loading = signal(true);

    ngOnInit() {
        this.userService.findAll().subscribe(users => {
            // Filtra apenas ROLE_CLIENTE
            this.clientes.set(users.filter((u: any) => u.role.nome === 'CLIENTE'));
            this.loading.set(false);
        });
    }
}
