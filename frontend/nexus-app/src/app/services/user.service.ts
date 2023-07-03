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

  blockUser(blocker: string, blocked: string): Observable<any> {
    const url = `/api/users/${blocker}/blocks`;

    return this.httpClient.post(url, blocked).pipe(
      catchError(error => this.handleError<any>(error))
    );
  }

  unblockUser(blocker: string, unblocked: string): Observable<any> {
    const url = `/api/users/${blocker}/blocks/${unblocked}`;
    return this.httpClient.delete(url).pipe(
      catchError(error => this.handleError<any>(error))
    );
  }

  followUser(follower: string, followed: string): Observable<any> {
    const url = `/api/users/${follower}/following`;

    return this.httpClient.post(url, followed).pipe(
      catchError(error => this.handleError<any>(error))
    );
  }

  unfollowUser(unfollower: string, unfollowed: string): Observable<any> {
    const url = `/api/users/${unfollower}/following/${unfollowed}`;
    return this.httpClient.delete(url).pipe(
      catchError(error => this.handleError<any>(error))
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
