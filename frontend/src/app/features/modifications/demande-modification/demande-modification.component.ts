import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ModificationService } from '../../../core/services/modification.service';
import { TypeModification } from '../../../core/models/modification.model';

@Component({
  selector: 'app-demande-modification',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './demande-modification.component.html',
  styleUrls: ['./demande-modification.component.css']
})
export class DemandeModificationComponent implements OnInit {
  modifForm!: FormGroup;
  isSubmitting: boolean = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  // The complete list of SDTM modification types
  typesModification: TypeModification[] = [
    'ERREUR_TAXATION',
    'ERREUR_POIDS',
    'ERREUR_CALCUL_PARAMETRAGE',
    'ERREUR_DESTINATION_CLIENT',
    'MODIFICATION_DEMANDEE_CLIENT',
    'MODIFICATION_VALEUR_DECLARE',
    'ANNULATION_ENCAISSEMENT',
    'REMISE_MONTANT',
    'SERVICE_GRATUIT'
  ];

  // Roles allowed to request a modification
  roles: string[] = ['CLIENT', 'AGENT', 'CHEFAGENCE', 'DIRECTION'];

  constructor(
    private fb: FormBuilder,
    private modificationService: ModificationService
  ) {}

  ngOnInit(): void {
    // Absolutely NO hardcoded defaults. Everything starts empty.
    this.modifForm = this.fb.group({
      numeroExpedition: ['', Validators.required],
      typeModification: ['', Validators.required],
      ancienneValeur: ['', Validators.required],
      nouvelleValeur: ['', Validators.required],
      demandeurId: ['', [Validators.required, Validators.min(1)]],
      roleDemandeur: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.modifForm.invalid) {
      this.modifForm.markAllAsTouched(); 
      return;
    }

    this.isSubmitting = true;
    this.successMessage = null;
    this.errorMessage = null;

    this.modificationService.soumettreDemande(this.modifForm.value).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        this.successMessage = `Demande soumise avec succès ! (N° ${response.numeroExpedition})`;
        this.modifForm.reset();
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = "Erreur lors de la soumission de la demande. Veuillez vérifier la connexion au serveur.";
        console.error('Erreur API:', err);
      }
    });
  }
}