import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-nav-button',
  template:`
    <button class="navbutton" (click)="redirect()">
      <span class="buttontext">{{ buttonText }}</span>
    </button>
  `,
  styleUrls:['./nav-button.component.css']
})
export class NavButtonComponent {
  @Input() url?: string;
  @Input() buttonText!: string;
  
  constructor(private router: Router){
  }

  redirect(){
    if(this.url != undefined)
      this.router.navigate([this.url]).catch(error => {
        console.error('Navigation error:', error);
      });;
  }
}

