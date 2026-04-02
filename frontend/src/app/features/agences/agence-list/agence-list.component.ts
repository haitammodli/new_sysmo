import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { AgenceService } from '../../../core/services/agence.service';
import { Agence } from '../../../core/models/agence.model'; 
import { UserService } from '../../../core/services/user.service'; 

@Component({
  selector: 'app-agence-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './agence-list.component.html',
  styleUrls: ['./agence-list.component.css']
})
export class AgenceListComponent implements OnInit, OnDestroy {
  agences: Agence[] = [];
  filteredAgences: Agence[] = [];
  
  chefs: any[] = []; 
  
  searchTerm: string = '';
  searchSubject: Subject<string> = new Subject<string>();

  
  showAddModal: boolean = false;
  isEditMode: boolean = false;
  editingAgenceId: number | null = null;
  
 
  newAgence: Agence = {
    nom: '',
    ville: '',
    matricule: undefined
  };

  private subscriptions: Subscription = new Subscription();

  constructor(
    public agenceService: AgenceService,
    private userService: UserService 
  ) {}

  ngOnInit(): void {
    this.loadAllAgences();
    this.loadChefs();

    this.subscriptions.add(
      this.searchSubject.pipe(
        debounceTime(300),
        distinctUntilChanged()
      ).subscribe(term => {
        if (term.trim() === '') {
          this.loadAllAgences();
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
  // DATA LOADING METHODS
  // ==========================================

  
  loadChefs(): void {
    this.userService.getUsersByRole('CHEFAGENCE').subscribe({
      next: (users: any[]) => {
        this.chefs = users;
      },
      error: (err: any) => console.error('Erreur lors du chargement des chefs', err)
    });
  }

  loadAllAgences(): void {
    this.agenceService.getAgences().subscribe({
      next: (data: Agence[]) => {
        this.agences = data;
        this.filteredAgences = [...this.agences];
      },
      error: (err: any) => {
        console.error('Failed to load agences', err);
      }
    });
  }

  // ==========================================
  // MODAL & CRUD METHODS (ADD, EDIT, DELETE)
  // ==========================================

  openAddModal(): void {
    this.isEditMode = false;
    this.editingAgenceId = null;
    this.newAgence = { nom: '', ville: '', matricule: undefined };
    this.showAddModal = true;
  }

  onEdit(agence: Agence): void {
    this.isEditMode = true;
    this.editingAgenceId = agence.code || null;
    
    
    this.newAgence = {
      nom: agence.nom,
      ville: agence.ville,
      matricule: agence.chefMatricule || undefined
    };
    
    this.showAddModal = true;
  }

  onDelete(agence: Agence): void {
    const confirmDelete = confirm(`Voulez-vous vraiment supprimer l'agence ${agence.nom} ?`);
    
    if (confirmDelete && agence.code) {
      this.agenceService.deleteAgence(agence.code).subscribe({
        next: () => {
          this.loadAllAgences();
        },
        error: (err) => {
          console.error('Erreur lors de la suppression', err);
          alert('Erreur: Agence potentiellement liée à des expéditions.');
        }
      });
    }
  }

  closeAddModal(): void {
    this.showAddModal = false;
  }

  submitNewAgence(): void {
    if (this.isEditMode && this.editingAgenceId) {
      this.agenceService.updateAgence(this.editingAgenceId, this.newAgence).subscribe({
        next: () => {
          this.closeAddModal();
          this.loadAllAgences();
        },
        error: (err: any) => {
          console.error('Erreur lors de la modification', err);
          alert('Erreur lors de la modification de l\'agence.');
        }
      });
    } else {
      this.agenceService.createAgence(this.newAgence).subscribe({
        next: () => {
          this.closeAddModal();
          this.loadAllAgences(); 
        },
        error: (err: any) => {
          console.error('Erreur lors de la création de l\'agence', err);
          alert('Erreur lors de la création.');
        }
      });
    }
  }

  // ==========================================
  // SEARCH & FILTER METHODS
  // ==========================================

  onSearchChange(term: string): void {
    this.searchSubject.next(term);
  }

  searchFromApi(term: string): void {
    this.agenceService.searchAgences(term).subscribe({
      next: (data: Agence[]) => {
        this.filteredAgences = data;
      },
      error: (err: any) => console.error(err)
    });
  }
}