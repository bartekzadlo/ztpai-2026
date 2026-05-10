import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  isLoggedIn$!: Observable<boolean>;
  username: string | null = null;
  isAdmin = false;
  menuOpen = false;

  constructor(private auth: AuthService, private router: Router) {}

  ngOnInit() {
    this.isLoggedIn$ = this.auth.isLoggedIn$;
    this.auth.isLoggedIn$.subscribe(loggedIn => {
      if (loggedIn) {
        this.username = this.auth.getUsername();
        this.isAdmin = this.auth.isAdmin();
      } else {
        this.username = null;
        this.isAdmin = false;
      }
    });
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/games']);
    this.menuOpen = false;
  }
}
