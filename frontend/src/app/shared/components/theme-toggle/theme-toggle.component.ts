import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ThemeService } from '../../../core/services/theme.service';

@Component({
  selector: 'app-theme-toggle',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button 
      (click)="toggle()"
      class="relative inline-flex items-center justify-center p-2 rounded-full overflow-hidden text-slate-500 hover:bg-slate-100 dark:text-slate-400 dark:hover:bg-slate-800 transition-colors focus:outline-none focus:ring-2 focus:ring-primary"
      aria-label="Toggle Dark Mode"
    >
      <!-- Sun Icon (shows in dark mode) -->
      <svg 
        class="w-5 h-5 absolute transition-all duration-300 transform" 
        [ngClass]="themeService.isDark() ? 'scale-100 rotate-0 opacity-100' : 'scale-0 -rotate-90 opacity-0'"
        xmlns="http://www.w3.org/2000/svg" 
        width="24" height="24" 
        viewBox="0 0 24 24" fill="none" 
        stroke="currentColor" stroke-width="2" 
        stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="4"></circle>
        <path d="M12 2v2"></path>
        <path d="M12 20v2"></path>
        <path d="m4.93 4.93 1.41 1.41"></path>
        <path d="m17.66 17.66 1.41 1.41"></path>
        <path d="M2 12h2"></path>
        <path d="M20 12h2"></path>
        <path d="m6.34 17.66-1.41 1.41"></path>
        <path d="m19.07 4.93-1.41 1.41"></path>
      </svg>
      <!-- Moon Icon (shows in light mode) -->
      <svg 
        class="w-5 h-5 transition-all duration-300 transform"
        [ngClass]="!themeService.isDark() ? 'scale-100 rotate-0 opacity-100' : 'scale-0 rotate-90 opacity-0'"
        xmlns="http://www.w3.org/2000/svg" 
        width="24" height="24" 
        viewBox="0 0 24 24" fill="none" 
        stroke="currentColor" stroke-width="2" 
        stroke-linecap="round" stroke-linejoin="round">
        <path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z"></path>
      </svg>
    </button>
  `,
  styles: []
})
export class ThemeToggleComponent {
  themeService = inject(ThemeService);

  toggle() {
    this.themeService.toggleTheme();
  }
}
