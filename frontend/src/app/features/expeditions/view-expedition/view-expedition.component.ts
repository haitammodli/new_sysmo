import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ExpeditionService} from '../../../core/services/expedition.service';
import { Expedition } from '../../../core/models/expedition.model';

@Component({
  selector: 'app-view-expedition',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './view-expedition.component.html',
  styleUrls: ['./view-expedition.component.css']
})
export class ViewExpeditionComponent implements OnInit {
  expedition: Expedition | null = null;
  loading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private expeditionService: ExpeditionService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.expeditionService.getExpeditionById(Number(idParam)).subscribe({
        next: (data) => {
          this.expedition = data;
          this.loading = false;
        },
        error: (err) => {
          console.error(err);
          this.loading = false;
        }
      });
    }
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
