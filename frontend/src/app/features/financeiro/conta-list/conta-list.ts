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
    private messageService = inject(MessageService);
    // CORREÇÃO AQUI: Injeção do ConfirmationService que faltava
    private confirmationService = inject(ConfirmationService);
    private route = inject(ActivatedRoute);

    agendamentoIdSearch: number | null = null;

    listaContas = signal<any[]>([]);

    contaAtual = signal<any>(null);

    itensCatalogo = signal<any[]>([]);
    dialogItemVisible = signal(false);
    selectedItemId: number | null = null;
    quantidadeItem: number = 1;

    preSelectedClientName: string | null = null;
    loading = signal(true);

    ngOnInit() {
        this.loadCatalogo();

        this.route.queryParams.subscribe(params => {
            if (params['clienteId']) {
                const clienteId = Number(params['clienteId']);
                this.preSelectedClientName = params['clienteNome'];
                this.carregarContas(clienteId);
            } else {
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
                this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Erro ao buscar contas.' });
            }
        });
    }

    buscarContaPorAgendamento() {
        if (!this.agendamentoIdSearch) return;

        this.contaService.abrirConta(this.agendamentoIdSearch).subscribe({
            next: (data) => {
                this.contaAtual.set(data);
                // Opcional: limpar lista ou manter
                // this.listaContas.set([]); 
                this.messageService.add({ severity: 'success', summary: 'Encontrada', detail: `Conta #${data.id} carregada` });
            },
            error: () => this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Agendamento não encontrado ou erro.' })
        });
    }

    selecionarConta(conta: any) {
        // Busca os detalhes completos (incluindo itens) pelo ID
        this.contaService.findById(conta.id).subscribe({
            next: (data) => this.contaAtual.set(data),
            error: () => this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Erro ao abrir conta.' })
        });
    }

    voltarParaLista() {
        this.contaAtual.set(null);
        // Recarrega a lista para garantir dados frescos
        const clienteId = this.route.snapshot.queryParams['clienteId'];
        this.carregarContas(clienteId ? Number(clienteId) : undefined);
    }

    adicionarItem() {
        if (!this.contaAtual() || !this.selectedItemId) return;

        this.contaService.adicionarItem(this.contaAtual().id, this.selectedItemId, this.quantidadeItem).subscribe({
            next: (contaAtualizada) => {
                this.contaAtual.set(contaAtualizada);
                this.dialogItemVisible.set(false);
                this.messageService.add({ severity: 'success', summary: 'Adicionado', detail: 'Item incluído na conta.' });
            },
            error: () => this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Falha ao adicionar item.' })
        });
    }

    removerItem(itemId: number) {
        this.contaService.removerItem(this.contaAtual().id, itemId).subscribe({
            next: (contaAtualizada) => {
                this.contaAtual.set(contaAtualizada);
                this.messageService.add({ severity: 'info', summary: 'Removido', detail: 'Item removido.' });
            },
            error: () => this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Falha ao remover item.' })
        });
    }

    fecharConta() {
        console.log("eu aqui 1")
        this.confirmationService.confirm({
            message: 'Tem certeza que deseja fechar esta conta? Isso irá gerar os títulos financeiros.',
            header: 'Fechar Conta',
            icon: 'pi pi-exclamation-triangle',

            accept: () => {
                this.contaService.fecharConta(this.contaAtual().id).subscribe({

                    next: (contaFechada) => {
                        this.contaAtual.set(contaFechada);
                        console.log("eu aqui")
                        this.messageService.add({ severity: 'success', summary: 'Conta Fechada', detail: 'Títulos gerados no financeiro.' });
                    },

                    error: () => this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Falha ao fechar conta.' })
                });
            }
        });
    }
}