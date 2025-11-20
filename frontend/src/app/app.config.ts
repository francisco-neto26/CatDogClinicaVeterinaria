import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeuix/themes/aura';
import { provideStore } from '@ngxs/store';
import { AuthState } from './store/auth/auth.state';
import { NgxsLoggerPluginModule } from '@ngxs/logger-plugin';
import { MessageService } from 'primeng/api';
import { authInterceptor } from './core/interceptors/auth-interceptor';
import { LayoutState } from './store/layout/layout.state';

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
    ),
    provideStore(
        [AuthState, LayoutState],
        importProvidersFrom(NgxsLoggerPluginModule.forRoot()) 
    ),
    MessageService
  ]
};