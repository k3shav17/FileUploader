import {
  HttpErrorResponse,
  HttpEvent,
  HttpEventType,
} from '@angular/common/http';
import { Component } from '@angular/core';
import { saveAs } from 'file-saver';
import { FileService } from './file.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  filenames: string[] = [];
  // fileStatus: { status?: string; requestType?: string; percent: number };
  status: string = '';
  requestType: string = '';
  percent: number = 0;

  constructor(private fileService: FileService) {}

  // function to upload files
  onUploadFiles(files: File[]): void {
    const formData = new FormData();
    for (const file of files) {
      formData.append('files', file, file.name);
      this.fileService.upload(formData).subscribe(
        (event) => {
          console.log(event);
          this.reportProgress(event);
        },
        (error: HttpErrorResponse) => console.log(error)
      );
    }
  }

  onDownloadFiles(filename: string): void {
    this.fileService.download(filename).subscribe(
      (event) => {
        console.log(event);
        this.reportProgress(event);
      },
      (error: HttpErrorResponse) => console.log(error)
    );
  }

  reportProgress(httpEvent: HttpEvent<string[] | Blob>): void {
    switch (httpEvent.type) {
      case HttpEventType.UploadProgress:
        this.updateStatus(httpEvent.loaded, httpEvent.total, 'Uploading... ');
        break;
      case HttpEventType.DownloadProgress:
        this.updateStatus(httpEvent.loaded, httpEvent.total, 'Downloading... ');
        break;
      case HttpEventType.ResponseHeader:
        console.log('Header returned', httpEvent);
        break;
      case HttpEventType.Response:
        if (httpEvent.body instanceof Array) {
          this.status = 'done';
          for (const filename of httpEvent.body) {
            this.filenames.unshift(filename);
          }
        } else {
          // download logic
          saveAs(
            new File([httpEvent.body], httpEvent.headers.get('File-Name'), {
              type: `${httpEvent.headers.get('Content-Type')};charset=utf-8`,
            })
          );
        }
        this.status = 'done';
        break;
      default:
        console.log(httpEvent);
    }
  }
  updateStatus(loaded: number, total: number, requestType: string) {
    this.status = 'progress';
    this.requestType = requestType;
    this.percent = Math.round((loaded / total) * 100);
  }
}
