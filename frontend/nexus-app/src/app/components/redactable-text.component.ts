import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-redactable-text',
  template:`
  <span *ngIf="!banned && !blocked; else redacted "[class]="class">
    <ng-content></ng-content>
  </span>
  <ng-template #redacted>
    <span [class]="class">
        {{ banned ? '[This user is banned]' : '[You blocked this user]' }}
    </span>
  </ng-template>

  `,
  styles: [`
    .description{
      top: 160px;
      left: 47px;
      color: rgba(224, 211, 222, 1);
      width: 282px;
      height: auto;
      position: absolute;
      font-size: 18px;
      align-self: auto;
      font-style: Medium;
      text-align: left;
      font-family: Ubuntu;
      font-weight: 500;
      line-height: normal;
      font-stretch: normal;
      margin-right: 0;
      margin-bottom: 0;
      text-decoration: none;
    }
    .tweet {
      color: rgba(255, 255, 255, 1);
      height: auto;
      font-size: 15px;
      align-self: stretch;
      font-style: Medium;
      text-align: left;
      font-family: Ubuntu;
      font-weight: 500;
      line-height: normal;
      font-stretch: normal;
      margin-right: 0;
      margin-bottom: 0;
      text-decoration: none;
    }
  `]
})
export class RedactableTextComponent {

  @Input()
  banned?:boolean

  @Input()
  blocked?:boolean

  @Input()
  class?:string

}
