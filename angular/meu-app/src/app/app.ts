import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from './components/header/header';
import { Hero } from './components/hero/hero';
import { Cards } from './components/cards/cards';
import { Footer } from './components/footer/footer';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Hero, Cards, Footer],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {}
