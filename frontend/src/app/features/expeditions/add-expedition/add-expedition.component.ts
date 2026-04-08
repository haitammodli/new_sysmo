import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ExpeditionService } from '../../../core/services/expedition.service';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject, of } from 'rxjs';
import { UserService } from '../../../core/services/user.service';
import { ReferenceDataService } from '../../../core/services/reference-data.service';
import { AgenceService } from '../../../core/services/agence.service';

@Component({
  selector: 'app-add-expedition',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './add-expedition.component.html',
  styleUrls: ['./add-expedition.component.css']
})
export class AddExpeditionComponent implements OnInit {
  addForm!: FormGroup;
  
  // Autocomplete state
  expediteurResults: any[] = [];
  destinataireResults: any[] = [];
  ramasseurResults: any[] = [];
  agenceResults: any[] = [];
  
  expediteurSearchTerm = new Subject<string>();
  destinataireSearchTerm = new Subject<string>();
  ramasseurSearchTerm = new Subject<string>();
  agenceSearchTerm = new Subject<string>();

  showExpediteurDropdown = false;
  showDestinataireDropdown = false;
  showRamasseurDropdown = false;
  showAgenceDropdown = false;

  // Inline Agence Creation Modal
  showAddAgenceModal = false;
  newAgence: any = { nom: '', ville: '' };

  // Dynamically fetched Reference Data
  natures: any[] = [];
  types: any[] = [];
  ports: any[] = [];
  modes: any[] = [];

  // Chefs d'Agence for inline assignment
  chefsAgence: any[] = [];

  constructor(
    private fb: FormBuilder,
    private expeditionService: ExpeditionService,
    private userService: UserService,
    private referenceDataService: ReferenceDataService,
    private agenceService: AgenceService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadReferenceData();
    this.userService.getUsersByRole('CHEFAGENCE').subscribe(data => this.chefsAgence = data);
    
    this.addForm = this.fb.group({
      // General Info
      numeroExpedition: ['', Validators.required],
      numerodeclaration: [null],
      dateLivraison: [null],
      adresseLivraison: ['', Validators.required],
      agenceId: [null, Validators.required],

      // Relationships (Hidden IDs)
      expiditeurId: [null, Validators.required],
      distinataireId: [null, Validators.required],
      ramasseurId: [null],
      
      // Search Inputs (UI Only)
      expiditeurSearch: [''],
      destinataireSearch: [''],
      ramasseurSearch: [''],
      agenceSearch: [''],

      // Classification
      natureId: [null],
      typeId: [null],
      portId: [null],
      modeReglId: [null],
      catprodId: [null],
      creditId: [null],
      unitId: [null],
      livraisonId: [null],
      taxationId: [null],

      // Taxation Details
      ht: [0, [Validators.required, Validators.min(0)]],
      tva: [20, [Validators.required, Validators.min(0)]],
      ttc: [{value: 0, disabled: true}],
      
      // Dimensions
      colis: [1],
      poid: [1],
      volume: [1],
      valeurDeclaree: [0],
      etiquette: [0],
      encombrement: [0],
      fond: [0],
      ps: [0],
      ref_regl: [null],
      
      // Documents
      bl: [false],
      numerobl: [''],
      facture: [false],
      numerofacture: [''],
      comment: ['']
    });

    this.setupAutocomplete();
    this.setupFinancialCalculations();
  }

