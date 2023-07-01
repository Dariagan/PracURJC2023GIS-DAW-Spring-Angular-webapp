import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Tweet } from 'app/models/tweet.model';

@Component({
  selector: 'app-thread',
  templateUrl: './thread.component.html',
  styleUrls: ['./thread.component.css']
})
export class ThreadComponent {

  @Input()
  displayedTweets!: Tweet[];

  page:number = 0;

  @Output()
  moreTweetsClicked = new EventEmitter<number>();

  constructor(){
  
  }

  showMoreTweets(){
    this.page++;
    this.moreTweetsClicked.emit(this.page);
  }
}
