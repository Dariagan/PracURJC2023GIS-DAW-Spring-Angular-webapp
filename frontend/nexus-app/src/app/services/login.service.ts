import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { User } from 'app/models/user';

const BASE_URL = '/api/auth';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

    logged: boolean = false;
    private user?: User;

    constructor(private httpClient: HttpClient) {
        this.reqIsLogged();
    }

    getUser(): User | undefined {
        return this.user;
    }
    
    isLoggedIn(): boolean {
        return this.logged;
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

    logIn(username: string, password: string): Observable<any> {

        return this.httpClient.post(BASE_URL + "/login", { username: username, password: password }, { withCredentials: true })
        .pipe(
        map((response: any) => {
            this.reqIsLogged();
            return response;
        }),
        catchError((error: any) => {
            return throwError('Credentials not found');
        })
        );
    }

    register(userData: any): Observable<any> {
        return this.httpClient.post("/api/users/", userData)
          .pipe(
            map((response: any) => {
              return response;
            }),
            catchError((error: any) => {
              return throwError('Register Error');
            })
          );
    }

    logOut() {

        return this.httpClient.post(BASE_URL + '/logout', { withCredentials: true })
            .subscribe(() => {
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
