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

  getTweet(id: number): Observable<Tweet> {
    const url = "/api/tweets/" + id;
    return this.httpClient.get<Tweet>(url).pipe(
      catchError(error => this.handleError<Tweet>(error))
    );
  }

  getUserTweets(user: string, page: number, size: number): Observable<Tweet[]> {
    let url = "/api/users/" + user + "/tweets?page=" + page + "&size=" + size;
    return this.httpClient.get<Tweet[]>(url).pipe(
      catchError(error => this.handleError<Tweet[]>(error))
    );
  }

  postUserToBlocklist(blocker: string, blocked: string): Observable<any> {
    const url = `/api/users/${blocker}/blocks`;
    const blockedUser = { blocked: blocked };

    return this.httpClient.post(url, blockedUser).pipe(
      catchError(error => this.handleError<any>(error))
    );
  }

  postTweet(tweetText: string, image?: File, tags?: string[]): Observable<Tweet> {
    const formData = new FormData();
    formData.append('tweetText', tweetText);

    if (image) {
      formData.append('image', image);
    }
    if (tags) {
      formData.append('tags', JSON.stringify(Array.from(tags)));
    }

    return this.httpClient.post<Tweet>('/api/tweets', formData).pipe(
      catchError(error => this.handleError<Tweet>(error))
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
    const url = `/api/tweets/${tweetId}/likes`;
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    const requestBody = { "text": username };
    return this.httpClient.post(url, requestBody, httpOptions).pipe(
      catchError(error => this.handleError<any>(error))
    );
  }
  unlikeTweet(tweetId: number, username: string): Observable<any> {
    const url = `/api/tweets/${tweetId}/likes/${username}`;
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    };
    return this.httpClient.delete(url, httpOptions).pipe(
      catchError(error => this.handleError<any>(error))
    );
  }

  reportTweet(tweetId: number, username: string): Observable<any> {
    const url = `/api/tweets/${tweetId}/reports`;
    const requestBody = { "text": username }
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    }
    return this.httpClient.post(url, requestBody, httpOptions).pipe(
      catchError(error => this.handleError<any>(error))
    );
  } 

  deleteTweet(tweetId: number): Observable<any> {
    console.log("asd")
    const url = `/api/tweets/${tweetId}`;
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' })
    }
    return this.httpClient.delete(url, httpOptions).pipe(
      catchError(error => this.handleError<any>(error))
    );
  } 
  
  private handleError<T>(error: any): Observable<T> {

    console.error('An error occurred:', error);

    return of([] as T);
  }
}
