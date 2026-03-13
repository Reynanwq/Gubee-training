import { Component } from '@angular/core';
import { NgFor } from '@angular/common';

@Component({
  selector: 'app-cards',
  imports: [NgFor],
  templateUrl: './cards.html',
  styleUrl: './cards.css',
})
export class Cards {
  items = [
    { titulo: 'Componentes', descricao: 'Blocos reutilizáveis de UI.' },
    { titulo: 'Rotas', descricao: 'Navegação entre páginas.' },
    { titulo: 'Serviços', descricao: 'Lógica compartilhada entre componentes.' },
  ];
}
