import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GameService } from '../../../../core/services/game.service';
import { ReviewService } from '../../../../core/services/review.service';
import { LibraryService } from '../../../../core/services/library.service';
import { AuthService } from '../../../../core/services/auth.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { GameResponse, ReviewResponse, GameRatingStats, LibraryStatus } from '../../../../core/models';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-game-detail',
  templateUrl: './game-detail.component.html',
  styleUrls: ['./game-detail.component.scss']
})
export class GameDetailComponent implements OnInit {
  game: GameResponse | null = null;
  reviews: ReviewResponse[] = [];
  stats: GameRatingStats | null = null;
  loading = true;
  error = '';

  reviewPage = 0;
  reviewTotalPages = 0;

  isLoggedIn = false;
  isAdmin = false;
  showReviewForm = false;
  reviewForm!: FormGroup;
  reviewError = '';
  reviewLoading = false;
  editingReviewId: number | null = null;

  libraryStatus: LibraryStatus | '' = '';
  libraryStatuses: LibraryStatus[] = ['PLAN_TO_PLAY', 'PLAYING', 'COMPLETED', 'ON_HOLD', 'DROPPED'];
  statusLabels: Record<LibraryStatus, string> = {
    PLAN_TO_PLAY: 'Planuję zagrać',
    PLAYING: 'Gram',
    COMPLETED: 'Ukończona',
    ON_HOLD: 'Pauza',
    DROPPED: 'Porzucona'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private gameService: GameService,
    private reviewService: ReviewService,
    private libraryService: LibraryService,
    private auth: AuthService,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isLoggedIn = !!this.auth.getToken();
    this.isAdmin = this.auth.isAdmin();
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadAll(id);
    this.initReviewForm();
  }

  resetReviewForm() { this.initReviewForm(); }

  private initReviewForm(review?: ReviewResponse) {
    this.reviewForm = this.fb.group({
      title: [review?.title || '', [Validators.required, Validators.maxLength(200)]],
      content: [review?.content || '', [Validators.required, Validators.minLength(10)]],
      gameplayScore: [review?.gameplayScore || 7, [Validators.required, Validators.min(1), Validators.max(10)]],
      graphicsScore: [review?.graphicsScore || 7, [Validators.required, Validators.min(1), Validators.max(10)]],
      soundScore: [review?.soundScore || 7, [Validators.required, Validators.min(1), Validators.max(10)]],
      storyScore: [review?.storyScore || null],
      replayValueScore: [review?.replayValueScore || 7, [Validators.required, Validators.min(1), Validators.max(10)]],
      ratingProfile: [review?.ratingProfile || 'DEFAULT']
    });
  }

  private loadAll(id: number) {
    this.loading = true;
    const requests: any = {
      game: this.gameService.getById(id),
      reviews: this.reviewService.getForGame(id, 0, 10),
      stats: this.reviewService.getStats(id)
    };

    if (this.isLoggedIn) {
      requests.libraryEntry = this.libraryService.getGameStatus(id);
    }

    forkJoin(requests).subscribe({
      next: (data: any) => {
        this.game = data.game;
        this.reviews = data.reviews.items;
        this.reviewTotalPages = data.reviews.totalPages;
        this.stats = data.stats;
        if (data.libraryEntry) {
          this.libraryStatus = data.libraryEntry.status;
        }
        this.loading = false;
      },
      error: () => { this.error = 'Nie udało się wczytać danych gry.'; this.loading = false; }
    });
  }

  loadReviews(page: number) {
    this.reviewPage = page;
    this.reviewService.getForGame(this.game!.id, page).subscribe(res => {
      this.reviews = res.items;
      this.reviewTotalPages = res.totalPages;
    });
  }

  submitReview() {
    if (this.reviewForm.invalid || !this.game) return;
    this.reviewLoading = true;
    this.reviewError = '';
    const req = { ...this.reviewForm.value, gameId: this.game.id };

    const obs = this.editingReviewId
      ? this.reviewService.update(this.editingReviewId, req)
      : this.reviewService.create(req);

    obs.subscribe({
      next: () => {
        this.showReviewForm = false;
        this.editingReviewId = null;
        this.loadReviews(0);
        this.reviewService.getStats(this.game!.id).subscribe(s => this.stats = s);
        this.reviewLoading = false;
      },
      error: err => {
        this.reviewError = err.status === 409 ? 'Już napisałeś recenzję tej gry.' : 'Błąd zapisu.';
        this.reviewLoading = false;
      }
    });
  }

  editReview(r: ReviewResponse) {
    this.initReviewForm(r);
    this.editingReviewId = r.id;
    this.showReviewForm = true;
  }

  deleteReview(id: number) {
    if (!confirm('Usunąć recenzję?')) return;
    this.reviewService.delete(id).subscribe(() => this.loadReviews(0));
  }

  addToLibrary(status: LibraryStatus) {
    if (!this.game) return;
    this.libraryService.upsert({ gameId: this.game.id, status, favorite: false, owned: true, hoursPlayed: 0 })
      .subscribe(() => { this.libraryStatus = status; });
  }

  deleteGame() {
    if (!this.game || !confirm(`Usunąć "${this.game.title}"?`)) return;
    this.gameService.delete(this.game.id).subscribe(() => this.router.navigate(['/games']));
  }

  scoreColor(score: number): string {
    if (score >= 8) return '#34d399';
    if (score >= 6) return '#fbbf24';
    return '#f87171';
  }
}
