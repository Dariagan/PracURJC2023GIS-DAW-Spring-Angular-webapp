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

  retrievedTweets:Tweet[] = [];

  currentUser?:User = this.loginService.getUser();

  constructor(private loginService: LoginService, private tweetService: TweetService){ 
    
    this.loadInMoreTweets(0)
  }

  loadInMoreTweets(page: number){ 
    if (this.currentUser)
      this.tweetService.getRecommendedTweetsForUser(this.currentUser.username, page, 10).subscribe(
        newTweets => this.retrievedTweets = this.retrievedTweets.concat(newTweets),
        error => console.log(error) 
      );
    else
      this.tweetService.getTweetsForAnon(page, 10).subscribe(
        newTweets => this.retrievedTweets = this.retrievedTweets.concat(newTweets),
        error => console.log(error) 
      )
  }


}
