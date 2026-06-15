import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { config } from './app/app.config.server';

const bootstrap = (ctx: any) => bootstrapApplication(AppComponent, config, ctx);
export default bootstrap;