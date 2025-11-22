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
import { DialogModule } from 'primeng/dialog';     
import { PasswordModule } from 'primeng/password'; 
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
    InputMaskModule,
    DialogModule, 
    PasswordModule 
  ],
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss']
})
export class ProfileComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private messageService = inject(MessageService);

  profileForm: FormGroup;
  
  passwordForm: FormGroup;
  passwordDialogVisible = false;
  passwordLoading = false;

  loading = false;
  currentPhotoUrl: string | null = null;

  constructor() {
    this.profileForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      email: [{value: '', disabled: true}], 
      telefone: [''],
      cpfcnpj: [{value: '', disabled: true}],
      logradouro: [''],
      numero: [''],
      bairro: [''],
      cidade: [''],
      uf: ['', [Validators.maxLength(2)]],
      cep: ['']
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
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

  openPasswordDialog() {
    this.passwordForm.reset();
    this.passwordDialogVisible = true;
  }

  onChangePassword() {
    if (this.passwordForm.invalid) return;

    const { currentPassword, newPassword, confirmPassword } = this.passwordForm.value;
    
    const currentName = this.profileForm.get('nome')?.value;

    if (newPassword !== confirmPassword) {
      this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'As novas senhas não conferem.' });
      return;
    }

    const payload = {
        nome: currentName,
        currentPassword,
        newPassword
    };

    this.passwordLoading = true;
    this.userService.changePassword(payload).subscribe({
      next: () => {
        this.passwordLoading = false;
        this.passwordDialogVisible = false;
        this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Senha alterada com sucesso!' });
      },
      error: (err) => {
        this.passwordLoading = false;
        const msg = err.error?.message || 'Erro ao alterar senha.';
        this.messageService.add({ severity: 'error', summary: 'Erro', detail: msg });
      }
    });
  }
}