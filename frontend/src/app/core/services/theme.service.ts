import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly THEME_KEY = 'sysmo-theme';
  
  // Expose the current theme state as a Signal for reactive UI updates
  isDark = signal<boolean>(false);

  constructor() {
    this.initTheme();
  }

  private initTheme(): void {
    const savedTheme = localStorage.getItem(this.THEME_KEY);
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    
    if (savedTheme === 'dark' || (!savedTheme && prefersDark)) {
      this.setDark(true);
    } else {
      this.setDark(false);
    }
  }

  toggleTheme(): void {
    this.setDark(!this.isDark());
  }

  private setDark(isDark: boolean): void {
    if (isDark) {
      document.documentElement.classList.add('dark');
      localStorage.setItem(this.THEME_KEY, 'dark');
    } else {
      document.documentElement.classList.remove('dark');
      localStorage.setItem(this.THEME_KEY, 'light');
    }
    this.isDark.set(isDark);
  }
}
