package Traktor;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;


public class MechanizmCzasu {
 

    public MechanizmCzasu() {

    }
    
    public static String GetCzas() {
        Calendar cal = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");  
        String czas = "[" + sdf.format(cal.getTime()) + "]";
        return czas;
    }
    
    public static String GetPoraDnia() {
        String poradnia;
        Calendar cal = Calendar.getInstance(); 
        int s = cal.get(Calendar.HOUR_OF_DAY);
        
        int p;
        p=0;
        int dn;
        
        if (s>=19) {
            p = 5;
            dn = 0;
        }
        else {
            if (s>=13) {
                p = 4;
                dn = 1;
            }
            else {
                if (s>=11) {
                    p = 3;
                    dn = 1;
                }
                else {
                    if (s>=5) {
                        p = 2;
                        dn = 1;
                    }
                    else {
                        if (s>=0) {
                            p = 1;
                            dn = 0;
                        }
                                
                }
            }}
        }
        
        switch(p) {
            case 1: poradnia = "noc"; break;
            case 2: poradnia = "poranek"; break;
            case 3: poradnia = "południe"; break;
            case 4: poradnia = "popołudnie"; break;
            case 5: poradnia = "wieczór"; break;  
                
            default:
                poradnia ="błąd - nie pobrano";
            break;
                
        }
        
        return poradnia;
    }
            
    public static String GetDzienNoc() {
        String dziennoc;
        Calendar cal = Calendar.getInstance(); 
        int s = cal.get(Calendar.HOUR_OF_DAY);
        
        int p;
        int dn;
        
        dn = 0;
        
        if (s>=19) {
            p = 5;
            dn = 0;
        }
        else {
            if (s>=13) {
                p = 4;
                dn = 1;
            }
            else {
                if (s>=11) {
                    p = 3;
                    dn = 1;
                }
                else {
                    if (s>=5) {
                        p = 2;
                        dn = 1;
                    }
                    else {
                        if (s>=0) {
                            p = 1;
                            dn = 0;
                        }
                                
                }
            }}
        }
        
        switch(dn) {
            case 1: dziennoc = "dzien"; break;
            case 0: dziennoc = "noc"; break;
                
            default:
                dziennoc ="błąd - nie pobrano";
            break;
                
        }
        return dziennoc;
    }
    
    public static int getPogoda() {
        Random random = new Random(); // or new Random(someSeed);
        int pogoda = 1 + random.nextInt(5);
        return pogoda;
    }
    
}
