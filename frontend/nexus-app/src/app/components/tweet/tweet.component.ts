import { HttpClient } from '@angular/common/http';
import { Component, Input } from '@angular/core';
import { Tweet } from 'app/models/tweet.model';
import { User } from 'app/models/user';
import { Router } from '@angular/router';
import { TweetService } from 'app/services/tweet.service';
import { LoginService } from 'app/services/login.service';
import { UserService } from 'app/services/user.service';

@Component({
  selector: 'app-tweet',
  templateUrl: './tweet.component.html',
  styleUrls: ['./tweet.component.css']
})
export class TweetComponent {

  @Input()
  tweet?: Tweet;

  authorBanned:boolean = this.tweet?.author.role == 'BANNED';
  
  adminView:boolean = this.tweet?.author.role === 'ADMIN';

  tweetMediaError:boolean = false;
  userImageError:boolean = false;

  viewingUser?:User = this.loginService.getUser();
  ownTweet:boolean = this.viewingUser != undefined && this.tweet?.author == this.viewingUser;

  constructor(private loginService:LoginService, private tweetService:TweetService, private userService:UserService, private router: Router){
    
  }

  getTweetMediaURL(): string {
    if (this.tweet) {
      return `/api/tweets/${this.tweet.id}/image`;
    }
    return '';
  }

  getUserImageURL(): string {
    if (this.tweet) {
      return `/api/users/${this.tweet.author.username}/image`;
    }
    return '';
  }

  handleTweetMediaError(){
    this.tweetMediaError = true;
   }

  handleUserImageError(){
    this.userImageError = true;
  }

  like() {
    if(this.viewingUser && !this.ownTweet && this.tweet){
      this.tweetService.likeTweet(this.tweet.id, this.viewingUser.username)
    }else{
      this.router.navigateByUrl(`login`);
    }
  }

  //TODO
  share() {
  }

  //TODO
  reply(){
  }

  report() {
    if(this.viewingUser && !this.ownTweet && this.tweet){
      this.tweetService.reportTweet(this.tweet.id, this.viewingUser.username)
    }else{
      this.router.navigateByUrl(`login`);
    }
  }

  delete() {
    // Emit an event with the tweet ID
    // Example: this.deleteTweet.emit(tweetId);
  }


}
