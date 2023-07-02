import { Component } from '@angular/core';
import { Tweet } from 'app/models/tweet.model';
import { User } from 'app/models/user';
import { LoginService } from 'app/services/login.service';
import { TweetService } from 'app/services/tweet.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css', './feed.boxes.css', './feed.style.css']
})
export class FeedComponent {

  retrievedTweets:Tweet[] = [];

  viewingUser?:User = this.loginService.getUser();

  getTweetsMethod: (page: number, size: number) => Observable<Tweet[]>;

  constructor(private loginService: LoginService, private tweetService: TweetService){ 
    this.getTweetsMethod = this.viewingUser
      ? (page: number) => this.tweetService.getRecommendedTweetsForUser(this.viewingUser!.username, page, 10)
      : (page: number) => this.tweetService.getTweetsForAnon(page, 10);
  }
}
