import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../core/service/user.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-protected',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './protected.component.html',
})
export class ProtectedComponent {
  constructor(private userService: UserService, private router: Router) {}

  logout(): void {
    this.userService.logout();
    this.router.navigate(['/']);
  }
}
