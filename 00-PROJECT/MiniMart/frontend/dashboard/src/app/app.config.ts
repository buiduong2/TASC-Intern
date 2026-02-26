import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { ProductDataSource } from './features/products/data/product.datasource';
import { ProductFakeDataSource } from './features/products/data/product-fake.datasouce';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    {
      provide: ProductDataSource,
      useClass: ProductFakeDataSource,
    },
  ],
};
