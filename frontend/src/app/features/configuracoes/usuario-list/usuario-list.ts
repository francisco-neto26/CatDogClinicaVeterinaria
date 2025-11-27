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
  private messageService = inject(MessageService);
  private fb = inject(FormBuilder);

  usuarios = signal<any[]>([]);
  loading = signal(true);
  dialogVisible = signal(false);
  saving = signal(false);
  isEditMode = signal(false);

  roleOptions = signal<any[]>([]);

  private readonly ALL_ROLES = [
    { label: 'Funcionário (Admin)', value: 'FUNCIONARIO' },
    { label: 'Médico Veterinário', value: 'MEDICO_VETERINARIO' },
    { label: 'Cliente', value: 'CLIENTE' }
  ];
  
  form: FormGroup;

  constructor() {
    this.form = this.fb.group({
      id: [null],
      nome: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      senha: [''], 
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
    this.isEditMode.set(false);

    this.roleOptions.set(this.ALL_ROLES.filter(r => r.value !== 'CLIENTE'));
    
    this.form.get('senha')?.setValidators([Validators.required, Validators.minLength(6)]);
    this.form.get('email')?.enable();
    this.form.get('role')?.enable(); 
    
    this.dialogVisible.set(true);
  }

  openEdit(user: any) {
    this.form.reset();
    this.isEditMode.set(true);
    
    this.roleOptions.set(this.ALL_ROLES);

    this.form.get('role')?.enable();
    this.form.get('email')?.enable();

    let roleValue = user.role.nome;
    if (roleValue === 'MEDICO VETERINARIO') roleValue = 'MEDICO_VETERINARIO';
    if (roleValue === 'MÉDICO VETERINÁRIO') roleValue = 'MEDICO_VETERINARIO';

    this.form.patchValue({
        id: user.id,
        nome: user.pessoa.nome,
        email: user.email,
        telefone: user.pessoa.telefone,
        cpfcnpj: user.pessoa.cpfcnpj,
        cep: user.pessoa.cep,
        logradouro: user.pessoa.logradouro,
        numero: user.pessoa.numero,
        bairro: user.pessoa.bairro,
        cidade: user.pessoa.cidade,
        uf: user.pessoa.uf,
        role: roleValue 
    });

    this.form.get('senha')?.clearValidators();
    this.form.get('senha')?.updateValueAndValidity();

    this.form.get('email')?.disable(); 
    this.form.get('role')?.disable(); 

    this.dialogVisible.set(true);
  }

  save() {
    if (this.form.invalid) return;

    this.saving.set(true);

    const data = this.form.getRawValue();
    
    if (data.cpfcnpj) data.cpfcnpj = data.cpfcnpj.replace(/\D/g, '');
    if (data.cep) data.cep = data.cep.replace(/\D/g, '');
    if (data.telefone) data.telefone = data.telefone.replace(/\D/g, '');

    if (this.isEditMode()) {

        const updateData = {
            ...data,
            newPassword: data.senha 
        };

        this.userService.update(data.id, updateData).subscribe({
            next: () => {
                this.saving.set(false);
                this.dialogVisible.set(false);
                this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Usuário atualizado' });
                this.loadUsuarios();
            },
            error: () => {
                this.saving.set(false);
                this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Falha ao atualizar.' });
            }
        });
    } else {
        this.userService.create(data).subscribe({
            next: () => {
                this.saving.set(false);
                this.dialogVisible.set(false);
                this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Usuário criado' });
                this.loadUsuarios();
            },
            error: (err) => {
                this.saving.set(false);
                this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Falha ao criar usuário.' });
                console.error(err);
            }
        });
    }
  }
}