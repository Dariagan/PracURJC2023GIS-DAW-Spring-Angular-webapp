import { Component, ElementRef, Injectable } from '@angular/core';
import { LoginService } from 'app/services/login.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
@Injectable({providedIn: 'root'})
export class LoginComponent {

  constructor(private loginService: LoginService){}

  logIn(event: any, username: string, password: string) {

    event.preventDefault();

    this.loginService.logIn(username, password);
  }

  logOut() {
    this.loginService.logOut();
  }
}
