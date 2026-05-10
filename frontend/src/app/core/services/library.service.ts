import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserGameEntry, LibraryUpsertRequest, PagedResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class LibraryService {
  private readonly API = 'http://localhost:8080/api/library';

  constructor(private http: HttpClient) {}

  getLibrary(page = 0, size = 20): Observable<PagedResponse<UserGameEntry>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse<UserGameEntry>>(this.API, { params });
  }

  getGameStatus(gameId: number): Observable<UserGameEntry | null> {
    return this.http.get<UserGameEntry | null>(`${this.API}/game/${gameId}`);
  }

  upsert(req: LibraryUpsertRequest): Observable<UserGameEntry> {
    return this.http.put<UserGameEntry>(this.API, req);
  }

  remove(gameId: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${gameId}`);
  }
}
