import { Component } from '@angular/core';
import { Hero } from '../../components/hero/hero';
import { Cards } from '../../components/cards/cards';

@Component({
  selector: 'app-home',
  imports: [Hero, Cards],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {}
