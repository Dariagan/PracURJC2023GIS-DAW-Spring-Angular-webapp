import { Component } from '@angular/core';

@Component({
  selector: 'app-nav',
  template: `
    <div class="navbar">
      <app-nexuschi>
      </app-nexuschi>

      <ng-content>
      </ng-content>
    </div>
  `,
  styles: [`
    .navbar {
      top: 0;
      left: 0;
      width: 100%;
      padding: 8px 13px;
      overflow: hidden;
      border-color: rgba(83, 76, 134, 1);
      border-style: solid;
      border-width: 0 0 2px 0;
      background-color: rgba(30, 26, 59, 1);
    }
  `]
})
export class NavComponent {

}
