import { State, Action, StateContext, Selector } from '@ngxs/store';
import { Injectable } from '@angular/core';
import { tap } from 'rxjs/operators';
import { Login, Logout } from './auth.actions';
import { AuthService } from '../../core/services/auth';


export interface AuthStateModel {
  token: string | null;
}

@State<AuthStateModel>({
  name: 'auth',
  defaults: {
    // MUDANÇA 1: Lê do sessionStorage (inicia vazio se fechar o navegador)
    token: sessionStorage.getItem('token')
  }
})
@Injectable()
export class AuthState {
  constructor(private authService: AuthService) {}

  @Selector()
  static token(state: AuthStateModel): string | null {
    return state.token;
  }

  @Selector()
  static isAuthenticated(state: AuthStateModel): boolean {
    return !!state.token;
  }

  @Action(Login)
  login(ctx: StateContext<AuthStateModel>, action: Login) {
    return this.authService.login(action.payload).pipe(
      tap((result: any) => {
        const token = result.token;
        // MUDANÇA 2: Salva na sessão atual
        sessionStorage.setItem('token', token);
        ctx.patchState({ token });
      })
    );
  }

  @Action(Logout)
  logout(ctx: StateContext<AuthStateModel>) {
    // MUDANÇA 3: Limpa da sessão
    sessionStorage.removeItem('token');
    ctx.patchState({ token: null });
  }
}