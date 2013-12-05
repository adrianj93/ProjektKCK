package etraktor;
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
        
        switch(s) {
            case 0: poradnia = "rano"; break;
            case 7: poradnia = "rano"; break;
            case 8: poradnia = "rano"; break;
            case 9: poradnia = "niedługo po kck"; break;
            case 20: poradnia = "wieczór"; break;
            case 21: poradnia = "wieczór po 21"; break;    
                
            default:
                poradnia ="jakaś";
            break;
                
        }
        return poradnia;
    }
    
    public static int getPogoda() {
        Random random = new Random(); // or new Random(someSeed);
        int pogoda = 1 + random.nextInt(5);
        return pogoda;
    }
    
}
