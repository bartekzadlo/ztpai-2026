import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { GamesRoutingModule } from './games-routing.module';
import { GameListComponent } from './components/game-list/game-list.component';
import { GameDetailComponent } from './components/game-detail/game-detail.component';
import { GameFormComponent } from './components/game-form/game-form.component';

@NgModule({
  declarations: [GameListComponent, GameDetailComponent, GameFormComponent],
  imports: [SharedModule, GamesRoutingModule]
})
export class GamesModule {}
