import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { MessageService, ConfirmationService } from 'primeng/api';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DialogModule } from 'primeng/dialog';
import { SelectModule } from 'primeng/select'; // Importante
import { TituloService } from '../../../core/services/titulo';
import { ContaService } from '../../../core/services/conta';

@Component({
  selector: 'app-titulo-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    ButtonModule,
    TagModule,
    ToastModule,
    ConfirmDialogModule,
    DialogModule,
    SelectModule
  ],
  providers: [ConfirmationService],
  templateUrl: './titulo-list.html',
  styleUrls: ['./titulo-list.scss']
})
export class TituloListComponent implements OnInit {
  private tituloService = inject(TituloService);
  private contaService = inject(ContaService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  titulos = signal<any[]>([]);
  loading = signal(true);
  
  preSelectedClientId: number | undefined = undefined;
  preSelectedClientName: string | undefined = undefined;
  
  dialogVisible = signal(false);
  
  contasAbertas = signal<any[]>([]);
  contaSelecionada = signal<any>(null);

  ngOnInit() {
    this.loadTitulos();
  }

  loadTitulos() {
    this.loading.set(true);
    this.tituloService.findAll().subscribe({
        next: (data) => {
            this.titulos.set(data);
            
            this.route.queryParams.subscribe(params => {
                if (params['clienteId']) {
                    this.preSelectedClientId = Number(params['clienteId']);
                    this.preSelectedClientName = params['clienteNome'];
                    this.filtrarPorCliente(this.preSelectedClientId);
                } else {
                    this.loading.set(false);
                }
            });
        },
        error: () => this.loading.set(false)
    });
  }

  filtrarPorCliente(clienteId: number) {
      const filtrados = this.titulos().filter(t => t.clienteId === clienteId);
      this.titulos.set(filtrados);
      this.loading.set(false);
  }

  limparFiltro() {
      this.preSelectedClientId = undefined;
      this.preSelectedClientName = undefined;
      this.loadTitulos(); 
      this.router.navigate([], { queryParams: {} });
  }

  openNew() {
      this.contaSelecionada.set(null);

      this.contaService.findAll().subscribe({
          next: (allContas) => {
              // Filtra apenas contas ABERTAS e com valor > 0
              const abertas = allContas.filter(c => c.status === 'ABERTA' && c.valorTotal > 0);
              this.contasAbertas.set(abertas);
              this.dialogVisible.set(true);
          },
          error: () => {
              this.messageService.add({severity:'error', summary:'Erro', detail:'Falha ao carregar contas abertas.'});
          }
      });
  }

  gerarTitulo() {
      const conta = this.contaSelecionada();
      if (!conta) return;

      this.loading.set(true);
      this.contaService.fecharConta(conta.id).subscribe({
          next: () => {
              this.dialogVisible.set(false);
              this.messageService.add({severity:'success', summary:'Sucesso', detail:'Conta fechada e título gerado!'});
              this.loadTitulos();
          },
          error: () => {
              this.loading.set(false);
              this.messageService.add({severity:'error', summary:'Erro', detail:'Falha ao gerar título.'});
          }
      });
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