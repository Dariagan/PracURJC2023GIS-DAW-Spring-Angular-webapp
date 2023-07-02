import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Tweet } from 'app/models/tweet.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-thread',
  templateUrl: './thread.component.html',
  styleUrls: ['./thread.component.css']
})
export class ThreadComponent {

  displayedTweets: Tweet[] = [];

  @Input()
  tweetRetrievalMethod!: (page: number, size: number) => Observable<Tweet[]>;

  page: number = 0;
  size: number = 10;

  constructor(){
  }

  ngOnInit(): void {
    this.showMoreTweets()
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
