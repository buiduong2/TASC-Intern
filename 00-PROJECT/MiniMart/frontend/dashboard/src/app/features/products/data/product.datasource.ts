import { Observable } from 'rxjs';
import { Page, Pageable } from '@shared/models/page.model';
import { ProductQuery } from '@products/models/product-query.model';

export abstract class ProductDataSource {
  abstract findAll(query: ProductQuery, pageable: Pageable<Product>): Observable<Page<Product>>;
}
