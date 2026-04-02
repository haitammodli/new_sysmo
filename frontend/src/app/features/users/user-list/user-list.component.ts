import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { UserService } from '../../../core/services/user.service';
import { User } from '../../../core/models/user.model';
import { AgenceService } from '../../../core/services/agence.service';
import { Agence } from '../../../core/models/agence.model';

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
  
  roles: string[] = ['ALL', 'ADMIN', 'CLIENT', 'CHEFAGENCE', 'AGENTMODIFICATION', 'RESPONSABLEMODIFICATION','EXPEDITEUR', 'DESTINATAIRE' , 'RAMASSEUR' , 'DIRECTION'];

  agences: Agence[] = [];

  // --- MODAL STATE (ADD & EDIT) ---
  showAddModal: boolean = false;
  isEditMode: boolean = false;
  editingUserId: number | null = null;
  
  newUser: any = {
    userType: 'ADMIN',
    nom: '',
    prenom: '',
    email: '',
    password: '',
    adresse: '',
    telephone: '',
    secteur: '',
    contact: '',
    typeClient: 'ENTREPRISE',
    agenceIds: [] as number[],
    agences: [] as number[] // Used for User update mapping
  };

  private subscriptions: Subscription = new Subscription();

  constructor(
    public userService: UserService,
    private agenceService: AgenceService
  ) {}

  ngOnInit(): void {
    this.loadAllUsers();
    this.agenceService.getAgences().subscribe(data => this.agences = data);

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

  // ==========================================
  // MODAL & CRUD METHODS (ADD, EDIT, DELETE)
  // ==========================================

  openAddModal(): void {
    this.isEditMode = false;
    this.editingUserId = null;
    this.newUser = {
      userType: 'ADMIN',
      nom: '',
      prenom: '',
      email: '',
      password: '',
      adresse: '',
      telephone: '',
      secteur: '',
      contact: '',
      typeClient: 'ENTREPRISE',
      agenceIds: [],
      agences: []
    };
    this.showAddModal = true;
  }

  onEdit(user: User): void {
    this.isEditMode = true;
    this.editingUserId = user.code || null;
    
    // Map backend roles to frontend dropdown values
    let mappedUserType = user.role;
    if (user.role === 'AGENTMODIFICATION') mappedUserType = 'AGENT_MODIF';
    if (user.role === 'RESPONSABLEMODIFICATION') mappedUserType = 'RESP_MODIF';
    if (user.role === 'CHEFAGENCE') mappedUserType = 'CHEF_AGENCE';

    // Pre-fill the form with the user's current data
    this.newUser = {
      userType: mappedUserType || 'ADMIN',
      nom: user.nom,
      prenom: user.prenom,
      email: user.email,
      password: '', // Leave password blank on edit unless they want to change it
      adresse: (user as any).adresse || '',
      telephone: (user as any).telephone || '',
      secteur: (user as any).secteur || '',
      contact: (user as any).contact || '',
      typeClient: (user as any).typeClient || 'ENTREPRISE',
      // Map existing agences if any (if backend sends array of agences)
      agenceIds: (user as any).agences?.map((a: any) => a.code) || [],
      agences: (user as any).agences?.map((a: any) => a.code) || [] // Keep this for Update payload
    };
    
    this.showAddModal = true;
  }

  onDelete(user: User): void {
    const confirmDelete = confirm(`Voulez-vous vraiment supprimer l'utilisateur ${user.nom} ${user.prenom} ?`);
    
    if (confirmDelete && user.code) {
      this.userService.deleteUser(user.code).subscribe({
        next: () => {
          console.log('Utilisateur supprimé avec succès');
          this.loadAllUsers(); // Auto-refresh the list!
        },
        error: (err) => {
          console.error('Erreur lors de la suppression', err);
          alert('Erreur lors de la suppression. Cet utilisateur est peut-être lié à d\'autres données.');
        }
      });
    }
  }

  closeAddModal(): void {
    this.showAddModal = false;
  }

  submitNewUser(): void {
    // Sync the two properties depending on endpoint requirements:
    // Update API uses "agences", Register API uses "agenceIds"
    if (this.newUser.userType === 'CHEF_AGENCE' || this.newUser.userType === 'CHEFAGENCE') {
        this.newUser.agences = this.newUser.agenceIds;
    }
    
    if (this.isEditMode && this.editingUserId) {
      // UPDATE EXISTING USER
      this.userService.updateUser(this.editingUserId, this.newUser).subscribe({
        next: (response: any) => {
          console.log('Utilisateur modifié avec succès:', response);
          this.closeAddModal();
          this.loadAllUsers();
        },
        error: (err: any) => {
          console.error('Erreur lors de la modification', err);
          alert('Erreur lors de la modification de l\'utilisateur.');
        }
      });
    } else {
      // CREATE NEW USER
      this.userService.createUser(this.newUser).subscribe({
        next: (response: any) => {
          console.log('Utilisateur créé avec succès:', response);
          this.closeAddModal();
          this.loadAllUsers(); 
        },
        error: (err: any) => {
          console.error('Erreur lors de la création de l\'utilisateur', err);
          alert('Erreur: Vérifiez les informations saisies ou si l\'email existe déjà.');
        }
      });
    }
  }

  // ==========================================
  // EXISTING SEARCH & FILTER METHODS
  // ==========================================

  onSearchChange(term: string): void {
    this.searchSubject.next(term);
  }

  loadAllUsers(): void {
    this.userService.searchUsers('').subscribe({
      next: (data: User[]) => {
        this.users = data;
        this.filteredUsers = [...this.users];
        this.applyFilters();
      },
      error: (err: any) => {
        console.error('Failed to load users', err);
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
      this.selectedRole = 'CLIENT'; 
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
    
    if (this.selectedRole !== 'ALL' && !this.showBlacklisted) {
      result = result.filter(u => u.role === this.selectedRole);
    }
    
    this.filteredUsers = result;
  }
}