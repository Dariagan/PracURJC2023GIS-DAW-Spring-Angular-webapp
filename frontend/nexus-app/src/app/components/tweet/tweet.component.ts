import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, Input, Output } from '@angular/core';
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

  @Output()
  deleted = new EventEmitter<number>();

  viewingUser?:User = this.loginService.getUser();

  authorBanned:boolean = this.tweet?.author.role == 'BANNED';
  blocked?:boolean = this.tweet?.author && this.viewingUser?.blocked.includes(this.tweet?.author.username);
  viewerIsAdmin:boolean = this.viewingUser?.role === 'ADMIN';

  displayTweetMedia?:boolean = this.tweet?.hasMedia;
  displayUserImage?:boolean = this.tweet?.author.hasImage;

  ownTweet:boolean = this.viewingUser != undefined && this.tweet?.author.username == this.viewingUser.username;

  constructor(private loginService:LoginService, private tweetService:TweetService, private userService:UserService, private router: Router){}

  getTweetMediaURL(): string {
    if (this.tweet) {
      return `/api/tweets/${this.tweet.id}/image`;
    }
    return '';
  }

  handleTweetMediaError(){
    this.displayTweetMedia = false;
   }
  handleUserImageError(){
    this.displayUserImage = false;
  }

  like() {
    if(this.viewingUser && !this.ownTweet && this.tweet){
      const i: number = this.tweet.likes.indexOf(this.viewingUser.username);
      if (i !== -1){
        this.tweet.likes.splice(i, 1);
        this.tweetService.unlikeTweet(this.tweet.id, this.viewingUser.username).subscribe();
      }else{
        this.tweet.likes.push(this.viewingUser.username)
        this.tweetService.likeTweet(this.tweet.id, this.viewingUser.username).subscribe();
      }
    }else{
      this.router.navigateByUrl(`login`);
    }
  }

  report() {
    if(this.viewingUser){
      if(this.tweet && !this.ownTweet && !this.tweet.reporters.includes(this.viewingUser.username)){
        this.tweet.reporters.push(this.viewingUser.username)
        this.tweetService.reportTweet(this.tweet.id, this.viewingUser.username).subscribe();
      }
    }else{
      this.router.navigateByUrl(`login`);
    }
  }


  share() {
  }
  reply(){
  }

  

  delete() {
    if(this.tweet && this.viewingUser && this.viewerIsAdmin){
      this.tweetService.deleteTweet(this.tweet.id).subscribe(
        () => (this.deleted.emit(this.tweet?.id))
      )
    }
  }


}
