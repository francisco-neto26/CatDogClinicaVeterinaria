import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms'; // <--- Adicionado FormsModule
import { Store } from '@ngxs/store';
import { AuthState } from '../../../store/auth/auth.state';
import { MessageService, ConfirmationService } from 'primeng/api';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { TextareaModule } from 'primeng/textarea';
import { SelectModule } from 'primeng/select';
import { DatePickerModule } from 'primeng/datepicker';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { AgendamentoService } from '../../../core/services/agendamento';
import { AnimalService } from '../../../core/services/animal';
import { UserService } from '../../../core/services/user';

@Component({
  selector: 'app-agendamento-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    TableModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    TextareaModule,
    SelectModule,
    DatePickerModule,
    TagModule,
    ToastModule,
    ConfirmDialogModule
  ],
  providers: [ConfirmationService],
  templateUrl: './agendamento-list.html',
  styleUrls: ['./agendamento-list.scss']
})
export class AgendamentoListComponent implements OnInit {
  private agendamentoService = inject(AgendamentoService);
  private animalService = inject(AnimalService);
  private userService = inject(UserService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);
  private store = inject(Store);

  agendamentos = signal<any[]>([]);
  meusAnimais = signal<any[]>([]);
  veterinarios = signal<any[]>([]);
  
  loading = signal(true);
  dialogVisible = signal(false);
  dialogAssignVisible = signal(false);
  saving = signal(false);
  
  isCliente = signal(false);
  isAdminOrVet = signal(false);

  form: FormGroup;
  selectedAgendamentoId: number | null = null;
  selectedVetId = signal<number | null>(null);

  constructor() {
    this.form = this.fb.group({
      animalId: [null, Validators.required],
      dataHora: [null, Validators.required],
      descricao: ['']
    });
  }

  ngOnInit() {
    this.checkRole();
    this.loadAgendamentos();
    
    if (this.isCliente()) {
        this.loadMeusAnimais();
    }
    if (this.isAdminOrVet()) {
        this.loadVeterinarios();
    }
  }

  checkRole() {
    const token = this.store.selectSnapshot(AuthState.token);
    if (token) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            const role = payload.role;
            this.isCliente.set(role === 'ROLE_CLIENTE');
            this.isAdminOrVet.set(role === 'ROLE_FUNCIONARIO' || role === 'ROLE_MEDICO_VETERINARIO');
        } catch (e) {
            console.error(e);
        }
    }
  }

  loadAgendamentos() {
    this.loading.set(true);
    let request$;

    if (this.isAdminOrVet()) {
        request$ = this.agendamentoService.listAll();
    } else {
        request$ = this.agendamentoService.listMine();
    }

    request$.subscribe({
      next: (data) => {
        this.agendamentos.set(data.content);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Erro ao carregar agenda.' });
      }
    });
  }

  loadMeusAnimais() {
    this.animalService.findAll(0, 100).subscribe({
        next: (data) => this.meusAnimais.set(data.content)
    });
  }

  loadVeterinarios() {
    this.userService.findAll().subscribe({
        next: (users) => {
            const vets = users.filter(u => u.role.nome === 'MEDICO VETERINARIO');
            this.veterinarios.set(vets);
        }
    });
  }

  openNew() {
    this.form.reset();
    this.dialogVisible.set(true);
  }

  save() {
    if (this.form.invalid) return;
    this.saving.set(true);
    const data = this.form.value;
    
    this.agendamentoService.create(data).subscribe({
        next: () => {
            this.saving.set(false);
            this.dialogVisible.set(false);
            this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Agendamento criado!' });
            this.loadAgendamentos();
        },
        error: () => {
            this.saving.set(false);
            this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Falha ao agendar.' });
        }
    });
  }

  cancelar(id: number) {
    this.confirmationService.confirm({
        message: 'Deseja cancelar este agendamento?',
        header: 'Confirmar Cancelamento',
        icon: 'pi pi-exclamation-triangle',
        accept: () => {
            this.agendamentoService.cancel(id).subscribe({
                next: () => {
                    this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Agendamento cancelado.' });
                    this.loadAgendamentos();
                },
                error: () => this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Erro ao cancelar.' })
            });
        }
    });
  }

  openAssign(id: number) {
    this.selectedAgendamentoId = id;
    this.selectedVetId.set(null);
    this.dialogAssignVisible.set(true);
  }

  assignVet() {
    if (!this.selectedAgendamentoId || !this.selectedVetId()) return;
    
    this.agendamentoService.assignVeterinarian(this.selectedAgendamentoId, this.selectedVetId()!).subscribe({
        next: () => {
            this.dialogAssignVisible.set(false);
            this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Veterinário atribuído!' });
            this.loadAgendamentos();
        }
    });
  }

  concluir(id: number) {
    this.confirmationService.confirm({
        message: 'Confirmar conclusão do atendimento?',
        header: 'Concluir Consulta',
        icon: 'pi pi-check-circle',
        accept: () => {
            this.agendamentoService.complete(id).subscribe({
                next: () => {
                    this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Atendimento concluído.' });
                    this.loadAgendamentos();
                }
            });
        }
    });
  }

  getStatusSeverity(status: string): "success" | "info" | "warn" | "danger" | "secondary" | "contrast" | undefined {
      switch(status) {
          case 'AGENDADO': return 'info';
          case 'CONCLUIDO': return 'success';
          case 'CANCELADO': return 'danger';
          default: return 'secondary';
      }
  }
}