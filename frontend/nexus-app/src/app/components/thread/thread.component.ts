import { Component, EventEmitter, Input, Output, QueryList, ViewChildren } from '@angular/core';
import { Tweet } from 'app/models/tweet.model';
import { Observable } from 'rxjs';
import { TweetComponent } from '../tweet/tweet.component';
import { User } from 'app/models/user';

@Component({
  selector: 'app-thread',
  templateUrl: './thread.component.html',
  styleUrls: ['./thread.component.css']
})
export class ThreadComponent {

  displayedTweets: Tweet[] = [];

  @ViewChildren(TweetComponent) tweetComponents!: QueryList<TweetComponent>;

  tweetRetrievalMethod?: (page: number, size: number) => Observable<Tweet[]>;

  @Input()
  viewingUser?: User;

  page: number = 0;
  size: number = 10;

  public async restart(){
    await new Promise((resolve) => setTimeout(resolve, 150));
    this.resetPage()
    this.showMoreTweets()
  }
  public resetPage(){
    this.page = 0;
    this.displayedTweets = []
  }
  showMoreTweets(){
    if(this.tweetRetrievalMethod){
      this.tweetRetrievalMethod(this.page, this.size).subscribe(
        newTweets => {
          this.displayedTweets = this.displayedTweets.concat(newTweets); 
          this.page++;
        }
      )
    }
  }

  public loadInLastTweet(){
    if(this.tweetRetrievalMethod)
      this.tweetRetrievalMethod(0, 1).subscribe(
        newTweet => this.displayedTweets = newTweet.concat(this.displayedTweets)
      )
  }

  refreshTweetsBlocked(blocked: boolean){
    this.tweetComponents.forEach(tweetComponent => {
      tweetComponent.blocked = blocked;
    });
  }
  refreshTweetsBanned(banned: boolean){
    this.tweetComponents.forEach(tweetComponent => {
      tweetComponent.authorBanned = banned;
      if (tweetComponent.tweet)
        tweetComponent.tweet.author.role = "BANNED";
    });
  }

  sortByMostReported(){
    
  }
  
  removeTweetFromList(id: number){
    this.displayedTweets = this.displayedTweets.filter(tweet => tweet.id !== id);
  }

  refreshTweets(){
    this.tweetComponents.forEach(tweetComponent => {
      tweetComponent.refreshTweet();
    });
  }
  refreshViewingUsers(viewingUser: User){
    this.viewingUser = viewingUser
    this.tweetComponents.forEach(tweetComponent => {
      tweetComponent.viewingUser = viewingUser
    });
  }
}
