export interface ApiResponse<T> {
  timestamp: string;
  status: number;
  message: string;
  data: T;
  path: string | null;
}

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
