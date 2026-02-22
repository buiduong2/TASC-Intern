import { inject } from '@angular/core';

import {
  RedirectCommand,
  Router,
  type ActivatedRouteSnapshot,
  type ResolveFn,
  type RouterStateSnapshot,
} from '@angular/router';
import { ProductService } from '@products/services/product.service';
import { catchError, of } from 'rxjs';

export const productDetailResolver: ResolveFn<Product> = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const productService = inject(ProductService);
  const router = inject(Router);
  const productId = route.paramMap.get('id');

  if (!productId) {
    return new RedirectCommand(router.parseUrl('/products'));
  }
  return productService.findById(productId).pipe(
    catchError((err) => {
      console.error(err);
      return of(new RedirectCommand(router.parseUrl('/products/not-found')));
    }),
  );
};
