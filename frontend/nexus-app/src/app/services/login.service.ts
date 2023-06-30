import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from 'app/models/user';

const BASE_URL = '/api/auth';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  logged: boolean = false;
  user?: User;

  constructor(private httpClient: HttpClient) {
      this.reqIsLogged();
  }

  reqIsLogged() {

      this.httpClient.get('/api/users/me', { withCredentials: true }).subscribe(
          response => {
              this.user = response as User;
              this.logged = true;
          },
          error => {
              if (error.status != 404) {
                  console.error('Error when asking if logged: ' + JSON.stringify(error));
              }
          }
      );

  }

  logIn(username: string, password: string) {

      this.httpClient.post(BASE_URL + "/login", { username: username, password: password }, { withCredentials: true })
          .subscribe(
              (response) => this.reqIsLogged(),
              error => {
                if (true) {
                    console.error(JSON.stringify(error));
                }
            }
          );

  }

  logOut() {

      return this.httpClient.post(BASE_URL + '/logout', { withCredentials: true })
          .subscribe((resp: any) => {
              console.log("LOGOUT: Successful");
              this.logged = false;
              this.user = undefined;
          });

  }

  isLogged() {
      return this.logged;
  }

  isAdmin() {
      return this.user && this.user.role == "ADMIN";
  }

  currentUser() {
      return this.user;
  }
}
