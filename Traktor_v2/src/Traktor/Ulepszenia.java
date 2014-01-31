
package Traktor;

public class Ulepszenia {
    int moc;
    int ladownosc;
    int ppcena;
    int pplvl;
    boolean swiatla;
    static Ulepszenia u = new Ulepszenia();
    

    public Ulepszenia() {
        this.moc = 20;
        this.ladownosc = 30;
        this.swiatla = false;
        ppcena = 250;
        pplvl = 1;
    }
    
    public int getPPCena(){
        return ppcena;
    }
    
    public void ppcena(){
        ppcena = ppcena + 400;
    }
    public int getLvlPP(){
        return pplvl;
    }
    public String getLvlPPstring(){
        pplvl = getLvlPP();
        String poziom;
        if (pplvl < 5){
        poziom = Integer.toString(pplvl);
        } else {
        poziom = "Maksymalny";
        }
        return poziom;
    }
    public String getPPCenaString(){
      ppcena = getPPCena();
      String cena = Integer.toString(ppcena)+"€";
      return cena;
    }
    public void lvlPP(){
        pplvl = pplvl + 1;
    }
    
    
    
    public void CapacityPack1() {
        
        Zasoby.z.ModyfikujStanKonta(-getPPCena());
        ladownosc = (int) (ladownosc+(ladownosc*(0.2*getLvlPP())));
    }
    
    public void PowerPack1() {
        
        Zasoby.z.ModyfikujStanKonta(-100);
        moc = (int) (moc+(moc*0.3));
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
