import { Component, SimpleChanges, ViewChild } from '@angular/core';
import { Tweet } from 'app/models/tweet.model';
import { User } from 'app/models/user';
import { AuthService } from 'app/services/login.service';
import { TweetService } from 'app/services/tweet.service';
import { Observable } from 'rxjs';
import { ThreadComponent } from '../thread/thread.component';
import { UserService } from 'app/services/user.service';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css', './feed.boxes.css', './feed.style.css']
})
export class FeedComponent {

  retrievedTweets:Tweet[] = [];

  viewingUser?:User 

  getTweetsMethod: (page: number, size: number) => Observable<Tweet[]>;

  @ViewChild(ThreadComponent) threadComponent!: ThreadComponent;

  isAdmin?: boolean;

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe(
      user => {this.viewingUser = user; this.isAdmin = UserService.isAdmin(this.viewingUser)}

    );
    
  }


  ngOnChanges(changes: SimpleChanges): void {
    if (changes['viewingUser'] && changes['viewingUser'].currentValue)  {
      this.isAdmin = UserService.isAdmin(this.viewingUser)
    }
  }

  constructor(private userService: UserService, private tweetService: TweetService){ 
    this.getTweetsMethod = this.viewingUser
      ? (page: number, size: number) => this.tweetService.getFollowedUsersTweets(this.viewingUser!.username, page, size)
      : (page: number, size: number) => this.tweetService.getNewestTweets(page, size);
  }

  onSearch(input:string){
    let tags: string[] = input.split(/[\s]+/);
    this.getTweetsMethod = (page: number, size: number) => this.tweetService.getTweetsByTags(tags, page, size);
    this.threadComponent.restart()
  }

  moderateButtonClicked(){
    this.getTweetsMethod = (page: number, size: number) => this.tweetService.getMostReportedTweets(page, size);
    this.threadComponent.restart()
  }
  
}
