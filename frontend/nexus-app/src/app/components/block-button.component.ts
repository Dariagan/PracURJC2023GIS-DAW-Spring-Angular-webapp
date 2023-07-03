import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { User } from 'app/models/user';
import { TweetService } from 'app/services/tweet.service';
import { UserService } from 'app/services/user.service';


@Component({
  selector: 'app-block-button',
  template: `
    <div *ngIf="viewingUser && targetUser && targetUser != viewingUser.username">
      <span style="cursor:pointer" (click)="buttonPressed()">
        {{ isBlocked ? '[UNBLOCK USER]' : '[BLOCK USER]' }}
      </span>
    </div>
  `,
  styles: []
})
export class BlockButtonComponent {
  @Input() targetUser?: string;
  @Input() viewingUser?: User;

  constructor(private userService: UserService) {}

  isBlocked?: boolean;

  @Output()
  blocked = new EventEmitter<boolean>()

  ngOnInit(): void {
    this.isBlocked = this.viewingUser && this.targetUser != undefined && this.viewingUser?.blocked.includes(this.targetUser);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['viewingUser'] && changes['viewingUser'].currentValue)  {
      this.isBlocked = this.viewingUser && this.targetUser != undefined && this.viewingUser?.blocked.includes(this.targetUser);
      
    }
  }


  buttonPressed() {
    if (this.isBlocked) {
      this.unblockUser();
    } else {
      this.blockUser();
    }
  }

  private blockUser() {
    const blocker = this.viewingUser?.username; 
    const blocked = this.targetUser; 

    if(blocker && blocked)
      this.userService.blockUser(blocker, blocked).subscribe(
        () => {
          this.isBlocked = true;
          this.viewingUser?.blocked.push(blocked);
          this.blocked.emit(true)
        }
      );
  }

  private unblockUser() {
    const unblocker = this.viewingUser?.username; 
    const unblocked = this.targetUser; 

    if(unblocker && unblocked != undefined && this.viewingUser)
      this.userService.unblockUser(unblocker, unblocked).subscribe(
        () => {
          this.isBlocked = false;
          if(this.viewingUser)
            this.viewingUser.blocked = this.viewingUser?.blocked.filter(((username) => username !== unblocked))
          this.blocked.emit(false)
        }
      );
  }
}
