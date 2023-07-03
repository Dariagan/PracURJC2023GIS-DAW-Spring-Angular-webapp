import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-searchbar',
  template: `
    <div class="roundedrectangle">
      <img
        src="assets/feed/iconsaxlinearboxsearchi133-oodp.svg"
        alt="IconsaxLinearboxsearchI133"
        class="tinysearchicon"
      />
      <form class="searchplaceholdertext" (ngSubmit)="onSubmit()">
        <input type="text" placeholder="Search by tags..." name="searchInput" [(ngModel)]="searchText" (keydown.enter)="onSubmit()"/>
      </form>
    </div>
  `,
  styles: [`
    .roundedrectangle{
      width: 357px;
      height: 44px;
      display: flex;
      padding: 0px 0px;
      overflow: hidden;
      position: relative;
      align-items: center;
      flex-shrink: 0;
      border-color: rgba(83, 76, 134, 1);
      border-style: solid;
      border-width: 0 0 2px 0;
      border-radius: 100px;
      margin-bottom: 46px;
      justify-content: center;
      background-image: linear-gradient(270deg, rgba(83, 76, 134, 1) 0%, rgba(83, 76, 134, 0) 100%);
      }
    .tinysearchicon{
      width: 16px;
      height: 16px;
      position: relative;
      margin-right: 8px;
    }
    .searchplaceholdertext{
      color: rgba(224, 211, 222, 1);
      height: auto;
      font-size: 14px;
      align-self: auto;
      font-style: Bold;
      text-align: center;
      font-family: Ubuntu;
      font-weight: 700;
      line-height: normal;
      font-stretch: normal;
      margin-right: 0;
      margin-bottom: 0;
      text-decoration: none;
    }
  `
  ]
})
export class SearchbarComponent {
  searchText: string = '';

  @Output()
  search = new EventEmitter<string>()

  onSubmit(){
    this.search.emit(this.searchText)
  }
}
