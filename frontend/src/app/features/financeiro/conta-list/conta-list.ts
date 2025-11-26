import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { MessageService, ConfirmationService } from 'primeng/api';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputNumberModule } from 'primeng/inputnumber';
import { SelectModule } from 'primeng/select';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TagModule } from 'primeng/tag';
import { ContaService } from '../../../core/services/conta';
import { ItemServicoService } from '../../../core/services/item-servico';
import { AgendamentoService } from '../../../core/services/agendamento';

@Component({
  selector: 'app-conta-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    ButtonModule,
    DialogModule,
    InputNumberModule,
    SelectModule,
    ToastModule,
    ConfirmDialogModule,
    TagModule
  ],
  providers: [ConfirmationService],
  templateUrl: './conta-list.html',
  styleUrls: ['./conta-list.scss']
})
export class ContaListComponent implements OnInit {
    private contaService = inject(ContaService);
    private itemService = inject(ItemServicoService);
    private agendamentoService = inject(AgendamentoService);
    private messageService = inject(MessageService);
    private confirmationService = inject(ConfirmationService);
    private route = inject(ActivatedRoute);

    agendamentoIdSearch: number | null = null;
    
    listaContas = signal<any[]>([]);
    contaAtual = signal<any>(null);
    
    itensCatalogo = signal<any[]>([]);
    listaAgendamentos = signal<any[]>([]);

    dialogItemVisible = signal(false);
    dialogNewContaVisible = signal(false);
    
    selectedItemId: number | null = null;
    newContaAgendamentoId: number | null = null;
    quantidadeItem: number = 1;
    
    preSelectedClientName: string | null = null;
    preSelectedClientId: number | null = null;
    loading = signal(true);

    ngOnInit() {
        this.loadCatalogo();
        
        this.route.queryParams.subscribe(params => {
            if (params['clienteId']) {
                this.preSelectedClientId = Number(params['clienteId']);
                this.preSelectedClientName = params['clienteNome'];
                this.carregarContas(this.preSelectedClientId);
            } else {
                this.preSelectedClientId = null;
                this.preSelectedClientName = null;
                this.carregarContas(); 
            }
        });
    }

    loadCatalogo() {
        this.itemService.findAll().subscribe(data => this.itensCatalogo.set(data));
    }
    
    carregarContas(clienteId?: number) {
        this.loading.set(true);
        this.contaService.findAll(clienteId).subscribe({
            next: (data) => {
                this.listaContas.set(data);
                this.contaAtual.set(null);
                this.loading.set(false);
            },
            error: () => {
                this.loading.set(false);
                this.messageService.add({severity:'error', summary:'Erro', detail:'Erro ao buscar contas.'});
            }
        });
    }

    buscarContaPorAgendamento() {
        if (!this.agendamentoIdSearch) {
            this.carregarContas(this.preSelectedClientId || undefined);
            return;
        }
        
        this.loading.set(true);
        this.contaService.abrirConta(this.agendamentoIdSearch).subscribe({
            next: (data) => {
                this.listaContas.set([data]); 
                this.contaAtual.set(null); 
                
                this.messageService.add({severity:'success', summary:'Encontrada', detail:`Conta #${data.id} listada`});
                this.loading.set(false);
            },
            error: (err) => {
                this.loading.set(false);
                if(err.status === 404) {
                    this.messageService.add({severity:'error', summary:'Não Encontrado', detail:'Agendamento não existe.'});
                } else {
                    this.messageService.add({severity:'error', summary:'Erro', detail:'Falha ao abrir conta.'});
                }
            }
        });
    }

    openNewContaDialog() {
        this.agendamentoService.listAll(0, 50).subscribe(data => {            
            const agendamentosValidos = data.content.filter((a: any) => 
                a.status === 'AGENDADO' 
            );            
            this.listaAgendamentos.set(agendamentosValidos);
            this.dialogNewContaVisible.set(true);
        });
    }

    criarContaManual() {
        if (!this.newContaAgendamentoId) return;

        this.loading.set(true);
        this.contaService.abrirConta(this.newContaAgendamentoId).subscribe({
            next: (data) => {
                this.listaContas.set([data]); 
                this.contaAtual.set(null); 

                this.dialogNewContaVisible.set(false);
                this.messageService.add({severity:'success', summary:'Sucesso', detail:`Conta #${data.id} iniciada`});
                this.loading.set(false);
            },
            error: (err) => {
                this.loading.set(false);
                this.messageService.add({severity:'error', summary:'Erro', detail:'Verifique se o ID do agendamento existe.'});
            }
        });
    }

    selecionarConta(conta: any) {
        this.contaService.findById(conta.id).subscribe({
            next: (data) => this.contaAtual.set(data),
            error: () => this.messageService.add({severity:'error', summary:'Erro', detail:'Erro ao abrir conta.'})
        });
    }
    
    voltarParaLista() {
        this.contaAtual.set(null);
        const clienteId = this.route.snapshot.queryParams['clienteId'];
        
        if(this.agendamentoIdSearch) {
            this.buscarContaPorAgendamento();
        } else {
            this.carregarContas(clienteId ? Number(clienteId) : undefined);
        }
    }

    adicionarItem() {
        if(!this.contaAtual() || !this.selectedItemId) return;
        
        this.contaService.adicionarItem(this.contaAtual().id, this.selectedItemId, this.quantidadeItem).subscribe({
            next: (contaAtualizada) => {
                this.contaAtual.set(contaAtualizada);
                this.dialogItemVisible.set(false);
                this.messageService.add({severity:'success', summary:'Adicionado', detail:'Item incluído na conta.'});
            },
            error: () => this.messageService.add({severity:'error', summary:'Erro', detail:'Falha ao adicionar item.'})
        });
    }

    removerItem(itemId: number) {
        this.contaService.removerItem(this.contaAtual().id, itemId).subscribe({
             next: (contaAtualizada) => {
                this.contaAtual.set(contaAtualizada);
                this.messageService.add({severity:'info', summary:'Removido', detail:'Item removido.'});
             },
             error: () => this.messageService.add({severity:'error', summary:'Erro', detail:'Falha ao remover item.'})
        });
    }

    fecharConta() {
        this.confirmationService.confirm({
            message: 'Tem certeza que deseja fechar esta conta? Isso irá gerar os títulos financeiros.',
            header: 'Fechar Conta',
            icon: 'pi pi-exclamation-triangle',
            accept: () => {
                this.contaService.fecharConta(this.contaAtual().id).subscribe({
                    next: (contaFechada) => {
                        this.contaAtual.set(contaFechada);
                        this.messageService.add({severity:'success', summary:'Conta Fechada', detail:'Títulos gerados no financeiro.'});
                    },
                    error: () => this.messageService.add({severity:'error', summary:'Erro', detail:'Falha ao fechar conta.'})
                });
            }
        });
    }
}