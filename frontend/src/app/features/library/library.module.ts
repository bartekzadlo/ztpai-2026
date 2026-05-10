import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { LibraryRoutingModule } from './library-routing.module';
import { LibraryListComponent } from './components/library-list/library-list.component';

@NgModule({
  declarations: [LibraryListComponent],
  imports: [SharedModule, LibraryRoutingModule]
})
export class LibraryModule {}
