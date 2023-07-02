import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from 'app/models/user';
import { Observable, catchError, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpClient: HttpClient) { }

  getUser(username: string): Observable<User> {
    const url = "/api/users/" + username;
    return this.httpClient.get<User>(url).pipe(
      catchError(error => this.handleError<User>(error))
    );
  }
  
  postImage(username: string, imageFile: File): Observable<any> {
    const url = `/api/users/${username}/image`;
  
    const formData = new FormData();
    formData.append('imageFile', imageFile);
  
    return this.httpClient.post(url, formData).pipe(
      catchError(error => this.handleError(error))
    );
  }
  

  private handleError<T>(error: any): Observable<T> {

    console.error('An error occurred:', error);

    return of([] as T);
  }
}
