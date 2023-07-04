import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'app/services/login.service';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css']
})
export class SignUpComponent {

  constructor(private loginService: AuthService, private router: Router){}

  signUp(event: any, username: string, password: string, email:string) {

    event.preventDefault();

    this.loginService.signUp(username, password).subscribe(
      () => {
        this.router.navigate([''])
      },
      () => {
        alert("Error on signup");
      }
    );
  }
}
