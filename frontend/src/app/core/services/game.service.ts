import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GameResponse, GameRequest, PagedResponse, GameSearchParams } from '../models';

@Injectable({ providedIn: 'root' })
export class GameService {
  private readonly API = 'http://localhost:8080/api/games';

  constructor(private http: HttpClient) {}

  getAll(params: GameSearchParams = {}): Observable<PagedResponse<GameResponse>> {
    let httpParams = new HttpParams()
      .set('page', params.page ?? 0)
      .set('size', params.size ?? 12)
      .set('sort', params.sort ?? 'title,asc');
    if (params.title) httpParams = httpParams.set('title', params.title);
    if (params.genre) httpParams = httpParams.set('genre', params.genre);
    if (params.platform) httpParams = httpParams.set('platform', params.platform);
    if (params.releaseYearFrom != null) httpParams = httpParams.set('releaseYearFrom', params.releaseYearFrom);
    if (params.releaseYearTo != null) httpParams = httpParams.set('releaseYearTo', params.releaseYearTo);
    if (params.hasStory != null) httpParams = httpParams.set('hasStory', params.hasStory);
    return this.http.get<PagedResponse<GameResponse>>(this.API, { params: httpParams });
  }

  getById(id: number): Observable<GameResponse> {
    return this.http.get<GameResponse>(`${this.API}/${id}`);
  }

  create(req: GameRequest): Observable<GameResponse> {
    return this.http.post<GameResponse>(this.API, req);
  }

  update(id: number, req: GameRequest): Observable<GameResponse> {
    return this.http.put<GameResponse>(`${this.API}/${id}`, req);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }
}
