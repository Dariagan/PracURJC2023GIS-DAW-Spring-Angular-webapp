import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { User } from 'app/models/user';
import { UploadImagePromptComponent } from './upload-image-prompt.component';
import { UserService } from 'app/services/user.service';

@Component({
  selector: 'app-user-image',
  template: `

      <ng-container *ngIf="user?.hasImage && !banned && !blocked; else defaultPic">
        <img
        [src]="url"
        [ngClass]="['round', class]"
        (error)="emitError()"
        (click)="handleClick()"
        />
      </ng-container>
      <ng-template #defaultPic>
        <img
        src="/assets/anonpfp.jpg"
        alt="imgI135"
        [ngClass]="['round', class]"
        (click)="handleClick()"
        />
      </ng-template>
      <app-upload-image-prompt *ngIf="showUploadPrompt" (hide)="showUploadPrompt = false" (imageSelected)="uploadImage($event)">
      </app-upload-image-prompt>
  `,
  styles: [`
    .round{
      border-radius: 50%;
      height: 100%;
      width: auto;
      left: 0px;
      top: 0px;
      cursor: pointer;
    }

    .tweet{
      top: 0px;
      left: 0px;
      width: 55px;
      height: 55px;
      position: absolute;
      border-color: transparent;
    }
    .profile {
      top: 20px;
      left: 20px;
      width: 109px;
      height: 109px;
      display: flex;
      overflow: hidden;
      position: absolute;
      box-sizing: content-box;
      align-items: center;
      flex-shrink: 0;
      border-color: transparent;
      border-style: solid;
      border-width: 4px;
      justify-content: center;
    }
  `]
})
export class UserImageComponent implements OnInit, OnChanges {

  @Input()
  user?: User;

  url = '';
  
  banned: boolean = false;

  @Input()
  blocked?: boolean = false;

  @Input()
  ownProfile?: boolean;

  @Input()
  class?: string;

  showUploadPrompt?: boolean;

  @Output()
  imageError = new EventEmitter();

  emitError() {
    this.imageError.emit();
    console.log("imageerror")
  }

  ngOnInit(): void {
    this.showUploadPrompt = false;
    this.updateImageUrl();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['user'] && changes['user'].currentValue)  {
      console.log("ngonchanges1"+this.user?.hasImage+this.banned+this.blocked)
      this.updateImageUrl();
      this.banned = this.user?.role== "BANNED";
    }
  }

  constructor(private userService: UserService) { }

  handleClick() {
    if (this.ownProfile) {
      this.showUploadPrompt = true;
    }
  }

  uploadImage(file: File) {
    if (this.user) {
      this.userService.postImage(this.user.username, file).subscribe(() => {
        this.user!.hasImage = true;
        this.updateImageUrl();
      });
    }
  }

  private updateImageUrl() {
    if (this.user) {
      const timestamp = new Date().getTime(); // Generate a timestamp
      this.url = `/api/users/${this.user.username}/image?timestamp=${timestamp}`; // Append timestamp as a query parameter
    }
  }
}