import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../environments/environment';

export const baseUrlInterceptor: HttpInterceptorFn = (req, next) => {
  const apiUrl = environment.apiUrl;

  // Nếu request đã có http/https thì bỏ qua
  if (/^https?:\/\//i.test(req.url)) return next(req);

  const cloned = req.clone({
    url: `${apiUrl}${req.url}`,
  });

  return next(cloned);
};
