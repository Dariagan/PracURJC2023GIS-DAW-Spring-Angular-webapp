import { HttpClient } from '@angular/common/http';
import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { Tweet } from 'app/models/tweet.model';
import { User } from 'app/models/user';
import { Router } from '@angular/router';
import { TweetService } from 'app/services/tweet.service';
import { AuthService } from 'app/services/login.service';
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

  @Input()
  viewingUser?:User;

  authorBanned:boolean = false;
  blocked?:boolean;
  viewerIsAdmin?:boolean;

  displayTweetMedia?:boolean;
  displayUserImage?:boolean;

  ownTweet?:boolean;

  constructor(private loginService:AuthService, private tweetService:TweetService, private userService:UserService, private router: Router){}

  ngOnInit(): void {
    this.authorBanned = UserService.isBanned(this.tweet?.author)
    this.blocked = this.tweet?.author && this.viewingUser?.blocked.includes(this.tweet?.author.username);
    this.viewerIsAdmin = UserService.isAdmin(this.viewingUser)
    this.displayTweetMedia = this.tweet?.hasMedia;
    this.displayUserImage = this.tweet?.author.hasImage;
    this.ownTweet = this.viewingUser != undefined && this.tweet?.author.username == this.viewingUser.username;
  }
  
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['viewingUser.blocked'] && changes['viewingUser.blocked'].currentValue)  {
      this.blocked = this.viewingUser && this.tweet?.author  && this.viewingUser?.blocked.includes(this.tweet?.author.username);
      
    }
  }

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

  public refreshViewingUser(){
    if(this.viewingUser)
      this.userService.getUser(this.viewingUser.username).subscribe(
        refreshedUser => this.viewingUser = refreshedUser,
        () => console.error("couldn't refresh tweet ${this.tweet?.id}'s viewing user")
      )
  }

  public refreshTweet(){
    if(this.tweet)
      this.tweetService.getTweet(this.tweet.id).subscribe(
        tweet => this.tweet = tweet,
        () => console.error("couldn't refresh tweet " + this.tweet?.id)
      )
  }

  like() {
    if(this.viewingUser && this.tweet){
      const i: number = this.tweet.likes.indexOf(this.viewingUser.username);
      if (i !== -1){
        this.tweet.likes.splice(i, 1);
        this.tweetService.unlikeTweet(this.tweet.id, this.viewingUser.username).subscribe();
      }else{
        this.tweet.likes.push(this.viewingUser.username)
        this.tweetService.likeTweet(this.tweet.id, this.viewingUser.username).subscribe();
      }
    }else{
      this.router.navigateByUrl(``);
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
