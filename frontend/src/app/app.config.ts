import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';
import { provideStore } from '@ngxs/store';
import { AuthState } from './store/auth/auth.state';
import Aura from '@primeuix/themes/aura';
import { NgxsLoggerPluginModule } from '@ngxs/logger-plugin';
import { authInterceptor } from './core/interceptors/auth-interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideAnimationsAsync(),
    providePrimeNG({
      theme: {
        preset: Aura
      }
    }),
    provideStore(
        [AuthState],
        importProvidersFrom(NgxsLoggerPluginModule.forRoot()) 
    )
  ]
};