  setupAutocomplete() {
    this.expediteurSearchTerm.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => term ? this.userService.searchUsers(term) : of([]))
    ).subscribe((results: any) => {
      this.expediteurResults = results;
      this.showExpediteurDropdown = results.length > 0;
    });

    this.destinataireSearchTerm.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => term ? this.userService.searchUsers(term) : of([]))
    ).subscribe((results: any) => {
      this.destinataireResults = results;
      this.showDestinataireDropdown = results.length > 0;
    });

    this.ramasseurSearchTerm.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => term ? this.userService.searchUsers(term) : of([]))
    ).subscribe((results: any) => {
      this.ramasseurResults = results;
      this.showRamasseurDropdown = results.length > 0;
    });

    this.agenceSearchTerm.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => term ? this.agenceService.searchAgences(term) : of([]))
    ).subscribe((results: any) => {
      this.agenceResults = results;
      this.showAgenceDropdown = results.length > 0;
    });
  }

  setupFinancialCalculations() {
    // Listen to changes in HT and TVA
    this.addForm.get('ht')?.valueChanges.subscribe(() => this.calculateTTC());
    this.addForm.get('tva')?.valueChanges.subscribe(() => this.calculateTTC());
  }

  loadReferenceData() {
    this.referenceDataService.getActiveByCategorie('NATURE').subscribe(data => this.natures = data);
    this.referenceDataService.getActiveByCategorie('TYPE').subscribe(data => this.types = data);
    this.referenceDataService.getActiveByCategorie('PORT').subscribe(data => this.ports = data);
    this.referenceDataService.getActiveByCategorie('MODE_REGL').subscribe(data => this.modes = data);
  }

  calculateTTC() {
    const ht = this.addForm.get('ht')?.value || 0;
    const tva = this.addForm.get('tva')?.value || 0;
    const ttc = ht + (ht * tva / 100);
    this.addForm.patchValue({ ttc: ttc }, { emitEvent: false });
  }

  // Event handlers for search
  onSearch(type: string, event: any) {
    const term = event.target.value;
    if (type === 'expiditeur') this.expediteurSearchTerm.next(term);
    if (type === 'distinataire') this.destinataireSearchTerm.next(term);
    if (type === 'ramasseur') this.ramasseurSearchTerm.next(term);
    if (type === 'agence') this.agenceSearchTerm.next(term);
  }

  selectAutocomplete(type: string, userOrAgence: any) {
    if (type === 'expiditeur') {
      this.addForm.patchValue({ expiditeurId: userOrAgence.code, expiditeurSearch: userOrAgence.nom + ' ' + userOrAgence.prenom });
      this.showExpediteurDropdown = false;
    } else if (type === 'distinataire') {
      this.addForm.patchValue({ distinataireId: userOrAgence.code, destinataireSearch: userOrAgence.nom + ' ' + userOrAgence.prenom });
      this.showDestinataireDropdown = false;
    } else if (type === 'ramasseur') {
      this.addForm.patchValue({ ramasseurId: userOrAgence.code, ramasseurSearch: userOrAgence.nom + ' ' + userOrAgence.prenom });
      this.showRamasseurDropdown = false;
    } else if (type === 'agence') {
      this.addForm.patchValue({ agenceId: userOrAgence.code, agenceSearch: userOrAgence.nom + ' (' + userOrAgence.ville + ')' });
      this.showAgenceDropdown = false;
    }
  }

  // ==========================================
  // INLINE AGENCE CREATION
  // ==========================================
  openAddAgenceModal() {
    this.newAgence = { nom: '', ville: '', matricule: null };
    this.showAddAgenceModal = true;
  }

  closeAddAgenceModal() {
    this.showAddAgenceModal = false;
  }

  submitNewAgence() {
    if (this.newAgence.nom && this.newAgence.ville) {
      this.agenceService.createAgence(this.newAgence).subscribe({
        next: (agence: any) => {
          this.addForm.patchValue({ agenceId: agence.code, agenceSearch: agence.nom + ' (' + agence.ville + ')' });
          this.closeAddAgenceModal();
        },
        error: (err: any) => {
          console.error('Erreur lors de la création de l\'agence inline', err);
          alert('Erreur lors de la création de l\'agence.');
        }
      });
    }
  }

  onSubmit() {
    if (this.addForm.valid) {
      const payload = this.addForm.getRawValue();
      
      // Separate Search UI fields from actual DTO
      delete payload.expiditeurSearch;
      delete payload.destinataireSearch;
      delete payload.ramasseurSearch;
      delete payload.agenceSearch;
      delete payload.ttc; 
      
      this.expeditionService.createExpedition(payload).subscribe({
        next: (res) => {
          this.router.navigate(['/dashboard/expeditions']);
        },
        error: (err) => {
          console.error('Error creating expedition', err);
        }
      });
    } else {
      Object.keys(this.addForm.controls).forEach(key => {
        this.addForm.get(key)?.markAsTouched();
      });
    }
  }
}
