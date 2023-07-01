import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpClient: HttpClient) { }


  private handleError<T>(error: any): Observable<T> {

    console.error('An error occurred:', error);

    return of([] as T);
  }
}
