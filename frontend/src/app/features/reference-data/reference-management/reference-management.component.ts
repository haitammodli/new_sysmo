import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReferenceDataService } from '../../../core/services/reference-data.service';
import { ReferenceData } from '../../../core/models/ReferenceData.model';

@Component({
  selector: 'app-reference-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reference-management.component.html',
  styleUrls: ['./reference-management.component.css']
})
export class ReferenceManagementComponent implements OnInit {
  references: ReferenceData[] = [];
  
  // Matches your Backend logic for Uppercase categories
  categories = ['NATURE', 'TYPE', 'PORT', 'MODE_REGL'];
  selectedCategory = 'NATURE';

  newReference: Partial<ReferenceData> = {
    categorie: 'NATURE',
    libelle: '',
    active: true
  };

  constructor(private referenceDataService: ReferenceDataService) {}

  ngOnInit(): void {
    this.loadReferences();
  }

  loadReferences() {
    this.referenceDataService.getByCategorie(this.selectedCategory).subscribe({
      next: (data) => this.references = data,
      error: (err) => console.error('Error loading references', err)
    });
  }
  loadReferencesA() {
    this.referenceDataService.getActiveByCategorie(this.selectedCategory).subscribe({
      next: (data) => this.references = data,
      error: (err) => console.error('Error loading references', err)
    });
  }

  onCategoryChange(event: Event) {
    const target = event.target as HTMLSelectElement;
    this.selectedCategory = target.value;
    this.newReference.categorie = this.selectedCategory;
    this.loadReferences();
  }

  addReference(form: any) {
    if (this.newReference.libelle && this.newReference.categorie) {
      this.referenceDataService.addReferenceData(this.newReference as ReferenceData).subscribe({
        next: (res) => {
          this.references = [...this.references, res]; 
          this.newReference.libelle = ''; 
          form.resetForm({ categorie: this.selectedCategory });
        },
        error: (err) => console.error('Error adding reference', err)
      });
    }
  }

  toggleStatus(ref: ReferenceData) {
    if (ref.id) {
      this.referenceDataService.toggleStatus(ref.id).subscribe({
        next: (updatedRef) => {
          const index = this.references.findIndex(r => r.id === updatedRef.id);
          if (index !== -1) {
            this.references[index] = updatedRef;
          }
        },
        error: (err) => console.error('Error toggling status', err)
      });
    }
  }
}