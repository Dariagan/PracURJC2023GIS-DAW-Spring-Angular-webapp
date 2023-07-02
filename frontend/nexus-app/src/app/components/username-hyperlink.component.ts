import { Component, Input } from '@angular/core';
import { User } from 'app/models/user';

@Component({
  selector: 'app-username-hyperlink',
  template: `
    <a *ngIf="!banned && !blocked; else redacted"  [routerLink]="['/u/', user?.username]" [class]="cssClass">
      {{user?.username}}
    </a>
    <ng-template #redacted>
      <span [class]="cssClass">
        <ng-component *ngIf="banned; else block">
          [Banned]
        </ng-component>
        <ng-template #block>
          [Blocked]
        </ng-template>
      </span>
    </ng-template>
  `,
  styles: [`
    .tweet{
    top: 10px;
    color: rgba(255, 255, 255, 1);
    width: 148px;
    height: auto;
    position: absolute;
    font-size: 16px;
    align-self: auto;
    font-style: Bold;
    text-align: left;
    font-family: Ubuntu;
    font-weight: 700;
    line-height: normal;
    font-stretch: normal;
    margin-right: 0;
    margin-bottom: 0;
    text-decoration: none;
    }
    .profile { color: blue; }
  `]
})
export class UsernameHyperlinkComponent {
//hacer lo de cambiar css class en angular 
  @Input()
  user?:User

  @Input()
  cssClass?:string

  @Input()
  blocked!:boolean;

  banned:boolean = this.user?.role == 'BANNED';

}
