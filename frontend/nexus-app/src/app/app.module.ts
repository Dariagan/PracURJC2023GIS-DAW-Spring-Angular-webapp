import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { ComponentNameComponent } from './component-name/component-name.component';
import { NexuschiComponent } from './nexuschi/nexuschi.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    ComponentNameComponent,
    NexuschiComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
