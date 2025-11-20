import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { MessageService } from 'primeng/api';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { AvatarModule } from 'primeng/avatar';
import { FileUploadModule } from 'primeng/fileupload';
import { InputMaskModule } from 'primeng/inputmask';
import { UserService } from '../../core/services/user';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CardModule,
    InputTextModule,
    ButtonModule,
    AvatarModule,
    FileUploadModule,
    InputMaskModule
  ],
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss']
})
export class ProfileComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private messageService = inject(MessageService);

  profileForm: FormGroup;
  loading = false;
  currentPhotoUrl: string | null = null;

  constructor() {
    this.profileForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      email: [{value: '', disabled: true}], // Email não editável
      telefone: [''],
      cpfcnpj: [{value: '', disabled: true}], // CPF não editável nesta rota
      logradouro: [''],
      numero: [''],
      bairro: [''],
      cidade: [''],
      uf: ['', [Validators.maxLength(2)]],
      cep: ['']
    });
  }

  ngOnInit() {
    this.loadUserData();
  }

  loadUserData() {
    this.userService.getMe().subscribe({
      next: (user) => {
        this.currentPhotoUrl = user.fotoUrl;
        this.userService.currentUserPhoto.set(user.fotoUrl);
        
        // Preenche o formulário com dados da Pessoa
        this.profileForm.patchValue({
          email: user.email,
          nome: user.pessoa.nome,
          telefone: user.pessoa.telefone,
          cpfcnpj: user.pessoa.cpfcnpj,
          logradouro: user.pessoa.logradouro,
          numero: user.pessoa.numero,
          bairro: user.pessoa.bairro,
          cidade: user.pessoa.cidade,
          uf: user.pessoa.uf,
          cep: user.pessoa.cep
        });
      },
      error: (err) => console.error(err)
    });
  }

  onUpload(event: any) {
    const file = event.files[0];
    this.userService.updatePhoto(file).subscribe({
      next: (res) => {
        this.currentPhotoUrl = res.fotoUrl;
        this.messageService.add({severity:'success', summary:'Sucesso', detail:'Foto atualizada!'});
      },
      error: () => this.messageService.add({severity:'error', summary:'Erro', detail:'Falha ao enviar foto.'})
    });
  }

  // Wrapper para usar input file simples se preferir, ou o componente do PrimeNG acima
  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
        this.userService.updatePhoto(file).subscribe({
            next: (res) => {
                this.currentPhotoUrl = res.fotoUrl;
                this.messageService.add({severity:'success', summary:'Sucesso', detail:'Foto atualizada!'});
            }
        });
    }
  }

  onSubmit() {
    if (this.profileForm.invalid) return;
    this.loading = true;
    
    const rawValue = this.profileForm.getRawValue();
    if (rawValue.telefone) rawValue.telefone = rawValue.telefone.replace(/\D/g, '');
    if (rawValue.cep) rawValue.cep = rawValue.cep.replace(/\D/g, '');

    this.userService.updateProfile(rawValue).subscribe({
      next: () => {
        this.loading = false;
        this.messageService.add({severity:'success', summary:'Sucesso', detail:'Perfil atualizado!'});
      },
      error: (err) => {
        this.loading = false;
        this.messageService.add({severity:'error', summary:'Erro', detail:'Falha ao atualizar perfil.'});
      }
    });
  }
}