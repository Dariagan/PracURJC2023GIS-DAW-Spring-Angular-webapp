import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { User } from 'app/models/user';
import { LoginService } from 'app/services/login.service';
import { UserService } from 'app/services/user.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css', './profile.boxes.css', './profile.style.css']
})
export class ProfileComponent {

  user?: User;
  viewingUser?: User;
  ownProfile?: boolean;
  blockedByViewer?: boolean;
  banned!: boolean;
  

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      const username = params['username'];
      this.userService.getUser(username).subscribe(
        foundUser => this.user = foundUser,
        error => error//TODO redirigir a 404 page si hay error
      )
      console.log(this.user?.following)
      this.viewingUser = this.loginService.getUser();
      this.ownProfile = this.viewingUser?.username === this.user?.username;
      this.blockedByViewer = !this.ownProfile && this.viewingUser && this.user && this.viewingUser.blocked.includes(this.user?.username);
      this.banned = this.user?.role == "BANNED";
        
    })
  }


  constructor(private loginService: LoginService, private userService: UserService, private activatedRoute: ActivatedRoute){

  }
}
