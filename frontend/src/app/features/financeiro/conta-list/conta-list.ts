import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
    // Nota: Em um sistema real, listariamos todas as contas. 
    // Por simplicidade, vou focar em "Abrir Conta" a partir de um Agendamento ID.
    // Para o MVP, vamos simular uma busca de conta por ID ou Agendamento.
    
    // Mas para listar, precisamos de um endpoint findAll no backend que não criamos específico para contas.
    // Vamos assumir que o usuario entra aqui, digita o ID do Agendamento e "Abre" a conta.
    
    private contaService = inject(ContaService);
    private itemService = inject(ItemServicoService);
    private messageService = inject(MessageService);

    agendamentoIdSearch: number | null = null;
    contaAtual = signal<any>(null);
    itensCatalogo = signal<any[]>([]);
    
    // Modal Adicionar Item
    dialogItemVisible = signal(false);
    selectedItemId: number | null = null;
    quantidadeItem: number = 1;

    ngOnInit() {
        this.loadCatalogo();
    }

    loadCatalogo() {
        this.itemService.findAll().subscribe(data => this.itensCatalogo.set(data));
    }

    buscarConta() {
        if(!this.agendamentoIdSearch) return;
        
        this.contaService.abrirConta(this.agendamentoIdSearch).subscribe({
            next: (data) => {
                this.contaAtual.set(data);
                this.messageService.add({severity:'success', summary:'Encontrada', detail:`Conta #${data.id} carregada`});
            },
            error: () => this.messageService.add({severity:'error', summary:'Erro', detail:'Agendamento não encontrado ou erro.'})
        });
    }

    adicionarItem() {
        if(!this.contaAtual() || !this.selectedItemId) return;
        
        this.contaService.adicionarItem(this.contaAtual().id, this.selectedItemId, this.quantidadeItem).subscribe({
            next: (contaAtualizada) => {
                this.contaAtual.set(contaAtualizada);
                this.dialogItemVisible.set(false);
                this.messageService.add({severity:'success', summary:'Adicionado', detail:'Item incluído na conta.'});
            }
        });
    }

    removerItem(itemId: number) {
        this.contaService.removerItem(this.contaAtual().id, itemId).subscribe({
             next: (contaAtualizada) => {
                this.contaAtual.set(contaAtualizada);
                this.messageService.add({severity:'info', summary:'Removido', detail:'Item removido.'});
             }
        });
    }

    fecharConta() {
        this.contaService.fecharConta(this.contaAtual().id).subscribe({
            next: (contaFechada) => {
                this.contaAtual.set(contaFechada);
                this.messageService.add({severity:'success', summary:'Conta Fechada', detail:'Títulos gerados no financeiro.'});
            }
        });
    }
}