
package Traktor;

public class Ulepszenia {
    int moc;
    int ladownosc;
    boolean swiatla;
    static Ulepszenia u = new Ulepszenia();

    public Ulepszenia() {
        this.moc = 20;
        this.ladownosc = 30;
        this.swiatla = false;
    }
    
    public void PowerPack1() {
        int cena = 100;
        Zasoby.z.ModyfikujStanKonta(-cena);
        moc = (int) (moc+(moc*0.3));
    }
    
    public void CapacityPack1() {
        int cena = 120;
        Zasoby.z.ModyfikujStanKonta(-cena);
        ladownosc = (int) (ladownosc+(ladownosc*0.4));
    }
    
    public int GetMoc() {
        return moc;
    }
    
    public int GetCapacity() {
        return ladownosc;
    }
    
    public void SwiatlaSwitch(String stan) {
        if ("on".equals(stan))  
            swiatla = true;
           else
          swiatla = false;
    }
    
    public boolean SwiatlaStan() {
        return swiatla;
    }
    
}
