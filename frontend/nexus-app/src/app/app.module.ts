import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { NexuschiComponent } from './components/nexuschi.component';
import { HttpClientModule } from '@angular/common/http';
import { NavButtonComponent } from './components/nav-button/nav-button.component';
import { NavComponent } from './components/nav.component';
import { FeedComponent } from './components/feed/feed.component';
import { ProfileComponent } from './components/profile/profile.component';
import { SignUpComponent } from './components/sign-up/sign-up.component';
import { TweetComponent } from './components/tweet/tweet.component';
import { ThreadComponent } from './components/thread/thread.component';
import { UsernameHyperlinkComponent } from './components/username-hyperlink.component';
import { BlockButtonComponent } from './components/block-button.component';
import { BanButtonComponent } from './components/ban-button.component';
import { UserImageComponent } from './components/user-image.component';
import { RedactableTextComponent } from './components/redactable-text.component';
import { UploadImagePromptComponent } from './components/upload-image-prompt.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    NexuschiComponent,
    NavButtonComponent,
    NavComponent,
    FeedComponent,
    ProfileComponent,
    SignUpComponent,
    TweetComponent,
    ThreadComponent,
    UsernameHyperlinkComponent,
    BlockButtonComponent,
    BanButtonComponent,
    UserImageComponent,
    RedactableTextComponent,
    UploadImagePromptComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
