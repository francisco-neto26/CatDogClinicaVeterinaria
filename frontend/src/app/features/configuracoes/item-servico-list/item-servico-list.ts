import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MessageService, ConfirmationService } from 'primeng/api';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select'; // CORREÇÃO: Novo componente
import { InputNumberModule } from 'primeng/inputnumber';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TagModule } from 'primeng/tag';
import { ItemServicoService } from '../../../core/services/item-servico';
import { TipoItemService } from '../../../core/services/tipo-item';

@Component({
  selector: 'app-item-servico-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    SelectModule, // Novo
    InputNumberModule,
    ToastModule,
    ConfirmDialogModule,
    TagModule
  ],
  providers: [ConfirmationService],
  templateUrl: './item-servico-list.html',
  styleUrls: ['./item-servico-list.scss']
})
export class ItemServicoListComponent implements OnInit {
  private itemService = inject(ItemServicoService);
  private tipoService = inject(TipoItemService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  itens = signal<any[]>([]);
  tipos = signal<any[]>([]); // Lista para o dropdown
  loading = signal(true);
  dialogVisible = signal(false);
  saving = signal(false);
  
  form: FormGroup;

  constructor() {
    this.form = this.fb.group({
      id: [null],
      descricao: ['', Validators.required],
      precoUnitario: [null, Validators.required],
      tipoItemId: [null, Validators.required]
    });
  }

  ngOnInit() {
    this.loadItens();
    this.loadTipos();
  }

  loadItens() {
    this.loading.set(true);
    this.itemService.findAll().subscribe({
      next: (data) => {
        this.itens.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Erro ao carregar catálogo.' });
      }
    });
  }

  loadTipos() {
    this.tipoService.findAll().subscribe({
        next: (data) => this.tipos.set(data)
    });
  }

  openNew() {
    this.form.reset();
    this.dialogVisible.set(true);
  }

  edit(item: any) {
    this.form.patchValue(item);
    this.dialogVisible.set(true);
  }

  delete(item: any) {
    this.confirmationService.confirm({
      message: `Excluir "${item.descricao}"?`,
      header: 'Confirmar',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.itemService.delete(item.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Excluído' });
            this.loadItens();
          },
          error: () => this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Erro ao excluir' })
        });
      }
    });
  }

  save() {
    if (this.form.invalid) return;

    this.saving.set(true);
    const data = this.form.value;
    
    const request$ = data.id 
      ? this.itemService.update(data.id, data)
      : this.itemService.create(data);

    request$.subscribe({
      next: () => {
        this.saving.set(false);
        this.dialogVisible.set(false);
        this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Salvo com sucesso' });
        this.loadItens();
      },
      error: () => {
        this.saving.set(false);
        this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Erro ao salvar' });
      }
    });
  }

  getTipoNome(id: number): string {
      const tipo = this.tipos().find(t => t.id === id);
      return tipo ? tipo.nome : 'N/A';
  }
}