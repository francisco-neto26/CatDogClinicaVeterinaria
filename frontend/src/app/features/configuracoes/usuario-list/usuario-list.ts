import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { SelectModule } from 'primeng/select';
import { InputMaskModule } from 'primeng/inputmask';
import { ToastModule } from 'primeng/toast';
import { AvatarModule } from 'primeng/avatar';
import { UserService } from '../../../core/services/user';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-usuario-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    PasswordModule,
    SelectModule,
    InputMaskModule,
    ToastModule,
    AvatarModule
  ],
  templateUrl: './usuario-list.html',
  styleUrls: ['./usuario-list.scss']
})
export class UsuarioListComponent implements OnInit {
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private messageService = inject(MessageService);
  private fb = inject(FormBuilder);

  usuarios = signal<any[]>([]);
  loading = signal(true);
  dialogVisible = signal(false);
  saving = signal(false);

  form: FormGroup;

  roleOptions = [
    { label: 'Funcionário (Admin)', value: 'FUNCIONARIO' },
    { label: 'Médico Veterinário', value: 'MEDICO_VETERINARIO' }
  ];

  constructor() {
    this.form = this.fb.group({
      nome: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(6)]],
      role: [null, Validators.required],
      telefone: [''],
      cpfcnpj: [''],
      cep: [''],
      logradouro: [''],
      numero: [''],
      bairro: [''],
      cidade: [''],
      uf: ['']
    });
  }

  ngOnInit() {
    this.loadUsuarios();
  }

  loadUsuarios() {
    this.loading.set(true);
    this.userService.findAll().subscribe({
      next: (data) => {
        this.usuarios.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Erro ao carregar usuários.' });
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

    if (data.cpfcnpj) data.cpfcnpj = data.cpfcnpj.replace(/\D/g, '');
    if (data.cep) data.cep = data.cep.replace(/\D/g, '');
    if (data.telefone) data.telefone = data.telefone.replace(/\D/g, '');

    this.authService.registerEmployee(data).subscribe({
      next: () => {
        this.saving.set(false);
        this.dialogVisible.set(false);
        this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Usuário criado' });
        this.loadUsuarios();
      },
      error: (err) => {
        this.saving.set(false);
        this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Falha ao criar usuário.' });
      }
    });
  }
}