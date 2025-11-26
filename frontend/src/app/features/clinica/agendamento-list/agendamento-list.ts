import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

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
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  agendamentos = signal<any[]>([]);
  allAgendamentos: any[] = []; 
  
  meusAnimais = signal<any[]>([]); 
  allAnimais = signal<any[]>([]); 
  veterinarios = signal<any[]>([]);
  
  loading = signal(true);
  dialogVisible = signal(false);
  dialogAssignVisible = signal(false);
  saving = signal(false);
  
  isCliente = signal(false);
  isAdmin = signal(false);
  isVet = signal(false);
  
  preSelectedClientId: number | null = null;
  preSelectedClientName: string | null = null;

  form: FormGroup;
  selectedAgendamentoId: number | null = null;
  selectedVetId = signal<number | null>(null);

  constructor() {
    this.form = this.fb.group({
      animalId: [null, Validators.required],
      dataHora: [null, Validators.required],
      descricao: [''],
      funcionarioId: [null]
    });
  }

  ngOnInit() {
    this.checkRole();
    this.loadInitialData();

    this.route.queryParams.subscribe(params => {
        if (params['clienteId']) {
            this.preSelectedClientId = Number(params['clienteId']);
            this.preSelectedClientName = params['clienteNome'];
            this.filterAnimais(this.preSelectedClientId);
        } else if (this.isCliente()) {
            this.filterAnimais(0);
        }
    });
    
    if (this.router.url.includes('novo-agendamento')) {
        this.openNew();
    }

    this.loadAgendamentos();
  }

  getPageTitle(): string {
      if (this.preSelectedClientName) return `Agenda de ${this.preSelectedClientName}`;
      if (this.router.url.includes('meus-agendamentos')) return 'Histórico de Consultas';
      if (this.router.url.includes('novo-agendamento')) return 'Agendar Consulta';
      return 'Gestão de Agenda';
  }

  checkRole() {
    const token = this.store.selectSnapshot(AuthState.token);
    if (token) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            const role = payload.role;
            this.isCliente.set(role === 'ROLE_CLIENTE');
            this.isAdmin.set(role === 'ROLE_FUNCIONARIO' || role === 'ROLE_ADMINISTRADOR');
            this.isVet.set(role === 'ROLE_MEDICO_VETERINARIO');
        } catch (e) { console.error(e); }
    }
  }

  isAdminOrVet() {
      return this.isAdmin() || this.isVet();
  }

  canCreate() {
      return true;
  }

  loadInitialData() {
    this.userService.findVeterinarios().subscribe({
        next: (users) => {
            const apenasVets = users.filter((u: any) => 
                u.role.nome.toUpperCase().includes('VETERINARIO')
            );
            this.veterinarios.set(apenasVets);
        },
        error: (err) => console.error('Erro ao carregar veterinários', err)
    });

    if (this.isAdminOrVet()) {
        this.animalService.findAll(0, 1000).subscribe({
            next: (data) => {
                this.allAnimais.set(data.content);
                this.meusAnimais.set(data.content); 
            },
            error: (err) => console.error('Erro ao carregar animais', err)
        });
    } else {
        this.animalService.findAll(0, 100).subscribe({
            next: (data) => {
                this.meusAnimais.set(data.content);
            },
            error: (err) => console.error('Erro ao carregar meus animais', err)
        });
    }
  }

  filterAnimais(clienteId: number) {
    if (this.isAdminOrVet() && clienteId > 0) {
        const filtrados = this.allAnimais().filter(a => a.usuarioId === clienteId);
        this.meusAnimais.set(filtrados);
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
        this.allAgendamentos = data.content;
        this.applyFilter();
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        console.warn(err); 
        this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Não foi possível carregar a agenda.' });
      }
    });
  }

  applyFilter() {
    if (this.preSelectedClientId) {
        const filtrados = this.allAgendamentos.filter(a => a.clienteId === this.preSelectedClientId);
        this.agendamentos.set(filtrados);
    } else {
        this.agendamentos.set(this.allAgendamentos);
    }
  }

  limparFiltro() {
      this.preSelectedClientId = null;
      this.preSelectedClientName = null;
      this.applyFilter();
      if(this.isAdminOrVet()) {
          this.meusAnimais.set(this.allAnimais());
      }
      this.router.navigate([], {
          queryParams: { 'clienteId': null, 'clienteNome': null },
          queryParamsHandling: 'merge'
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
    
    if (data.dataHora instanceof Date) {
        const date = data.dataHora;
        const offset = date.getTimezoneOffset() * 60000;
        const localISOTime = (new Date(date.getTime() - offset)).toISOString().slice(0, -1);
        data.dataHora = localISOTime;
    }
    
    this.agendamentoService.create(data).subscribe({
        next: () => {
            this.saving.set(false);
            this.dialogVisible.set(false);
            this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Agendamento criado!' });
            this.loadAgendamentos();
            
            if (this.router.url.includes('novo-agendamento')) {
                this.router.navigate(['/meus-agendamentos']);
            }
        },
        error: (err) => {
            this.saving.set(false);
            console.error(err);
            const msg = err.error?.errors?.dataHora || 'Falha ao agendar. Verifique os dados.';
            this.messageService.add({ severity: 'error', summary: 'Erro', detail: msg });
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