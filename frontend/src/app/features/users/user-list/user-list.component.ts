import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { UserService } from '../../../core/services/user.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit, OnDestroy {
  users: User[] = [];
  filteredUsers: User[] = [];
  
  searchTerm: string = '';
  searchSubject: Subject<string> = new Subject<string>();
  
  selectedRole: string = 'ALL';
  showBlacklisted: boolean = false;
  
  roles: string[] = ['ALL', 'ADMIN', 'CLIENT', 'CHEFAGENCE', 'AGENTMODIFICATION', 'RESPONSABLEMODIFICATION'];

  private subscriptions: Subscription = new Subscription();

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadAllUsers();

    // Setup search with debounce
    this.subscriptions.add(
      this.searchSubject.pipe(
        debounceTime(300),
        distinctUntilChanged()
      ).subscribe(term => {
        if (term.trim() === '') {
          this.applyFilters();
        } else {
          this.searchFromApi(term);
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  onSearchChange(term: string): void {
    this.searchSubject.next(term);
  }

  loadAllUsers(): void {
    // There isn't an explicit getAllUsers. 
    // Usually searching for '' doesn't work well on backends if not handled.
    // So we assume the typical fetching will be by role or search.
    // For now, let's try getting all users via an empty search if the backend supports it, 
    // or just load admins or clients initially.
    this.userService.searchUsers('').subscribe({
      next: (data: User[]) => {
        this.users = data;
        this.filteredUsers = [...this.users];
        this.applyFilters();
      },
      error: (err: any) => {
        console.error('Failed to load users', err);
        // Fallback or handle error
      }
    });
  }

  searchFromApi(term: string): void {
    this.userService.searchUsers(term).subscribe({
      next: (data: User[]) => {
        this.users = data;
        this.applyFilters();
      },
      error: (err: any) => console.error(err)
    });
  }

  setRoleFilter(role: string): void {
    this.selectedRole = role;
    if (role === 'ALL') {
      this.loadAllUsers();
    } else {
      this.userService.getUsersByRole(role).subscribe({
        next: (data: User[]) => {
          this.users = data;
          this.applyFilters();
        },
        error: (err: any) => console.error(err)
      });
    }
  }

  toggleBlacklisted(): void {
    this.showBlacklisted = !this.showBlacklisted;
    if (this.showBlacklisted) {
      this.selectedRole = 'CLIENT'; // Blacklisted makes sense mostly for clients
      this.userService.getBlacklistedClients().subscribe({
         next: (data: User[]) => {
           this.users = data;
           this.filteredUsers = data;
         },
         error: (err: any) => console.error(err)
      });
    } else {
      this.setRoleFilter(this.selectedRole);
    }
  }

  applyFilters(): void {
    let result = this.users;
    
    // The role filter is done mostly on the backend now, but re-filtering frontend just in case of empty search that returned all
    if (this.selectedRole !== 'ALL' && !this.showBlacklisted) {
      result = result.filter(u => u.role === this.selectedRole);
    }
    
    // We already handle showBlacklisted with backend call specifically
    // So we assign the result
    this.filteredUsers = result;
  }
}
