import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'marca',
        data: { pageTitle: 'concesionarioApp.marca.home.title' },
        loadChildren: () => import('./marca/marca.module').then(m => m.MarcaModule),
      },
      {
        path: 'modelo',
        data: { pageTitle: 'concesionarioApp.modelo.home.title' },
        loadChildren: () => import('./modelo/modelo.module').then(m => m.ModeloModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
