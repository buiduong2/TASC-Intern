import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection,
} from '@angular/core';

import { provideRouter } from '@angular/router';

import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideToastr } from 'ngx-toastr';
import { routes } from '../router/app.routes';
import { baseUrlInterceptor } from '../utils/interceptors/base-url.interceptor';
import { provideAuth } from '../configurations/auth.configuration';

export const appConfig: ApplicationConfig = {
  providers: [
    ...provideAuth({ tokenTtlThresholdMs: 30000 }),
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withFetch(), withInterceptors([baseUrlInterceptor])), // <-- cần cái này
    provideAnimations(),
    provideToastr(),
  ],
};
