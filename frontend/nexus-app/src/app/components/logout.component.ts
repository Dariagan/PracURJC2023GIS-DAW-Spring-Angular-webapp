import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'app/services/login.service';

@Component({
  selector: 'app-logout',
  template: `
    <p>
      logout works!
    </p>
  `,
  styles: [
  ]
})
export class LogoutComponent {

  constructor(private loginService: AuthService, private router: Router) { }

  ngOnInit(): void {
    this.loginService.logOut();
    this.router.navigate([''])
  }
}
