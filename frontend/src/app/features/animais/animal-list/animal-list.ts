import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MessageService, ConfirmationService } from 'primeng/api';

import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { FileUploadModule } from 'primeng/fileupload';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { AvatarModule } from 'primeng/avatar';
import { TagModule } from 'primeng/tag';
import { InputMaskModule } from 'primeng/inputmask';

import { SelectModule } from 'primeng/select';
import { DatePickerModule } from 'primeng/datepicker';
import { AnimalService } from '../../../core/services/animal';

@Component({
  selector: 'app-animal-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    FileUploadModule,
    ToastModule,
    ConfirmDialogModule,
    AvatarModule,
    TagModule,
    InputMaskModule,
    // --- Novos Imports ---
    SelectModule,
    DatePickerModule
  ],
  providers: [ConfirmationService],
  templateUrl: './animal-list.html',
  styleUrls: ['./animal-list.scss']
})
export class AnimalListComponent implements OnInit {
  // ... (O restante da classe permanece IDÊNTICO, a lógica não muda)
  private animalService = inject(AnimalService);
  private messageService = inject(MessageService);
  private confirmationService = inject(ConfirmationService);
  private fb = inject(FormBuilder);

  animais = signal<any[]>([]);
  totalRecords = signal(0);
  loading = signal(true);
  dialogVisible = signal(false);
  saving = signal(false);
  
  animalForm: FormGroup;
  selectedFile: File | null = null;
  
  sexoOptions = [
    { label: 'Macho', value: 'MACHO' },
    { label: 'Fêmea', value: 'FEMEA' }
  ];

  constructor() {
    this.animalForm = this.fb.group({
      id: [null],
      nome: ['', Validators.required],
      especie: ['', Validators.required],
      raca: [''],
      sexo: ['', Validators.required],
      corPelagem: [''],
      dataNascimento: [null]
    });
  }

  ngOnInit() {
    this.loadAnimais();
  }

  loadAnimais(event?: any) {
    this.loading.set(true);
    // Se o evento for undefined (primeira carga), usa padrão 0 e 10
    const page = event ? event.first / event.rows : 0;
    const size = event ? event.rows : 10;

    this.animalService.findAll(page, size).subscribe({
      next: (data) => {
        this.animais.set(data.content);
        this.totalRecords.set(data.totalElements);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Não foi possível carregar os animais.' });
      }
    });
  }

  openNew() {
    this.animalForm.reset();
    this.selectedFile = null;
    this.dialogVisible.set(true);
  }

  editAnimal(animal: any) {
    let dataNasc = animal.dataNascimento;
    if (dataNasc && typeof dataNasc === 'string') {
        dataNasc = new Date(dataNasc + 'T00:00:00');
    }

    this.animalForm.patchValue({
      ...animal,
      dataNascimento: dataNasc
    });
    this.selectedFile = null;
    this.dialogVisible.set(true);
  }

  deleteAnimal(animal: any) {
    this.confirmationService.confirm({
      message: `Tem certeza que deseja excluir ${animal.nome}?`,
      header: 'Confirmar Exclusão',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.animalService.delete(animal.id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Animal excluído' });
            this.loadAnimais();
          },
          error: () => this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Falha ao excluir animal' })
        });
      }
    });
  }

  onFileSelect(event: any) {
    // Ajuste para pegar o arquivo corretamente dependendo do modo do componente
    const file = event.files ? event.files[0] : (event.currentFiles ? event.currentFiles[0] : null);
    this.selectedFile = file;
  }

  saveAnimal() {
    if (this.animalForm.invalid) return;

    this.saving.set(true);
    const animalData = this.animalForm.value;
    
    if (animalData.dataNascimento instanceof Date) {
        const date = animalData.dataNascimento;
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        animalData.dataNascimento = `${year}-${month}-${day}`;
    }

    const request$ = animalData.id 
      ? this.animalService.update(animalData.id, animalData, this.selectedFile || undefined)
      : this.animalService.create(animalData, this.selectedFile || undefined);

    request$.subscribe({
      next: () => {
        this.saving.set(false);
        this.dialogVisible.set(false);
        this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Animal salvo com sucesso' });
        this.loadAnimais();
      },
      error: (err) => {
        this.saving.set(false);
        console.error(err);
        this.messageService.add({ severity: 'error', summary: 'Erro', detail: 'Falha ao salvar animal' });
      }
    });
  }
}