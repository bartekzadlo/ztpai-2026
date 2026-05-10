import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReviewResponse, ReviewRequest, PagedResponse, GameRatingStats } from '../models';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private readonly API = 'http://localhost:8080/api/reviews';

  constructor(private http: HttpClient) {}

  getForGame(gameId: number, page = 0, size = 10, sort = 'createdAt,desc'): Observable<PagedResponse<ReviewResponse>> {
    const params = new HttpParams().set('page', page).set('size', size).set('sort', sort);
    return this.http.get<PagedResponse<ReviewResponse>>(`${this.API}/game/${gameId}`, { params });
  }

  getStats(gameId: number): Observable<GameRatingStats> {
    return this.http.get<GameRatingStats>(`${this.API}/game/${gameId}/stats`);
  }

  create(req: ReviewRequest): Observable<ReviewResponse> {
    return this.http.post<ReviewResponse>(this.API, req);
  }

  update(id: number, req: ReviewRequest): Observable<ReviewResponse> {
    return this.http.put<ReviewResponse>(`${this.API}/${id}`, req);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }
}
