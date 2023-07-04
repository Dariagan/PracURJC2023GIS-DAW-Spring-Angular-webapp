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

    constructor(private httpClient: HttpClient) {}

    logIn(username: string, password: string): Observable<any> {

        return this.httpClient.post(BASE_URL + "/login", { username: username, password: password }, { withCredentials: true })
        .pipe(
        map((response: any) => {
            return response;
        }),
        catchError(() => {
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
            });

    }

    isLogged() {
        return this.logged;
    }
}
