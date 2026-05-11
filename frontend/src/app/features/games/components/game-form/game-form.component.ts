import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { GameService } from '../../../../core/services/game.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-game-form',
  templateUrl: './game-form.component.html',
  styleUrls: ['./game-form.component.scss']
})
export class GameFormComponent implements OnInit {
  form!: FormGroup;
  editId: number | null = null;
  loading = false;
  error = '';

  genres = ['RPG', 'Action', 'Adventure', 'Strategy', 'Shooter', 'Sports', 'Simulation', 'Puzzle', 'Horror', 'Racing', 'Action-Adventure', 'Action RPG', 'JRPG', 'Survival Horror', 'Stealth', 'Hack and Slash', 'Metroidvania', 'Platformer', 'Roguelike', 'Run and Gun', 'FPS', 'RTS', 'Fighting', 'Battle Royale', 'TPS', 'MOBA', 'MMORPG', 'Sandbox', 'Survival'];
  platforms = ['PC', 'PlayStation 3', 'PlayStation 4', 'PlayStation 5', 'Xbox 360', 'Xbox One', 'Xbox Series X', 'Nintendo Switch', 'Wii U', 'Mobile'];
  profiles = [{ value: 'DEFAULT', label: 'Domyślny' }, { value: 'NARRATIVE', label: 'Fabularny' }, { value: 'MULTIPLAYER', label: 'Gameplay' }];

  constructor(private fb: FormBuilder, private gameService: GameService, private router: Router, private route: ActivatedRoute, private auth: AuthService) {}

  ngOnInit() {
    if (!this.auth.isAdmin()) { this.router.navigate(['/games']); return; }
    this.form = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(255)]],
      description: ['', Validators.required],
      genres: [[], [Validators.required, Validators.minLength(1)]],
      platforms: [[], [Validators.required, Validators.minLength(1)]],
      releaseYear: [2024, [Validators.required, Validators.min(1958), Validators.max(2030)]],
      coverUrl: [''],
      hasStory: [true],
      defaultRatingProfile: ['DEFAULT']
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id && id !== 'new') {
      this.editId = Number(id);
      this.gameService.getById(this.editId).subscribe(g => {
        this.form.patchValue(g);
      });
    }
  }

  toggleGenre(genre: string) {
    const genres = this.form.get('genres')?.value as string[];
    const index = genres.indexOf(genre);
    if (index >= 0) {
      genres.splice(index, 1);
    } else {
      genres.push(genre);
    }
    this.form.patchValue({ genres });
  }

  togglePlatform(platform: string) {
    const platforms = this.form.get('platforms')?.value as string[];
    const index = platforms.indexOf(platform);
    if (index >= 0) {
      platforms.splice(index, 1);
    } else {
      platforms.push(platform);
    }
    this.form.patchValue({ platforms });
  }

  isGenreSelected(genre: string): boolean {
    return (this.form.get('genres')?.value as string[]).includes(genre);
  }

  isPlatformSelected(platform: string): boolean {
    return (this.form.get('platforms')?.value as string[]).includes(platform);
  }

  submit() {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = '';
    const req = this.form.value;
    const obs = this.editId ? this.gameService.update(this.editId, req) : this.gameService.create(req);
    obs.subscribe({
      next: game => this.router.navigate(['/games', game.id]),
      error: () => { this.error = 'Błąd zapisu gry.'; this.loading = false; }
    });
  }
}
