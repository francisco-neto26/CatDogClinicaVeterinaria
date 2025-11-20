export interface TabInfo {
  label: string;
  icon: string;
  routerLink: string;
}

export class OpenTab {
  static readonly type = '[Layout] Open Tab';
  constructor(public payload: TabInfo) {}
}

export class CloseTab {
  static readonly type = '[Layout] Close Tab';
  constructor(public payload: TabInfo) {}
}