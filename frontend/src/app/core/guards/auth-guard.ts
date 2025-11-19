import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { Store } from '@ngxs/store';
import { AuthState } from '../../store/auth/auth.state';

export const authGuard: CanActivateFn = (route, state) => {
  const store = inject(Store);
  const router = inject(Router);
  
  // Seleciona a variável 'isAuthenticated' do nosso estado
  const isAuthenticated = store.selectSnapshot(AuthState.isAuthenticated);

  if (!isAuthenticated) {
    // Se não estiver logado, redireciona para o login
    router.navigate(['/login']);
    return false;
  }

  // Se estiver logado, deixa passar
  return true;
};