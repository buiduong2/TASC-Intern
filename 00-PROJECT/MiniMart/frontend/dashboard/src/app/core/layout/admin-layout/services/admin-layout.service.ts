import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AdminLayoutService {
  private _isCollapsed: boolean;

  constructor() {
    this._isCollapsed = false;
  }

  isCollapse() {
    return this._isCollapsed;
  }

  toggleCollapse() {

    this._isCollapsed = !this._isCollapsed;
    console.log(this._isCollapsed)
  }
}
