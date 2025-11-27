import { State, Action, StateContext, Selector } from '@ngxs/store';
import { Injectable, inject } from '@angular/core';
import { OpenTab, CloseTab, TabInfo } from './layout.actions';
import { Router } from '@angular/router';

export interface LayoutStateModel {
  openTabs: TabInfo[];
  activeTabLink: string | null;
}

@State<LayoutStateModel>({
  name: 'layout',
  defaults: {
    openTabs: [],
    activeTabLink: null
  }
})
@Injectable()
export class LayoutState {
  private router = inject(Router);

  @Selector()
  static openTabs(state: LayoutStateModel): TabInfo[] {
    return state.openTabs;
  }

  @Selector()
  static activeTabLink(state: LayoutStateModel): string | null {
    return state.activeTabLink;
  }

  @Action(OpenTab)
  openTab(ctx: StateContext<LayoutStateModel>, action: OpenTab) {
    const state = ctx.getState();
    const tabExists = state.openTabs.find(t => t.routerLink === action.payload.routerLink);

    let newTabs = state.openTabs;
    if (!tabExists) {
      newTabs = [...state.openTabs, action.payload];
    }

    ctx.patchState({
      openTabs: newTabs,
      activeTabLink: action.payload.routerLink
    });
  }

  @Action(CloseTab)
  closeTab(ctx: StateContext<LayoutStateModel>, action: CloseTab) {
    const state = ctx.getState();
    const newTabs = state.openTabs.filter(t => t.routerLink !== action.payload.routerLink);

    ctx.patchState({ openTabs: newTabs });

    if (state.activeTabLink === action.payload.routerLink) {
      if (newTabs.length > 0) {
        const lastTab = newTabs[newTabs.length - 1];
        this.router.navigate([lastTab.routerLink]);
        ctx.patchState({ activeTabLink: lastTab.routerLink });
      } else {
        this.router.navigate(['/']);
        ctx.patchState({ activeTabLink: '/' });
      }
    }
  }
}