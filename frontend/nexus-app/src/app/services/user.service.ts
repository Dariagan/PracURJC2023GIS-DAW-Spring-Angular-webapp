import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from 'app/models/user';
import { Observable, catchError, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpClient: HttpClient) { }

  public getCurrentUser(): Observable<User> {
    const url = "/api/users/me";
    return this.httpClient.get<User>(url).pipe(
      catchError(error => this.handleError<User>(error))
    );
  }

  public getUser(username: string): Observable<User> {
    const url = "/api/users/" + username;
    return this.httpClient.get<User>(url).pipe(
      catchError(error => this.handleError<User>(error))
    );
  }

  public blockUser(blocker: string, blocked: string): Observable<any> {
    const url = `/api/users/${blocker}/blocks`;

    return this.httpClient.post(url, blocked).pipe(
      catchError(error => this.handleError<any>(error))
    );
  }

  public unblockUser(blocker: string, unblocked: string): Observable<any> {
    const url = `/api/users/${blocker}/blocks/${unblocked}`;
    return this.httpClient.delete(url).pipe(
      catchError(error => this.handleError<any>(error))
    );
  }

  public banUser(username: string): Observable<any> {
    const url = `/api/users/${username}`;
    const params = new HttpParams().set('banned', 'true');
  
    return this.httpClient.patch(url, null, { params }).pipe(
      catchError(error => this.handleError<any>(error))
    );
  }
  
  public unbanUser(username: string): Observable<any> {
    const url = `/api/users/${username}`;
    const params = new HttpParams().set('banned', 'false');
  
    return this.httpClient.patch(url, null, { params }).pipe(
      catchError(error => this.handleError<any>(error))
    );
  }

  public followUser(follower: string, followed: string): Observable<any> {
    const url = `/api/users/${follower}/following`;

    return this.httpClient.post(url, followed).pipe(
      catchError(error => this.handleError<any>(error))
    );
  }

  public unfollowUser(unfollower: string, unfollowed: string): Observable<any> {
    const url = `/api/users/${unfollower}/following/${unfollowed}`;
    return this.httpClient.delete(url).pipe(
      catchError(error => this.handleError<any>(error))
    );
  }
  
  public postImage(username: string, imageFile: File): Observable<any> {
    const url = `/api/users/${username}/image`;
  
    const formData = new FormData();
    formData.append('imageFile', imageFile);
  
    return this.httpClient.post(url, formData).pipe(
      catchError(error => this.handleError(error))
    );
  }

  public static isAdmin(user?: User) :boolean{
    return user != undefined && user.role == 'ADMIN'
  }
  public static isBanned(user?: User) :boolean{
    return user != undefined && user.role == 'BANNED'
  }
  public static isUser(user?: User) :boolean{
    return user != undefined && user.role == 'USER'
  }

  private handleError<T>(error: any): Observable<T> {

    console.error('An error occurred:', error);

    throw error
  }
}
