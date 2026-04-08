import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ModificationService } from '../../../core/services/modification.service';

@Component({
  selector: 'app-modification',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './modification.component.html',
   styleUrls: ['./modification.component.css']
})
export class ModificationComponent implements OnInit {
  
  activeTab: 'LIST' | 'NEW' = 'LIST';
  modifications: any[] = [];
  searchCritere: string = '';
  
  // Formulaire de soumission
  modForm!: FormGroup;
  
  // Simulation de l'utilisateur connecté (A remplacer par votre AuthService)
  currentUser = { id: 1, role: 'CHEF_AGENCE' };

  // Les types de modifications (Enum)
  typesModification = [
    'ERREUR_POIDS', 'ERREUR_TAXATION', 'ERREUR_CALCUL_PARAMETRAGE', 
    'REMISE_MONTANT', 'SERVICE_GRATUIT', 'MODIFICATION_VALEUR_DECLARE', 
    'ANNULATION_ENCAISSEMENT', 'ERREUR_DESTINATION_CLIENT', 'MODIFICATION_DEMANDEE_CLIENT'
  ];

  constructor(
    private fb: FormBuilder,
    private modificationService: ModificationService
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadModifications();
  }

  initForm() {
    this.modForm = this.fb.group({
      numeroExpedition: ['', Validators.required],
      typeModification: ['', Validators.required],
      ancienneValeur: [''], // Optionnel selon le type
      nouvelleValeur: ['', Validators.required],
      demandeurId: [this.currentUser.id],
      roleDemandeur: [this.currentUser.role]
    });
  }

  // --- ACTIONS DE LECTURE ---

  loadModifications() {
    this.modificationService.getAllModifications().subscribe({
      next: (data) => this.modifications = data,
      error: (err) => console.error('Erreur de chargement', err)
    });
  }

  onSearch() {
    if (this.searchCritere.trim()) {
      this.modificationService.searchModifications(this.searchCritere).subscribe({
        next: (data) => this.modifications = data,
        error: (err) => alert("Erreur lors de la recherche.")
      });
    } else {
      this.loadModifications();
    }
  }

  resetSearch() {
    this.searchCritere = '';
    this.loadModifications();
  }

  // --- ACTIONS D'ÉCRITURE ---

  onSubmit() {
    if (this.modForm.valid) {
      this.modificationService.soumettreDemande(this.modForm.value).subscribe({
        next: (res) => {
          alert("Demande soumise avec succès !");
          this.modForm.reset({ demandeurId: this.currentUser.id, roleDemandeur: this.currentUser.role });
          this.activeTab = 'LIST';
          this.loadModifications();
        },
        error: (err) => {
          // Affiche l'erreur renvoyée par vos règles métier Spring Boot (ex: Limite de 2, Facturée)
          alert(err.error || "Erreur lors de la soumission de la demande.");
        }
      });
    } else {
      this.modForm.markAllAsTouched();
    }
  }

  traiter(id: number, statut: 'APPROUVEE' | 'REJETEE') {
    if(confirm(`Êtes-vous sûr de vouloir ${statut === 'APPROUVEE' ? 'approuver' : 'rejeter'} cette modification ?`)) {
      this.modificationService.traiterDemande(id, statut, this.currentUser.id).subscribe({
        next: (res) => {
          alert(`Modification ${statut} avec succès.`);
          this.loadModifications(); // Rafraîchir la liste
        },
        error: (err) => {
          alert(err.error || "Erreur lors du traitement.");
        }
      });
    }
  }

  // Utilitaire pour la couleur des badges
  getBadgeClass(statut: string): string {
    switch (statut) {
      case 'EN_ATTENTE': return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'APPROUVEE': return 'bg-green-100 text-green-800 border-green-200';
      case 'REJETEE': return 'bg-red-100 text-red-800 border-red-200';
      default: return 'bg-slate-100 text-slate-800 border-slate-200';
    }
  }
}