import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

import { MessageService, ConfirmationService } from 'primeng/api';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TituloService } from '../../../core/services/titulo';

@Component({
  selector: 'app-titulo-list',
  standalone: true,
  imports: [
    CommonModule,
    TableModule,
    ButtonModule,
    TagModule,
    ToastModule,
    ConfirmDialogModule
  ],
  providers: [ConfirmationService],
  templateUrl: './titulo-list.html',
  styleUrls: ['./titulo-list.scss']
})
export class TituloListComponent implements OnInit {
  private tituloService = inject(TituloService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  titulos = signal<any[]>([]);
  allTitulos: any[] = [];
  loading = signal(true);
  
  preSelectedClientId: number | null = null;
  preSelectedClientName: string | null = null;

  ngOnInit() {
    this.loadTitulos();
  }

  loadTitulos() {
    this.loading.set(true);
    this.tituloService.findAll().subscribe({
        next: (data) => {
            this.allTitulos = data;
            
            this.route.queryParams.subscribe(params => {
                if (params['clienteId']) {
                    this.preSelectedClientId = Number(params['clienteId']);
                    this.preSelectedClientName = params['clienteNome'];
                    this.filtrarPorCliente(this.preSelectedClientId);
                } else {
                    this.titulos.set(this.allTitulos);
                }
            });
            
            this.loading.set(false);
        },
        error: () => this.loading.set(false)
    });
  }

  filtrarPorCliente(clienteId: number) {
      const filtrados = this.allTitulos.filter(t => t.clienteId === clienteId);
      this.titulos.set(filtrados);
      this.messageService.add({severity:'info', summary:'Filtro', detail:`Exibindo títulos de ${this.preSelectedClientName}`});
  }

  limparFiltro() {
      this.preSelectedClientId = null;
      this.preSelectedClientName = null;
      this.titulos.set(this.allTitulos);
      this.router.navigate([], { queryParams: {} });
  }

  darBaixa(titulo: any) {
    this.confirmationService.confirm({
        message: `Confirmar recebimento de ${titulo.valor}?`,
        header: 'Baixa de Título',
        icon: 'pi pi-dollar',
        accept: () => {
            this.tituloService.darBaixa(titulo.id).subscribe({
                next: () => {
                    this.messageService.add({ severity: 'success', summary: 'Pago', detail: 'Título baixado com sucesso.' });
                    this.loadTitulos();
                }
            });
        }
    });
  }
  
  getStatusSeverity(status: string): "success" | "info" | "warn" | "danger" | "secondary" | "contrast" | undefined {
      return status === 'PAGO' ? 'success' : (status === 'PENDENTE' ? 'warn' : 'danger');
  }
}