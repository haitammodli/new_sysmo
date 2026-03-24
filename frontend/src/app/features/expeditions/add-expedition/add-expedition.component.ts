import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ExpeditionService } from '../../../core/services/expedition.service';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject, of } from 'rxjs';
import { UserService } from '../../../core/services/user.service';
import { ReferenceDataService } from '../../../core/services/reference-data.service';

@Component({
  selector: 'app-add-expedition',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './add-expedition.component.html',
  styleUrls: ['./add-expedition.component.css']
})
export class AddExpeditionComponent implements OnInit {
  addForm!: FormGroup;
  
  // Autocomplete state
  expediteurResults: any[] = [];
  destinataireResults: any[] = [];
  ramasseurResults: any[] = [];
  
  expediteurSearchTerm = new Subject<string>();
  destinataireSearchTerm = new Subject<string>();
  ramasseurSearchTerm = new Subject<string>();

  showExpediteurDropdown = false;
  showDestinataireDropdown = false;
  showRamasseurDropdown = false;

  // Dynamically fetched Reference Data
  natures: any[] = [];
  types: any[] = [];
  ports: any[] = [];
  modes: any[] = [];

  constructor(
    private fb: FormBuilder,
    private expeditionService: ExpeditionService,
    private userService: UserService,
    private referenceDataService: ReferenceDataService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadReferenceData();
    this.addForm = this.fb.group({
      // Relationships (Hidden IDs)
      expiditeurId: [null, Validators.required],
      distinataireId: [null, Validators.required],
      ramasseurId: [null],
      agenceId: [1], // Mock agence
      
      // Search Inputs (UI Only)
      expiditeurSearch: [''],
      destinataireSearch: [''],
      ramasseurSearch: [''],

      // Classification
      natureId: [null],
      typeId: [null],
      portId: [null],
      modeReglId: [null],

      // Taxation Details
      ht: [0, [Validators.required, Validators.min(0)]],
      tva: [20, [Validators.required, Validators.min(0)]],
      ttc: [{value: 0, disabled: true}],
      
      // Dimensions
      colis: [1],
      poid: [1],
      volume: [1],
      valeurDeclaree: [0],
      
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
  }

  setupFinancialCalculations() {
    // Listen to changes in HT and TVA
    this.addForm.get('ht')?.valueChanges.subscribe(() => this.calculateTTC());
    this.addForm.get('tva')?.valueChanges.subscribe(() => this.calculateTTC());
  }

  loadReferenceData() {
    this.referenceDataService.getByCategorie('NATURE').subscribe(data => this.natures = data);
    this.referenceDataService.getByCategorie('TYPE').subscribe(data => this.types = data);
    this.referenceDataService.getByCategorie('PORT').subscribe(data => this.ports = data);
    this.referenceDataService.getByCategorie('MODE_REGL').subscribe(data => this.modes = data);
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
  }

  selectAutocomplete(type: string, user: any) {
    if (type === 'expiditeur') {
      this.addForm.patchValue({ expiditeurId: user.code, expiditeurSearch: user.nom + ' ' + user.prenom });
      this.showExpediteurDropdown = false;
    } else if (type === 'distinataire') {
      this.addForm.patchValue({ distinataireId: user.code, destinataireSearch: user.nom + ' ' + user.prenom });
      this.showDestinataireDropdown = false;
    } else if (type === 'ramasseur') {
      this.addForm.patchValue({ ramasseurId: user.code, ramasseurSearch: user.nom + ' ' + user.prenom });
      this.showRamasseurDropdown = false;
    }
  }

  onSubmit() {
    if (this.addForm.valid) {
      const payload = this.addForm.getRawValue();
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
