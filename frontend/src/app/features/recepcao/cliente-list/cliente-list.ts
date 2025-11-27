import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';

import { TableModule } from 'primeng/table';
import { AvatarModule } from 'primeng/avatar';
import { TagModule } from 'primeng/tag';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';
import { InputTextModule } from 'primeng/inputtext';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { DialogModule } from 'primeng/dialog';
import { PasswordModule } from 'primeng/password';
import { InputMaskModule } from 'primeng/inputmask';
import { UserService } from '../../../core/services/user';

@Component({
  selector: 'app-cliente-list',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule,
    ReactiveFormsModule,
    TableModule, 
    AvatarModule, 
    TagModule, 
    ButtonModule,
    TooltipModule,
    InputTextModule,
    IconFieldModule,
    InputIconModule,
    DialogModule,
    PasswordModule,
    InputMaskModule
  ],
  templateUrl: './cliente-list.html',
  styleUrls: ['./cliente-list.scss']
})
export class ClienteListComponent implements OnInit {
    private userService = inject(UserService);
    private router = inject(Router);
    private fb = inject(FormBuilder);
    private messageService = inject(MessageService);

    clientes = signal<any[]>([]);
    private allClientes: any[] = [];
    
    loading = signal(true);
    termoBusca: string = '';
    
    dialogVisible = signal(false);
    saving = signal(false);
    form: FormGroup;

    constructor() {
        this.form = this.fb.group({
            nome: ['', Validators.required],
            email: ['', [Validators.required, Validators.email]],
            senha: ['', [Validators.required, Validators.minLength(6)]],
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
        this.loadClientes();
    }

    loadClientes() {
        this.loading.set(true);
        this.userService.findAll().subscribe(users => {
            this.allClientes = users.filter((u: any) => u.role.nome === 'CLIENTE');
            this.clientes.set(this.allClientes);
            this.loading.set(false);
        });
    }

    filtrar() {
        const termo = this.termoBusca.toLowerCase().trim();
        
        if (!termo) {
            this.clientes.set(this.allClientes);
            return;
        }

        const filtrados = this.allClientes.filter(c => 
            c.pessoa.nome.toLowerCase().includes(termo) || 
            (c.pessoa.cpfcnpj && c.pessoa.cpfcnpj.includes(termo))
        );

        this.clientes.set(filtrados);
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

        const payload = { ...data, role: 'CLIENTE' };

        this.userService.create(payload).subscribe({
            next: () => {
                this.saving.set(false);
                this.dialogVisible.set(false);
                this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Cliente cadastrado' });
                this.loadClientes();
            },
            error: () => {
                this.saving.set(false);
                this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Falha ao cadastrar.' });
            }
        });
    }

    criarAgendamento(cliente: any) {
        this.router.navigate(['/agendamentos'], { 
            queryParams: { clienteId: cliente.id, clienteNome: cliente.pessoa.nome } 
        });
    }

    verContas(cliente: any) {
        this.router.navigate(['/contas'], { 
            queryParams: { clienteId: cliente.id, clienteNome: cliente.pessoa.nome } 
        }); 
    }

    verTitulos(cliente: any) {
        this.router.navigate(['/titulos'], { 
            queryParams: { clienteId: cliente.id, clienteNome: cliente.pessoa.nome } 
        });
    }
}