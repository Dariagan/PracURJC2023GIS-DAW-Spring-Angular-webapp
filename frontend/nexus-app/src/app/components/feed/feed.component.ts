import { Component } from '@angular/core';
import { Tweet } from 'app/models/tweet.model';
import { User } from 'app/models/user';
import { LoginService } from 'app/services/login.service';
import { TweetService } from 'app/services/tweet.service';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css', './feed.boxes.css', './feed.style.css']
})
export class FeedComponent {

  displayedTweets?: Tweet[];

  currentUser?:User;

  constructor(loginService: LoginService, tweetService: TweetService){


    if (this.currentUser)
      tweetService.getRecommendedTweetsForUser(this.currentUser.name, 0, 10).subscribe(
        tweets => this.displayedTweets = tweets,
        error => console.log(error) 
      );
    else
      tweetService.getTweetsForAnon(0, 10).subscribe(
        tweets => this.displayedTweets = tweets,
        error => console.log(error) 
      )
  }


}
