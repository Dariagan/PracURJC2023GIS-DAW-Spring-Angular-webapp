import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { User } from 'app/models/user';
import { UserService } from 'app/services/user.service';


@Component({
  selector: 'app-ban-button',
  template: `
    <div *ngIf="targetUser && viewingUser && targetUser.username != viewingUser.username && isAdmin()">
      <span style="cursor:pointer" (click)="buttonPressed()">
        {{ isBanned ? '[UNBAN USER]' : '[BAN USER]' }}
      </span>
    </div>
  `,
  styles: []
})
export class BanButtonComponent {

  @Input() viewingUser?: User;
  @Input() targetUser?: User;
  
  constructor(private userService: UserService) {}

  isBanned?: boolean;

  @Output()
  banned = new EventEmitter<boolean>()


  ngOnChanges(changes: SimpleChanges): void {
    if (changes['targetUser'] && changes['targetUser'].currentValue)  {
      this.isBanned = UserService.isBanned(this.targetUser)
    }
  }

  isAdmin(): boolean{
    return UserService.isAdmin(this.viewingUser)
  }


  buttonPressed() {
    if (this.isBanned) {
      this.unbanUser();
    } else {
      this.banUser();
    }
  }

  private banUser() {
    if(this.targetUser)
      this.userService.banUser(this.targetUser.username).subscribe(
        () => {
          if (this.targetUser)
            this.targetUser.role = "BANNED"     
          
          this.isBanned = true;
          this.banned.emit(true)
        }
      );
  }

  private unbanUser() {

    if(this.targetUser)
      this.userService.unbanUser(this.targetUser.username).subscribe(
        () => {
          if(this.targetUser)
            this.targetUser.role = "USER"
          this.isBanned = false;        
          this.banned.emit(false)
        }
      );
  }
}
