/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Traktor;

public  class Informations {
    public static String FirstInfo() {
        String FirstInfoString;
        FirstInfoString = "Jest godzina " + MechanizmCzasu.GetCzas() + " (" + MechanizmCzasu.GetPoraDnia() + ")";
        
        return FirstInfoString;
    }
//    public static String SecondInfo() {
//        String SecondInfoString;
//        //SecondInfoString = "Obecna pogoda: " + GameFrame.getStringPogoda();
//        return SecondInfoString;
//    }
    public static String OutsideEdge = "Nie możesz wyjść poza krawędz.";
    public static String ExistStore = "Nie możesz przejść na to pole, jest zajęte przez półkę!";
    public static String UnknownCommand = "Nie rozumiem polecenia.";
}
