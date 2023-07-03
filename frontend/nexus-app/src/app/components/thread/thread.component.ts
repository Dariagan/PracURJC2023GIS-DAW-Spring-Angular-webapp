import { Component, EventEmitter, Input, Output, QueryList, ViewChildren } from '@angular/core';
import { Tweet } from 'app/models/tweet.model';
import { Observable } from 'rxjs';
import { TweetComponent } from '../tweet/tweet.component';

@Component({
  selector: 'app-thread',
  templateUrl: './thread.component.html',
  styleUrls: ['./thread.component.css']
})
export class ThreadComponent {

  displayedTweets: Tweet[] = [];

  @ViewChildren(TweetComponent) tweetComponents!: QueryList<TweetComponent>;

  @Input()
  tweetRetrievalMethod!: (page: number, size: number) => Observable<Tweet[]>;

  page: number = 0;
  size: number = 10;

  constructor(){
  }

  ngOnInit(): void {
    this.showMoreTweets()
  }

  public loadInLastTweet(){
    this.tweetRetrievalMethod(0, 1).subscribe(
      newTweet => this.displayedTweets = newTweet.concat(this.displayedTweets)
    )
  }

  refreshTweetsBlocked(blocked: boolean){//se puede mejorar y hacer q solo refresque al especificado
    this.tweetComponents.forEach(tweetComponent => {
      tweetComponent.blocked = blocked;
    });
  }

  refreshTweets(){//se puede mejorar y hacer q solo refresque al especificado
    this.tweetComponents.forEach(tweetComponent => {
      tweetComponent.refreshTweet();
    });
  }
  refreshUsers(){
    this.tweetComponents.forEach(tweetComponent => {
      tweetComponent.refreshViewingUser();
    });
  }
  showMoreTweets(){
    this.tweetRetrievalMethod(this.page, this.size).subscribe(
      newTweets => this.displayedTweets = this.displayedTweets.concat(newTweets)
    )
    this.page++;
  }
  removeTweetFromList(id: number){
    this.displayedTweets = this.displayedTweets.filter(tweet => tweet.id !== id);
  }
}
