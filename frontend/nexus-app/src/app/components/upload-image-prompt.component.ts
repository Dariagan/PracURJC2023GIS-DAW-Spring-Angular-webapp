import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-upload-image-prompt',
  template:`
    <div class="upload-prompt-container">
      <div class="upload-prompt-overlay">
        <div class="upload-prompt">
          <h2>Upload Picture</h2>
          <input type="file" accept="image/*" (change)="onImageSelected($event)">
          <button (click)="hideUploadPrompt()">Close</button>
        </div>
      </div>
    </div>

  `,
  styles: [`
    .upload-prompt-container {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .upload-prompt-overlay {
      background-color: rgba(0, 0, 0, 0.5); /* Semi-transparent background */
    }

    .upload-prompt {
      background-color: #fff;
      padding: 20px;
    }
  `]
})
export class UploadImagePromptComponent {

  @Output() 
  hide = new EventEmitter();
  @Output() 
  imageSelected = new EventEmitter<File>();

  hideUploadPrompt() {
    this.hide.emit();
  }

  onImageSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const files = input.files;
    if (files && files.length > 0) {
      const selectedImage = files[0];
      this.imageSelected.emit(selectedImage);
    }
  }
}
