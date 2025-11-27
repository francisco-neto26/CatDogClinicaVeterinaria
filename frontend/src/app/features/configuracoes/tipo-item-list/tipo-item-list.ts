import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { MessageService, ConfirmationService } from 'primeng/api';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TipoItemService } from '../../../core/services/tipo-item';

@Component({
  selector: 'app-tipo-item-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    ToastModule,
    ConfirmDialogModule
  ],
  providers: [ConfirmationService],
  templateUrl: './tipo-item-list.html',
  styleUrls: ['./tipo-item-list.scss']
})
export class TipoItemListComponent implements OnInit {
  private tipoItemService = inject(TipoItemService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  itens = signal<any[]>([]);
  loading = signal(true);
  dialogVisible = signal(false);
  saving = signal(false);
  
  form: FormGroup;

  constructor() {
    this.form = this.fb.group({
      id: [null],
      nome: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.loadItens();
  }

  loadItens() {
    this.loading.set(true);
    this.tipoItemService.findAll().subscribe({
      next: (data) => {
        this.itens.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Erro ao carregar dados.' });
      }
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
      message: `Tem certeza que deseja excluir "${item.nome}"?`,
      header: 'Confirmar Exclusão',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.tipoItemService.delete(item.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Registro excluído' });
            this.loadItens();
          },
          error: (err) => {
            let msg = 'Erro ao excluir registro.';
            
            if (err.status === 409) {
                msg = 'Não é possível excluir: Este tipo está vinculado a itens do catálogo.';
            }
            
            this.messageService.add({ severity: 'error', summary: 'Erro', detail: msg });
          }
        });
      }
    });
  }

  save() {
    if (this.form.invalid) return;

    this.saving.set(true);
    const data = this.form.value;
    
    const request$ = data.id 
      ? this.tipoItemService.update(data.id, data)
      : this.tipoItemService.create(data);

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
}