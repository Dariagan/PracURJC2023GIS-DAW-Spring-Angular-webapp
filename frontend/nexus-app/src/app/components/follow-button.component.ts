import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { User } from 'app/models/user';
import { UserService } from 'app/services/user.service';


@Component({
  selector: 'app-follow-button',
  template: `
    <div class="followbutton" *ngIf="viewingUser && targetUser && targetUser.username != viewingUser.username">
      <span class="followtext" (click)="buttonPressed()">
        {{ followed ? 'Follow' : 'Unfollow' }}
      </span>
    </div>
  `,
  styles: [`
    .followbutton {
      top: 101px;
      left: 151px;
      width: 113px;
      display: flex;
      padding: 10px;
      overflow: hidden;
      position: absolute;
      align-items: center;
      flex-shrink: 0;
      border-color: transparent;
      border-style: solid;
      border-width: 2px;
      border-radius: 100px;
      justify-content: center;
      background-color: rgba(224, 211, 222, 1);
      cursor:pointer;
    }
    .followtext {
      color: rgba(30, 26, 59, 1);
      height: auto;
      font-size: 16px;
      align-self: auto;
      font-style: Bold;
      text-align: center;
      font-family: Ubuntu;
      font-weight: 700;
      line-height: normal;
      font-stretch: normal;
      margin-right: 0;
      margin-bottom: 0;
      text-decoration: none;
    }
  `]
})
export class FollowButtonComponent {
  @Input() targetUser?: User;
  @Input() viewingUser?: User;

  constructor(private userService: UserService) {}

  followed?: boolean;

  ngOnInit(): void {
    this.followed = this.viewingUser && this.targetUser != undefined && this.viewingUser?.following.includes(this.targetUser.username);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['viewingUser'] && changes['viewingUser'].currentValue)  {
      this.followed = this.viewingUser && this.targetUser != undefined && this.viewingUser?.following.includes(this.targetUser.username);
      
    }
  }


  buttonPressed() {
    if (this.followed) {
      this.unfollowUser();
    } else {
      this.followUser();
    }
  }
  private followUser() {
    if(this.viewingUser && this.targetUser)
      this.userService.followUser(this.viewingUser.username, this.targetUser.username).subscribe(
        () => {
          this.followed = true;
          if(this.viewingUser && this.targetUser){
            this.viewingUser?.following.push(this.targetUser.username);
            this.targetUser?.followers.push(this.viewingUser.username);
          }
        }
      );
  }
  private unfollowUser() {
    if(this.viewingUser && this.targetUser)
      this.userService.unfollowUser(this.viewingUser.username, this.targetUser.username).subscribe(
        () => {
          this.followed = false;
          if(this.viewingUser && this.targetUser){
            this.viewingUser.following = this.viewingUser?.following.filter(((username) => username !== this.targetUser?.username))
            this.targetUser.followers = this.targetUser?.followers.filter(((username) => username !== this.viewingUser?.username))
          }
        }
      );
  }
}
