import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-redactable-text',
  template:`
  <span *ngIf="!banned && !blocked; else redacted "[class]="cssClass">
    <ng-content></ng-content>
  </span>
  <ng-template #redacted>
    <span [class]="cssClass">
      <ng-component *ngIf="banned; else block">
        [banned]
      </ng-component>
      <ng-template #block>
        [blocked]
      </ng-template>
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
  `]
})
export class RedactableTextComponent {

  @Input()
  banned?:boolean

  @Input()
  blocked?:boolean

  @Input()
  cssClass?:string

}
