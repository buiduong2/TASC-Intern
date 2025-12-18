export interface PageResponse<T> {
  content: T[];
  page: {
    totalElements: number;
    totalPages: number;
    pageNumber: number;
    pageSize: number;
    last: boolean;
    first: boolean;
    empty: boolean;
  };
}

export interface FieldError {
  field: string;
  message: string;
}

export interface ValidationErrorResponse {
  status: number;
  error: string;
  message: string;
  timestamp: string;
  errors: FieldError[];
}
