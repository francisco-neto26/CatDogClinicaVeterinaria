import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { MessageModule } from 'primeng/message';
import { InputMaskModule } from 'primeng/inputmask';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    CardModule,
    InputTextModule,
    PasswordModule,
    ButtonModule,
    MessageModule,
    InputMaskModule
  ],
  templateUrl: './register.html',
  styleUrls: ['./register.scss']
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  loading = false;
  errorMessage: string | null = null;

  registerForm = this.fb.group({
    nome: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(6)]],
    telefone: [''],
    cpfcnpj: [''],
    cep: [''],
    logradouro: [''],
    numero: [''],
    bairro: [''],
    cidade: [''],
    uf: ['', [Validators.maxLength(2)]]
  });

  onSubmit() {
    if (this.registerForm.invalid) return;

    this.loading = true;
    this.errorMessage = null;

    const rawValue = this.registerForm.getRawValue();
    if (rawValue.cpfcnpj) rawValue.cpfcnpj = rawValue.cpfcnpj.replace(/\D/g, '');
    if (rawValue.cep) rawValue.cep = rawValue.cep.replace(/\D/g, '');
    if (rawValue.telefone) rawValue.telefone = rawValue.telefone.replace(/\D/g, '');

    this.authService.register(rawValue).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Erro ao realizar cadastro.';
        console.error(err);
      }
    });
  }
}