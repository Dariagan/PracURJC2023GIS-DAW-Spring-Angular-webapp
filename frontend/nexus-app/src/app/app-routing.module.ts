import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { FeedComponent } from './components/feed/feed.component';
import { ProfileComponent } from './components/profile/profile.component';
import { ChartComponent } from './components/chart/chart.component';
import { ErrorComponent } from './components/error.component';
import { LogoutComponent } from './components/logout.component';

const routes: Routes = [

  { path: '', component: LoginComponent },
  { path: 'feed', component: FeedComponent},
  { path: 'chart', component: ChartComponent},
  { path: 'logout', component: LogoutComponent },
  {
    path: 'u/:username',
    component: ProfileComponent
  },
  { path: '**', component: ErrorComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }