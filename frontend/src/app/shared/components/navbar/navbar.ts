import { Component, OnInit, inject, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Store } from '@ngxs/store';
import { AuthState } from '../../../store/auth/auth.state';
import { Logout } from '../../../store/auth/auth.actions';
import { ButtonModule } from 'primeng/button';
import { AvatarModule } from 'primeng/avatar';
import { LayoutState } from '../../../store/layout/layout.state';
import { CloseTab, TabInfo } from '../../../store/layout/layout.actions';
import { toSignal } from '@angular/core/rxjs-interop';
import { TooltipModule } from 'primeng/tooltip';
import { UserService } from '../../../core/services/user';


@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ButtonModule,
    AvatarModule,
    TooltipModule
  ],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.scss']
})
export class NavbarComponent implements OnInit {
  userName: string = '';
  userRole: string = '';

  @Output() toggleSidebar = new EventEmitter<void>();

  private store = inject(Store);
  private router = inject(Router);
  private userService = inject(UserService);

  openTabs = toSignal(this.store.select(LayoutState.openTabs), { initialValue: [] });
  activeLink = toSignal(this.store.select(LayoutState.activeTabLink), { initialValue: '/' });
  
  userPhoto = this.userService.currentUserPhoto;

  ngOnInit() {
    this.decodeToken();
  }

  decodeToken() {
    const token = this.store.selectSnapshot(AuthState.token);
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.userName = payload.name || payload.sub; 
        this.userRole = payload.role;
        
        if (payload.fotoUrl) {
            this.userService.currentUserPhoto.set(payload.fotoUrl);
        }
      } catch (e) {
        console.error(e);
      }
    }
  }

  onToggleSidebar() {
    this.toggleSidebar.emit();
  }

  logout() {
    this.store.dispatch(new Logout()).subscribe(() => {
      this.router.navigate(['/login']);
    });
  }

  closeTab(event: Event, tab: TabInfo) {
    event.stopPropagation();
    this.store.dispatch(new CloseTab(tab));
  }

  isActive(link: string): boolean {
      return this.router.url === link;
  }
}