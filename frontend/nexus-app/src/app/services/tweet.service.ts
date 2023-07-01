import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Tweet } from 'app/models/tweet.model';
import { throwError, Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TweetService {

  constructor(private httpClient: HttpClient) { }

  getUserTweets(user: string, page: number, size: number): Observable<Tweet[]> {
    let url = "/api/users/" + user + "/tweets?page=" + page + "&size=" + size;
    return this.httpClient.get<Tweet[]>(url).pipe(
      catchError(error => this.handleError<Tweet[]>(error))
    );
  }

  getRecommendedTweetsForUser(user: string, page: number, size: number): Observable<Tweet[]> {
    
    let url = "/api/users/" + user + "/following/tweets?page=" + page + "&size=" + size;
    return this.httpClient.get<Tweet[]>(url).pipe(
      catchError(error => this.handleError<Tweet[]>(error))
    );
  }

  getTweetsForAnon(page: number, size: number): Observable<Tweet[]> {
    
    let url = "/api/tweets?page=" + page + "&size=" + size;
    return this.httpClient.get<Tweet[]>(url).pipe(
      catchError(error => this.handleError<Tweet[]>(error))
    );
  }

  likeTweet(tweetId: number, username: string): Observable<any> {
    let url = `/api/tweets/${tweetId}/likes`;
    let object = { "username": username }
    let httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    }
    return this.httpClient.post(url, object, httpOptions).pipe(
      catchError(error => this.handleError<any>(error))
    );
  }

  reportTweet(tweetId: number, username: string): Observable<any> {
    let url = `/api/tweets/${tweetId}/reports`;
    let object = { "username": username }
    let httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    }
    return this.httpClient.post(url, object, httpOptions).pipe(
      catchError(error => this.handleError<any>(error))
    );
  } 
  
  private handleError<T>(error: any): Observable<T> {

    console.error('An error occurred:', error);

    return of([] as T);
  }
}
