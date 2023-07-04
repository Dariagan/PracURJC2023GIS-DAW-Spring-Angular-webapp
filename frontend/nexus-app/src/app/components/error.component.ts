import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-error',
  template: `
    <html lang="english">
      <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <meta charset="utf-8" />
        <meta property="twitter:card" content="summary_large_image" />
        <style data-tag="reset-style-sheet">
          html {  line-height: 1.15;}body {  margin: 0;}* {  box-sizing: border-box;  border-width: 0;  border-style: solid;}p,li,ul,pre,div,h1,h2,h3,h4,h5,h6,figure,blockquote,figcaption {  margin: 0;  padding: 0;}button {  background-color: transparent;}button,input,optgroup,select,textarea {  font-family: inherit;  font-size: 100%;  line-height: 1.15;  margin: 0;}button,select {  text-transform: none;}button,[type="button"],[type="reset"],[type="submit"] {  -webkit-appearance: button;}button::-moz-focus-inner,[type="button"]::-moz-focus-inner,[type="reset"]::-moz-focus-inner,[type="submit"]::-moz-focus-inner {  border-style: none;  padding: 0;}button:-moz-focus,[type="button"]:-moz-focus,[type="reset"]:-moz-focus,[type="submit"]:-moz-focus {  outline: 1px dotted ButtonText;}a {  color: inherit;  text-decoration: inherit;}input {  padding: 2px 4px;}img {  display: block;}html { scroll-behavior: smooth  }
        </style>
        <style data-tag="default-style-sheet">
          html {
            font-family: Inter;
            font-size: 16px;
          }
          body {
            font-weight: 400;
            font-style:normal;
            text-decoration: none;
            text-transform: none;
            letter-spacing: normal;
            line-height: 1.15;
            color: var(--dl-color-gray-black);
            background-color: var(--dl-color-gray-white);
          }
        </style>
        <link
          rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Inter:wght@100;200;300;400;500;600;700;800;900&amp;display=swap"
          data-tag="font"
        />
        <link
          rel="stylesheet"
          href="https://fonts.googleapis.com/css2?family=Ubuntu:ital,wght@0,300;0,400;0,500;0,700;1,300;1,400;1,500;1,700&amp;display=swap"
          data-tag="font"
        />
      </head>
      <body>
        <div>
          <div class="frame404page-frame404page">
            <div class="frame404page-content">
              <span class="frame404page-text">404</span>
              <span class="frame404page-text2">Page not found</span>
              <button class="frame404page-backtonexusbutton" (click)="navigateToFeed()">
                <span class="frame404page-text4">Back to nexus</span>
              </button>
            </div>
          </div>
        </div>
      </body>
    </html>

  `,
  styles: [`
    .frame404page-frame404page {
      width: 100vw;
      height: 100vh;
      display: flex;
      overflow: hidden;
      position: relative;
      align-items: flex-start;
      flex-shrink: 0;
      border-color: transparent;
      background-image: linear-gradient(150deg, rgba(30, 26, 59, 1) 0%, rgba(51, 46, 89, 1) 100%);
    }
    .frame404page-content {
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      margin: auto;
      display: flex;
      overflow: hidden;
      position: absolute;
      align-items: center;
      border-color: transparent;
      flex-direction: column;
    }
    .frame404page-text {
      color: rgba(215, 38, 61, 1);
      height: auto;
      font-size: 128px;
      align-self: auto;
      font-style: Bold;
      text-align: center;
      font-family: Ubuntu;
      font-weight: 700;
      line-height: normal;
      font-stretch: normal;
      margin-right: 0;
      margin-bottom: 20px;
      text-decoration: none;
    }
    .frame404page-text2 {
      color: rgba(224, 211, 222, 1);
      height: auto;
      font-size: 48px;
      align-self: auto;
      font-style: Medium;
      text-align: center;
      font-family: Ubuntu;
      font-weight: 500;
      line-height: normal;
      font-stretch: normal;
      margin-right: 0;
      margin-bottom: 20px;
      text-decoration: none;
    }
    .frame404page-backtonexusbutton {
      width: 164px;
      height: 41px;
      display: flex;
      overflow: hidden;
      position: relative;
      align-items: flex-start;
      flex-shrink: 0;
      border-color: rgba(83, 76, 134, 1);
      border-style: solid;
      border-width: 2px;
      border-radius: 60px;
      background-color: rgba(224, 211, 222, 1);
      cursor: pointer;
    }
    .frame404page-text4 {
      top: 11px;
      left: 31px;
      color: rgba(30, 26, 59, 1);
      height: auto;
      position: absolute;
      font-size: 16px;
      align-self: auto;
      font-style: Medium;
      text-align: center;
      font-family: Ubuntu;
      font-weight: 500;
      line-height: normal;
      font-stretch: normal;
      margin-right: 0;
      margin-bottom: 0;
      text-decoration: none;
    }`
  ]
})
export class ErrorComponent {

  constructor(private router: Router) {}

  navigateToFeed() {
    this.router.navigate(['feed']);
  }
}
