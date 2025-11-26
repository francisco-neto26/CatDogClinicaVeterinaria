import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Store } from '@ngxs/store';
import { AuthState } from '../../store/auth/auth.state';
import { Logout } from '../../store/auth/auth.actions';

import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ChartModule } from 'primeng/chart';
import { SkeletonModule } from 'primeng/skeleton';
import { AgendamentoService } from '../../core/services/agendamento';
import { ContaService } from '../../core/services/conta';
import { TituloService } from '../../core/services/titulo';
import { UserService } from '../../core/services/user';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule, 
    RouterModule,
    ButtonModule, 
    CardModule, 
    ChartModule, 
    SkeletonModule
  ],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class DashboardComponent implements OnInit {
  private store = inject(Store);
  private router = inject(Router);
  private agendamentoService = inject(AgendamentoService);
  private contaService = inject(ContaService);
  private tituloService = inject(TituloService);
  private userService = inject(UserService);

  userName = signal('');
  role = signal('');
  loading = signal(true);

  totalAgendamentos = signal(0);
  totalContasAbertas = signal(0);
  valorFinanceiro = signal(0);
  
  proximosAgendamentos = signal<any[]>([]);

  ngOnInit() {
    this.checkUser();
  }

  checkUser() {
    const token = this.store.selectSnapshot(AuthState.token);
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.userName.set(payload.name || payload.sub);
        this.role.set(payload.role);
        
        if (this.role() === 'ROLE_CLIENTE') {
            this.loadClientData();
        } else {
            this.loadAdminData();
        }
      } catch (e) {
        console.error(e);
        this.loading.set(false);
      }
    }
  }

  loadAdminData() {
    // 1. Agendamentos (Busca mais itens para poder filtrar no front)
    this.agendamentoService.listAll(0, 100).subscribe({
        next: (page) => {
            // FILTRO: Apenas AGENDADO
            const agendados = page.content.filter((a: any) => a.status === 'AGENDADO');
            
            this.totalAgendamentos.set(agendados.length);
            // Pega apenas os 5 primeiros para a lista rápida
            this.proximosAgendamentos.set(agendados.slice(0, 5));
        },
        error: () => console.error("Erro ao carregar agenda admin")
    });

    // 2. Contas Abertas
    this.contaService.findAll().subscribe({
        next: (contas) => {
            const abertas = contas.filter(c => c.status === 'ABERTA').length;
            this.totalContasAbertas.set(abertas);
        }
    });

    // 3. Financeiro
    this.tituloService.findAll().subscribe({
        next: (titulos) => {
            const aReceber = titulos
                .filter(t => t.status === 'PENDENTE')
                .reduce((acc, t) => acc + t.valor, 0);
            this.valorFinanceiro.set(aReceber);
            this.loading.set(false);
        },
        error: () => this.loading.set(false)
    });
  }

  loadClientData() {
    this.userService.getMe().subscribe(user => {
        const userId = user.id;

        // 1. Agendamentos (Cliente)
        this.agendamentoService.listMine(0, 100).subscribe({
            next: (page) => {
                // FILTRO: Apenas AGENDADO
                const agendados = page.content.filter((a: any) => a.status === 'AGENDADO');
                
                this.totalAgendamentos.set(agendados.length);
                this.proximosAgendamentos.set(agendados.slice(0, 5));
            }
        });

        // 2. Financeiro
        this.tituloService.findAll(userId).subscribe({
             next: (titulos) => {
                 const meusTitulos = titulos.filter(t => t.clienteId === userId);
                 
                 const aPagar = meusTitulos
                    .filter(t => t.status === 'PENDENTE')
                    .reduce((acc, t) => acc + t.valor, 0);
                 
                 this.valorFinanceiro.set(aPagar);
                 this.loading.set(false);
             },
             error: () => this.loading.set(false)
        });
    });
  }

  logout() {
    this.store.dispatch(new Logout()).subscribe(() => {
      this.router.navigate(['/login']);
    });
  }
}