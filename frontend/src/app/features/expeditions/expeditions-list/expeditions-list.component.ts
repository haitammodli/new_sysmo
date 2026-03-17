import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ExpeditionService, Expedition } from '../services/expedition.service';

@Component({
  selector: 'app-expeditions-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './expeditions-list.component.html',
  styleUrls: ['./expeditions-list.component.css']
})
export class ExpeditionsListComponent implements OnInit {
  expeditions: Expedition[] = [];

  constructor(private expeditionService: ExpeditionService) {}

  ngOnInit() {
    this.expeditionService.getAllExpeditions().subscribe({
      next: (data) => this.expeditions = data,
      error: (err) => console.error(err)
    });
  }

  getBadgeClass(statut: string | undefined): string {
    switch(statut) {
      case 'EN_COURS': return 'bg-yellow-100 text-yellow-800 ring-yellow-600/20';
      case 'LIVRE': return 'bg-green-100 text-green-800 ring-green-600/20';
      case 'ANNULE': return 'bg-red-100 text-red-800 ring-red-600/20';
      default: return 'bg-gray-100 text-gray-800 ring-gray-600/20';
    }
  }
}
