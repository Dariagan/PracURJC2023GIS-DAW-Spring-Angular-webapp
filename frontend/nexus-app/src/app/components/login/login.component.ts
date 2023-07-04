import { Component, ElementRef, Injectable } from '@angular/core';
import { AuthService } from 'app/services/login.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
@Injectable({providedIn: 'root'})
export class LoginComponent {

  constructor(private loginService: AuthService, private router: Router){}

  logIn(event: any, username: string, password: string) {

    event.preventDefault();

    this.loginService.logIn(username, password).subscribe(
      () => {
        this.router.navigate(['feed'])
      },
      () => {
        alert("Wrong credentials");
      }
    );
  }

}
