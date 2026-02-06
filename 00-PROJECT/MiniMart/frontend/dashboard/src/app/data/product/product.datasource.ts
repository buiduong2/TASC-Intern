import { Observable } from 'rxjs';
import { Page, Pageable } from '../../models/common/page.model';
import { ProductQuery } from '../../models/product/product-query.model';

export abstract class ProductDataSource {
  abstract findAll(query: ProductQuery, pageable: Pageable<Product>): Observable<Page<Product>>;
}
