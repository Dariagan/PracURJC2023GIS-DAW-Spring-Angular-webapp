import { Component, EventEmitter, Input, Output } from '@angular/core';
import { User } from 'app/models/user';
import { TweetService } from 'app/services/tweet.service';
import { UserService } from 'app/services/user.service';

@Component({
  selector: 'app-write-tweet',
  template: `
    <div class="profile">
      <button *ngIf="hide" (click)="this.hide = false" class="profileanon-btn1">Write</button>
      <div *ngIf="!hide" class="writebox">
        <textarea [(ngModel)]="tweetText"></textarea>
        <input type="file" (change)="onMediaSelected($event)" accept="image/*">
        <button (click)="postTweet(tweetText, selectedMedia)">Publish</button>
        <button (click)="cancelTweet()">Cancel</button>
      </div>
    </div>
  `,
  styles: [`
    :host {
	    --box: 307px;
    }
    .profile{
      top: 20px;
      width: 615px;
      left: calc(50% - var(--box));
      display: flex;
      padding: 0 24px;
      overflow: hidden;
      position: absolute;
      align-items: center;
      border-color: rgba(83, 76, 134, 1);
      border-style: dashed;
      border-width: 2px;
      border-radius: 50px;
      flex-direction: column;
      justify-content: center;
      
      background-color: rgba(30, 26, 59, 1);
      padding-bottom: 40px;
    }
    .writebox {
      align-items: center;
    }
    .profileanon-btn1 {
      top: 0px;
      
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
    }
  `]
})
export class WriteTweetComponent {
  tweetText: string = '';
  selectedMedia: File | undefined;
  hide: boolean = true;

  constructor(private tweetService: TweetService) {}

  postTweet(text: string, media?: File) {
    this.tweetService.postTweet(text, media).subscribe();
    this.resetForm();
  }

  cancelTweet() {
    this.resetForm();
  }

  resetForm() {
    this.tweetText = '';
    this.selectedMedia = undefined;
    this.hide = true;
  }

  onMediaSelected(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    if (inputElement.files && inputElement.files.length > 0) {
      this.selectedMedia = inputElement.files[0];
    } else {
      this.selectedMedia = undefined;
    }
  }
}
