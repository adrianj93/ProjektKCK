
package Traktor;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.text.*;

/**
 * Prosty zegar w Swingu
 * @author kodatnik.blogspot.com
 */

// nowa klasa Zegar zbudowana w oparciu o klasę JLabel
class Zegar extends JLabel implements Runnable {
 // wątek
 private Thread watek;
 // liczba milisekund pauzy (1000 ms czyli 1 sekunda)
 private int pauza = 1000;

 // konstruktor klasy
 public Zegar() {
  // wyrównamy napisy do środka
 
  // wybieramy font do wyświetlenia zagara (podajemy nazwę, styl oraz rozmiar)
  setFont (new Font ("Consolas", Font.BOLD, 28));
  // ustalamy kolor tła
  //setBackground(Color.BLUE);
  // ustalamy kolor tekstu
  setForeground(Color.BLACK);
  // ustawiamy przeźroczystość
  setOpaque(true);
 }

 // metoda start tworzy i uruchamia wątek zegara
 public void start() {
  // jeśli nie ma działającego wątka, utwórz i uruchom nowy
  if (watek == null) {
   watek = new Thread(this);
   watek.start();
  }
 }

 // metoda wywołana po starcie wątku
 public void run() {
  // dopóki zmienna watek wskazuje na bieżący wątek
  while ( watek == Thread.currentThread()) {
   // nowy obiekt klasy Date
   Date time = new Date();
   // formatowanie
   DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);
   // ustawiamy tekst
   setText(df.format(time));
   try {
    // wstrzymujemy działanie wątku na 1 sekundę
    watek.sleep(pauza);
   } catch (InterruptedException e) {}
  }
 }

 // metoda zatrzymująca zegar (wątek)
 public void stop() {
  // ustawiamy referencję watek na null
  watek = null;
 }
}
