/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Traktor;

public class Informations {
        public static String getStringPogoda() {
        String pogoda;
        
        switch(MechanizmCzasu.getPogoda()){
            case 1: 
              pogoda = "słońce";
              break;
            case 2: 
                pogoda = "deszcz";
                break;
            case 3: 
                pogoda = "śnieg";
                break;
            case 4: 
                pogoda = "wiatr/mgła";
                break;
            case 5: 
                pogoda = "burza";
                
                break;
            default: pogoda = "nie pobrano";
                break;
        }
        return pogoda;
    }
    
    
    public static String FirstInfo() {
        String FirstInfoString;
        FirstInfoString = "Jest godzina " + MechanizmCzasu.GetCzas() + " (" + MechanizmCzasu.GetPoraDnia() + ")";
        
        return FirstInfoString;
    }
    public static String SecondInfo() {
        String SecondInfoString;
        SecondInfoString = "Obecna pogoda: " + getStringPogoda();
        return SecondInfoString;
    }
    public static String OutsideEdge = "Nie możesz wyjść poza krawędz.";
    public static String ExistStore = "Nie możesz przejść na to pole, jest zajęte przez półkę!";
    public static String UnknownCommand = "Nie rozumiem polecenia.";
}
