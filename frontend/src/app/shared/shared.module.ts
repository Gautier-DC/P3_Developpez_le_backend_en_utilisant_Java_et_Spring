import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OwnerInfoComponent } from './components/owner-info/owner-info.component';
import { MatIconModule } from "@angular/material/icon";

@NgModule({
  declarations: [
    OwnerInfoComponent
  ],
  imports: [
    CommonModule,
    MatIconModule
],
  exports: [
    OwnerInfoComponent
  ],
})
export class SharedModule { }
