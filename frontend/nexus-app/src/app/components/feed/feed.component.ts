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



  @ViewChild(ThreadComponent) threadComponent!: ThreadComponent;

  isAdmin?: boolean;

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe(
      user => {
        this.viewingUser = 
        user; this.isAdmin = UserService.isAdmin(this.viewingUser);
        this.threadComponent.refreshViewingUsers(this.viewingUser)
        
        if(this.viewingUser.following.length > 0)
          this.threadComponent.tweetRetrievalMethod = 
          (page: number, size: number) => this.tweetService.getFollowedUsersTweets(this.viewingUser!.username, page, size)
        else
        this.threadComponent.tweetRetrievalMethod = 
        (page: number, size: number) => this.tweetService.getMostLikedTweets(page, size)

        this.threadComponent.showMoreTweets();
      },
      () => {
        this.threadComponent.tweetRetrievalMethod = 
        (page: number, size: number) => this.tweetService.getNewestTweets(page, size)
        this.threadComponent.showMoreTweets();
      }
    );
    
  }


  ngOnChanges(changes: SimpleChanges): void {
    if (changes['viewingUser'] && changes['viewingUser'].currentValue)  {
      this.isAdmin = UserService.isAdmin(this.viewingUser)
    }
  }

  constructor(private userService: UserService, private tweetService: TweetService){ 
    
  }

  onSearch(input:string){
    let tags: string[] = input.split(/[\s]+/);
    this.threadComponent.tweetRetrievalMethod = (page: number, size: number) => this.tweetService.getTweetsByTags(tags, page, size);
    this.threadComponent.restart()
  }

  moderateButtonClicked(){
    this.threadComponent.tweetRetrievalMethod = (page: number, size: number) => this.tweetService.getMostReportedTweets(page, size);
    this.threadComponent.restart()
  }
  
}
