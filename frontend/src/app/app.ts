import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ToastModule } from 'primeng/toast';
import { NavbarComponent } from './shared/components/navbar/navbar';
import { Select } from '@ngxs/store';
import { AuthState } from './store/auth/auth.state';
import { toSignal } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from './shared/components/sidebar/sidebar'; 
import { ClassProvider } from '@angular/core';
import { Store } from '@ngxs/store';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, ToastModule, NavbarComponent, SidebarComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class AppComponent {
  private store = inject(Store);
  isAuthenticated = toSignal(this.store.select(AuthState.isAuthenticated), { initialValue: false });
  
  isSidebarOpen = signal(true); 

  toggleSidebar() {
    this.isSidebarOpen.update(value => !value);
  }
}