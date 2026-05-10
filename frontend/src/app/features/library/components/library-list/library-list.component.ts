import { Component, OnInit } from '@angular/core';
import { LibraryService } from '../../../../core/services/library.service';
import { UserGameEntry, LibraryStatus } from '../../../../core/models';

@Component({
  selector: 'app-library-list',
  templateUrl: './library-list.component.html',
  styleUrls: ['./library-list.component.scss']
})
export class LibraryListComponent implements OnInit {
  entries: UserGameEntry[] = [];
  loading = true;
  error = '';
  page = 0;
  totalPages = 0;
  filterStatus: LibraryStatus | '' = '';

  statuses: LibraryStatus[] = ['PLAN_TO_PLAY', 'PLAYING', 'COMPLETED', 'ON_HOLD', 'DROPPED'];
  statusLabels: Record<LibraryStatus, string> = {
    PLAN_TO_PLAY: 'Planuję', PLAYING: 'Gram', COMPLETED: 'Ukończona', ON_HOLD: 'Pauza', DROPPED: 'Porzucona'
  };
  statusColors: Record<LibraryStatus, string> = {
    PLAN_TO_PLAY: '#818cf8', PLAYING: '#34d399', COMPLETED: '#fbbf24', ON_HOLD: '#94a3b8', DROPPED: '#f87171'
  };

  constructor(private libraryService: LibraryService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.libraryService.getLibrary(this.page).subscribe({
      next: res => { this.entries = res.items; this.totalPages = res.totalPages; this.loading = false; },
      error: () => { this.error = 'Nie udało się wczytać biblioteki.'; this.loading = false; }
    });
  }

  get filtered(): UserGameEntry[] {
    return this.filterStatus ? this.entries.filter(e => e.status === this.filterStatus) : this.entries;
  }

  remove(gameId: number, title: string) {
    if (!confirm(`Usunąć "${title}" z biblioteki?`)) return;
    this.libraryService.remove(gameId).subscribe(() => this.load());
  }

  onPageChange(p: number) { this.page = p; this.load(); }
}
