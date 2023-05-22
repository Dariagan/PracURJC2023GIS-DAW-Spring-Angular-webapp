import { Component, ElementRef, Injectable } from '@angular/core';
import { NexuschiComponent } from '../nexuschi/nexuschi.component';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
@Injectable({providedIn: 'root'})
export class LoginComponent {

  @Input()
  title: string

  constructor(private httpClient: HttpClient){

    @ViewChild('titleInput') titleInput: ElementRef;

    this.titleINput.nativeElement.vale

    
  }
}
