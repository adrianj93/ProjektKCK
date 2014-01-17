/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Traktor;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.HashMap;

public class GamePanel extends javax.swing.JPanel {
    
    public static final int SZEROKOSC = 25;
    public static final int WYSOKOSC = 250;
    DefaultListModel dlm; 
    Interpreter interpreter;
    Board board;
    Agent agent;
    public BufferedImage potworek = null;
    int scaleX;
    int scaleY;
    
    
    
    
    public int pozX,pozY;
    public GamePanel()
    {
        pozX=50;
        pozY=380;
        initComponents();
        board = new Board(11,10);      
        setObjectsRandom();
        interpreter = new Interpreter("src\\Traktor\\keyword.txt"); 
        sprites = new HashMap();
        
    }
    
    public HashMap sprites;
    public void SetDLM(DefaultListModel _dlm)
    {
        dlm = _dlm;
    }
    
    
    //ustawienie elementów w okienku
    private void setObjectsRandom()
    {
        Random rand = new Random();
        int x,y;
        
        agent = new Agent();
        
        //Miejsce startu traktora
        board.SetObject(8, 9,true, agent);
        
        int countPackage = 5;
        //hangary
        ObjectStore os1 = new ObjectStore(5);
        ObjectStore os2 = new ObjectStore(2);
        board.SetObject(board.width-1, board.height-1, false, os1);
        board.SetObject(0, board.height-1, false, os2);
        
        //Losowe rozmieszczone kamienie
        while((countPackage--) > 0)
        {
            ObjectPackage op = new ObjectPackage();
            
            do
            {
                x = rand.nextInt(board.width );
                y = rand.nextInt(board.height-2 );
            }while(board.CheckPool(x, y, false));
            
            board.SetObject(x, y, false, op);
        }
        
    }
    
    //Jak nie ma jakiegos obrazku to podczasz kompilowanie informuje nas o tym
    public BufferedImage loadImage(String sciezka) {
    URL url=null;
    try {
           url = getClass().getClassLoader().getResource(sciezka);
           return ImageIO.read(url);
        } 
    catch (IOException e) 
    {
        System.out.println("Przy otwieraniu " + sciezka +" jako " + url);
        System.out.println("Wystapil blad : "+e.getClass().getName()+""+e.getMessage());
        System.exit(0);
        return null;
    }
}
    
   
public BufferedImage getSprite(String sciezka) {
BufferedImage img = (BufferedImage)sprites.get(sciezka);
if (img == null) {
img = loadImage(sciezka);
sprites.put(sciezka,img);
}
return img;
}    
    
    private Point findAgent()
    {
        Point p = null;
    
        for(int i = 0; i < board.getWidth(); i++)
            for(int j = 0; j < board.getHeight(); j++)
            {
                if(board.CheckPool(i, j, true))
                {
                    PoolObject po = board.GetObject(i, j, true);
                    if(po instanceof Agent)
                    {                        
                        p = new Point(i,j);
                    }
                }
            }
        
        return p;
    }
    
    private void addTextToList(String text)
    {
        dlm.addElement(MechanizmCzasu.GetCzas() + " eTraktor: " + text);
    }
    
    public void Run(String text) {
        ExecCommand[] excmd = interpreter.PrepareText(text);
        boolean isOccupied = false;
        boolean isObstacle = false;
        boolean isPlow = false;
        boolean isSow = false;
        boolean isWater = false;
        boolean isRoute = false;
        

        if (excmd.length == 0) {
            addTextToList(Informations.UnknownCommand);
        }
        
        // blokowanie drogi przed możliwością uprawy
        RouteObject op = new RouteObject();
        board.SetObject(2, 9, false, op);
        board.SetObject(3, 9, false, op);
        board.SetObject(4, 9, false, op);
        board.SetObject(5, 9, false, op);
        board.SetObject(6, 9, false, op);
        board.SetObject(7, 9, false, op);
        board.SetObject(8, 9, false, op);
        
        for (int i = 0; i < excmd.length; i++) {
            if (!excmd[i].arg) {
                
                Point pAgent = findAgent();
                int no = excmd[i].cmd.GetNo();

                switch (no) {
                    case 1: //poruszanie się
                    { 
                        
                        for (int j = 0; j < excmd.length; j++) {
                            if (excmd[j].arg) {
                                int xOld = pAgent.x;
                                int yOld = pAgent.y;                              
                               
                                if (!excmd[j].argCus) {
                                    int mainNo = excmd[j].cmd.GetNo();

                                    if (mainNo == no) {
                                        int argNo = excmd[j].cmd.GetNoArg();
                                        
                                        switch (argNo) {
                                            case 1: {
                                                //p.agent to ten czarny kwadracik. Usunąłem go, ale umownie on cały czas jest
                                                //i w jego miejscu posawiłem traktor, dzieki temu działa usuwanie
                                                if (pAgent.x > 0) {   
                                                    isOccupied =  checkIfIsOccupied(pAgent.x-1, pAgent.y);
                                                    isObstacle =  checkIfIsObstacle(pAgent.x-1, pAgent.y);
                                                    pAgent.x -= 1;
                                                    addTextToList( "Dojechałem. Co dalejr?");
                                                    
                                                }
                                                else
                                                {
                                                    addTextToList(Informations.OutsideEdge);
                                                    addTextToList( "Co dalej?");
                                                }
                                                break;
                                            }
                                            case 2: {
                                                if (pAgent.y > 0) {
                                                    isOccupied =  checkIfIsOccupied(pAgent.x, pAgent.y - 1);
                                                    isObstacle =  checkIfIsObstacle(pAgent.x, pAgent.y - 1);
                                                    pAgent.y -= 1;
                                                    addTextToList( "Dojechałem. Co dalej3?");
                                                }
                                                else
                                                {
                                                    addTextToList( Informations.OutsideEdge);
                                                    addTextToList( "Co dalej?");
                                                }
                                                break;
                                            }
                                            case 3: {
                                                if (pAgent.x < board.width - 1) {
                                                    isOccupied =  checkIfIsOccupied(pAgent.x+1, pAgent.y);
                                                    isObstacle =  checkIfIsObstacle(pAgent.x+1, pAgent.y);
                                                    pAgent.x += 1;
                                                    addTextToList( "Dojechałem. Co dalej?");
                                                }
                                                else
                                                {
                                                    addTextToList(Informations.OutsideEdge);
                                                    addTextToList( "Co dalej?");
                                                }
                                                break;
                                            }
                                            case 4: {
                                                if (pAgent.y < board.height - 1) {
                                                    isOccupied =  checkIfIsOccupied(pAgent.x, pAgent.y + 1);
                                                    isObstacle =  checkIfIsObstacle(pAgent.x, pAgent.y + 1);
                                                    pAgent.y += 1;
                                                    addTextToList( "Dojechałem. Co dalej?");
                                                }
                                                else
                                                {
                                                    addTextToList(Informations.OutsideEdge);
                                                    addTextToList( "Co dalej?");
                                                }
                                                break;
                                            }
                                            
                                            
                                            case 5:{
                                                //kierowanie pomocnikiem
                                                 pozX=pAgent.x*40;
                                                 pozY=pAgent.y*40;
                                                 addTextToList( "Pomoc na miejscu. Co dalej?");
                                               
                                                break;
                                                }
                                            
                                                   
                                               
                                
                                        }
                                       
                                    }
                                } else {    
                                    int tmpx = excmd[j].GetX();
                                    int tmpy = excmd[j].GetY();
                                    
                                    if(tmpx >= 0 && tmpx < board.getWidth())
                                    {
                                        if(tmpy >= 0 && tmpy < board.getHeight())
                                        {
                                            isOccupied = checkIfIsOccupied(tmpx, tmpy);
                                            isObstacle = checkIfIsObstacle(tmpx, tmpy);
                                            pAgent.x = excmd[j].GetX();
                                            pAgent.y = excmd[j].GetY();
                                        }
                                        else
                                        {
                                            addTextToList(Informations.OutsideEdge);
                                            addTextToList( "Co dalej?");
                                        }
                                    }
                                    else
                                    {
                                        addTextToList( Informations.OutsideEdge);
                                        addTextToList( "Co dalej?");
                                    }
                                    
                                }
                                
                                if(!isOccupied && !isObstacle)
                                {
                                    board.DeleteObject(xOld, yOld, true);
                                    board.SetObject(pAgent.x, pAgent.y, true, agent); 
                                }
                                else
                                {   
                                    if(isOccupied){
                                    addTextToList(Informations.ExistStore);
                                    }else{
                                    addTextToList(Informations.Obstacle);
                                    }
                                }
                                
                                
                                
                  
                                
                            }
                        }
                        break;
                    }
                    
                    
                   
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    case 2: //oranie
                    {
                        
                        for (int j = 0; j < excmd.length; j++) {
                          
                                if (!excmd[j].argCus) {
                                    int mainNo = excmd[j].cmd.GetNo();

                                    if (mainNo == no) {
                                        int argNo = excmd[j].cmd.GetNoArg();
                                        
                                        switch (argNo) {
                                            case 1: {
                        
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                                            
                                                                        break;
                                                                        }
                                            
                        case 2: {
                        
                      
                                            
                          for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                                           
                                                                        break;
                                                                        }
                        case 3: {
                        
                      
                        for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                                           
                                                                        break;
                                                                        }
                        case 4: {
                        
                      
                        for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                                           
                                           
                                                                        break;
                                                                        }
                        case 5: {
                        
                      
                        for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                                           
                                           
                                                                        break;
                                                                        }
                        case 6: {
                        
                      
                        for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                                           
                                           
                                                                        break;
                                                                        }
                        case 7: {
                        
                      
                       
                        
                          for(int a = 0; a<5;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                                            
                                                                      
                                            
                          for(int a = 6; a<11;a++){
                            for(int b= 0;b < 3;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                                           
                        
                      
                        for(int a = 0; a<5;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                                           
                         
                        for(int a = 6; a<11;a++){
                            for(int b= 3;b < 6;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                                           
                                           
                        
                        for(int a = 0; a<5;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                                           
                                           
                      
                        for(int a = 6; a<11;a++){
                            for(int b= 6;b < 9;b++){
                            isObstacle =  checkIfIsObstacle(a, b);
                            if(isObstacle)
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Na polu są przeszkody! USUŃ JE");
                           
                        }
                        else{
                      
                        PlowObject op1 = new PlowObject();
                        board.SetObject(a, b, false, op1);
                        }
                            }
                        }
                                           
                                                                        break;
                                                                        }
                                        }}}}
                                                                      break;
                                                                        }
                    
                    case 10:
                    {
                        if (board.CheckPool(pAgent.x-1, pAgent.y,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x-1, pAgent.y, false);
                           addTextToList( "Usunięto przeszkodę");
                        }
                        else if (board.CheckPool(pAgent.x+1, pAgent.y,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x+1, pAgent.y, false);
                           
                        }
                        else if (board.CheckPool(pAgent.x, pAgent.y+1,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x, pAgent.y+1, false);
                           
                        }
                        else if (board.CheckPool(pAgent.x, pAgent.y-1,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x, pAgent.y-1, false);
                           
                        }
                        else if (board.CheckPool(pAgent.x+1, pAgent.y+1,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x+1, pAgent.y+1, false);
                           
                        }
                        else if (board.CheckPool(pAgent.x-1, pAgent.y+1,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x-1, pAgent.y+1, false);
                           
                        }
                        else if (board.CheckPool(pAgent.x+1, pAgent.y-1,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x+1, pAgent.y-1, false);
                           
                        }
                        else if (board.CheckPool(pAgent.x-1, pAgent.y-1,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x-1, pAgent.y-1, false);
                        }
                        else{
                        addTextToList( "Brak przeszkody w pobliżu");
                        }
                  
                        
                        
                        
                    break;    
                    }
                    
                    
                    
                    
                    
                    
                    
                    case 3: //sianie
                    {    
                        
                        isRoute = checkIfIsRoute(pAgent.x, pAgent.y);
                        if(isRoute)
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Nie można uprawiac na drodze");
                        }
                        else{
                         isPlow =  checkIfIsPlow(pAgent.x, pAgent.y);
                         if(isPlow)
                         {
                             if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                            SowObject op1 = new SowObject();
                            board.SetObject(0, 0, false, op1);
                        board.SetObject(0, 1, false, op1);
                        board.SetObject(0, 2, false, op1);
                        board.SetObject(1, 0, false, op1);
                        board.SetObject(1, 1, false, op1);
                        board.SetObject(1, 2, false, op1);
                        board.SetObject(2, 0, false, op1);
                        board.SetObject(2, 1, false, op1);
                        board.SetObject(2, 2, false, op1);
                        board.SetObject(3, 0, false, op1);
                        board.SetObject(3, 1, false, op1);
                        board.SetObject(3, 2, false, op1);
                        board.SetObject(4, 0, false, op1);
                        board.SetObject(4, 1, false, op1);
                        board.SetObject(4, 2, false, op1);
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            SowObject op1 = new SowObject();
                           board.SetObject(6, 0, false, op1);
                        board.SetObject(6, 1, false, op1);
                        board.SetObject(6, 2, false, op1);
                        board.SetObject(7, 0, false, op1);
                        board.SetObject(7, 1, false, op1);
                        board.SetObject(7, 2, false, op1);
                        board.SetObject(8, 0, false, op1);
                        board.SetObject(8, 1, false, op1);
                        board.SetObject(8, 2, false, op1);
                        board.SetObject(9, 0, false, op1);
                        board.SetObject(9, 1, false, op1);
                        board.SetObject(9, 2, false, op1);
                        board.SetObject(10, 0, false, op1);
                        board.SetObject(10, 1, false, op1);
                        board.SetObject(10, 2, false, op1);
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            SowObject op1 = new SowObject();
                            board.SetObject(0, 3, false, op1);
                        board.SetObject(0, 4, false, op1);
                        board.SetObject(0, 5, false, op1);
                        board.SetObject(1, 3, false, op1);
                        board.SetObject(1, 4, false, op1);
                        board.SetObject(1, 5, false, op1);
                        board.SetObject(2, 3, false, op1);
                        board.SetObject(2, 4, false, op1);
                        board.SetObject(2, 5, false, op1);
                        board.SetObject(3, 3, false, op1);
                        board.SetObject(3, 4, false, op1);
                        board.SetObject(3, 5, false, op1);
                        board.SetObject(4, 3, false, op1);
                        board.SetObject(4, 4, false, op1);
                        board.SetObject(4, 5, false, op1);
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            SowObject op1 = new SowObject();
                            board.SetObject(6, 3, false, op1);
                        board.SetObject(6, 4, false, op1);
                        board.SetObject(6, 5, false, op1);
                        board.SetObject(7, 3, false, op1);
                        board.SetObject(7, 4, false, op1);
                        board.SetObject(7, 5, false, op1);
                        board.SetObject(8, 3, false, op1);
                        board.SetObject(8, 4, false, op1);
                        board.SetObject(8, 5, false, op1);
                        board.SetObject(9, 3, false, op1);
                        board.SetObject(9, 4, false, op1);
                        board.SetObject(9, 5, false, op1);
                        board.SetObject(10, 3, false, op1);
                        board.SetObject(10, 4, false, op1);
                        board.SetObject(10, 5, false, op1);
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            SowObject op1 = new SowObject();
                           board.SetObject(0, 6, false, op1);
                        board.SetObject(0, 7, false, op1);
                        board.SetObject(0, 8, false, op1);
                        board.SetObject(1, 6, false, op1);
                        board.SetObject(1, 7, false, op1);
                        board.SetObject(1, 8, false, op1);
                        board.SetObject(2, 6, false, op1);
                        board.SetObject(2, 7, false, op1);
                        board.SetObject(2, 8, false, op1);
                        board.SetObject(3, 6, false, op1);
                        board.SetObject(3, 7, false, op1);
                        board.SetObject(3, 8, false, op1);
                        board.SetObject(4, 6, false, op1);
                        board.SetObject(4, 7, false, op1);
                        board.SetObject(4, 8, false, op1);
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                            SowObject op1 = new SowObject();
                            board.SetObject(6, 6, false, op1);
                        board.SetObject(6, 7, false, op1);
                        board.SetObject(6, 8, false, op1);
                        board.SetObject(7, 6, false, op1);
                        board.SetObject(7, 7, false, op1);
                        board.SetObject(7, 8, false, op1);
                        board.SetObject(8, 6, false, op1);
                        board.SetObject(8, 7, false, op1);
                        board.SetObject(8, 8, false, op1);
                        board.SetObject(9, 6, false, op1);
                        board.SetObject(9, 7, false, op1);
                        board.SetObject(9, 8, false, op1);
                        board.SetObject(10, 6, false, op1);
                        board.SetObject(10, 7, false, op1);
                        board.SetObject(10, 8, false, op1);
                         }
                         }
                         else
                         {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Pole musi zostać najpierw zaorane");      
                         }}
                         break;
                    }
                    
                    case 4: //zbieranie
                    {   
                        isRoute = checkIfIsRoute(pAgent.x, pAgent.y);
                        if(isRoute)
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Nie można uprawiac na drodze");
                        }
                        else{
                        isWater = checkIfIsWater(pAgent.x, pAgent.y);
                        if(isWater)
                        {
                            CollectingObject op1 = new CollectingObject();
                            if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                            
                            board.SetObject(0, 0, false, op1);
                        board.SetObject(0, 1, false, op1);
                        board.SetObject(0, 2, false, op1);
                        board.SetObject(1, 0, false, op1);
                        board.SetObject(1, 1, false, op1);
                        board.SetObject(1, 2, false, op1);
                        board.SetObject(2, 0, false, op1);
                        board.SetObject(2, 1, false, op1);
                        board.SetObject(2, 2, false, op1);
                        board.SetObject(3, 0, false, op1);
                        board.SetObject(3, 1, false, op1);
                        board.SetObject(3, 2, false, op1);
                        board.SetObject(4, 0, false, op1);
                        board.SetObject(4, 1, false, op1);
                        board.SetObject(4, 2, false, op1);
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                           board.SetObject(6, 0, false, op1);
                        board.SetObject(6, 1, false, op1);
                        board.SetObject(6, 2, false, op1);
                        board.SetObject(7, 0, false, op1);
                        board.SetObject(7, 1, false, op1);
                        board.SetObject(7, 2, false, op1);
                        board.SetObject(8, 0, false, op1);
                        board.SetObject(8, 1, false, op1);
                        board.SetObject(8, 2, false, op1);
                        board.SetObject(9, 0, false, op1);
                        board.SetObject(9, 1, false, op1);
                        board.SetObject(9, 2, false, op1);
                        board.SetObject(10, 0, false, op1);
                        board.SetObject(10, 1, false, op1);
                        board.SetObject(10, 2, false, op1);
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            board.SetObject(0, 3, false, op1);
                        board.SetObject(0, 4, false, op1);
                        board.SetObject(0, 5, false, op1);
                        board.SetObject(1, 3, false, op1);
                        board.SetObject(1, 4, false, op1);
                        board.SetObject(1, 5, false, op1);
                        board.SetObject(2, 3, false, op1);
                        board.SetObject(2, 4, false, op1);
                        board.SetObject(2, 5, false, op1);
                        board.SetObject(3, 3, false, op1);
                        board.SetObject(3, 4, false, op1);
                        board.SetObject(3, 5, false, op1);
                        board.SetObject(4, 3, false, op1);
                        board.SetObject(4, 4, false, op1);
                        board.SetObject(4, 5, false, op1);
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                            board.SetObject(6, 3, false, op1);
                        board.SetObject(6, 4, false, op1);
                        board.SetObject(6, 5, false, op1);
                        board.SetObject(7, 3, false, op1);
                        board.SetObject(7, 4, false, op1);
                        board.SetObject(7, 5, false, op1);
                        board.SetObject(8, 3, false, op1);
                        board.SetObject(8, 4, false, op1);
                        board.SetObject(8, 5, false, op1);
                        board.SetObject(9, 3, false, op1);
                        board.SetObject(9, 4, false, op1);
                        board.SetObject(9, 5, false, op1);
                        board.SetObject(10, 3, false, op1);
                        board.SetObject(10, 4, false, op1);
                        board.SetObject(10, 5, false, op1);
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                           board.SetObject(0, 6, false, op1);
                        board.SetObject(0, 7, false, op1);
                        board.SetObject(0, 8, false, op1);
                        board.SetObject(1, 6, false, op1);
                        board.SetObject(1, 7, false, op1);
                        board.SetObject(1, 8, false, op1);
                        board.SetObject(2, 6, false, op1);
                        board.SetObject(2, 7, false, op1);
                        board.SetObject(2, 8, false, op1);
                        board.SetObject(3, 6, false, op1);
                        board.SetObject(3, 7, false, op1);
                        board.SetObject(3, 8, false, op1);
                        board.SetObject(4, 6, false, op1);
                        board.SetObject(4, 7, false, op1);
                        board.SetObject(4, 8, false, op1);
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                           
                            board.SetObject(6, 6, false, op1);
                        board.SetObject(6, 7, false, op1);
                        board.SetObject(6, 8, false, op1);
                        board.SetObject(7, 6, false, op1);
                        board.SetObject(7, 7, false, op1);
                        board.SetObject(7, 8, false, op1);
                        board.SetObject(8, 6, false, op1);
                        board.SetObject(8, 7, false, op1);
                        board.SetObject(8, 8, false, op1);
                        board.SetObject(9, 6, false, op1);
                        board.SetObject(9, 7, false, op1);
                        board.SetObject(9, 8, false, op1);
                        board.SetObject(10, 6, false, op1);
                        board.SetObject(10, 7, false, op1);
                        board.SetObject(10, 8, false, op1);
                         }
                        }
                        else
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Pole musi zostać najpierw podlane");
                        }}
                        break;
                    }
                    case 5: //podlewanie
                    {   
                        isRoute = checkIfIsRoute(pAgent.x, pAgent.y);
                        if(isRoute)
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Nie można uprawiac na drodze");
                        }
                        else{
                        isSow = checkIfIsSow(pAgent.x, pAgent.y);
                        if(isSow)
                        {
                            WaterObject op1 = new WaterObject();
                             if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=0 && pAgent.y<=2){
                            
                            board.SetObject(0, 0, false, op1);
                        board.SetObject(0, 1, false, op1);
                        board.SetObject(0, 2, false, op1);
                        board.SetObject(1, 0, false, op1);
                        board.SetObject(1, 1, false, op1);
                        board.SetObject(1, 2, false, op1);
                        board.SetObject(2, 0, false, op1);
                        board.SetObject(2, 1, false, op1);
                        board.SetObject(2, 2, false, op1);
                        board.SetObject(3, 0, false, op1);
                        board.SetObject(3, 1, false, op1);
                        board.SetObject(3, 2, false, op1);
                        board.SetObject(4, 0, false, op1);
                        board.SetObject(4, 1, false, op1);
                        board.SetObject(4, 2, false, op1);
                         }
                             else if((pAgent.x>=6 && pAgent.x<=10) && (pAgent.y>=0 && pAgent.y<=2)){
                            
                           board.SetObject(6, 0, false, op1);
                        board.SetObject(6, 1, false, op1);
                        board.SetObject(6, 2, false, op1);
                        board.SetObject(7, 0, false, op1);
                        board.SetObject(7, 1, false, op1);
                        board.SetObject(7, 2, false, op1);
                        board.SetObject(8, 0, false, op1);
                        board.SetObject(8, 1, false, op1);
                        board.SetObject(8, 2, false, op1);
                        board.SetObject(9, 0, false, op1);
                        board.SetObject(9, 1, false, op1);
                        board.SetObject(9, 2, false, op1);
                        board.SetObject(10, 0, false, op1);
                        board.SetObject(10, 1, false, op1);
                        board.SetObject(10, 2, false, op1);
                         }
                        else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=3 && pAgent.y<=5){
                            
                            board.SetObject(0, 3, false, op1);
                        board.SetObject(0, 4, false, op1);
                        board.SetObject(0, 5, false, op1);
                        board.SetObject(1, 3, false, op1);
                        board.SetObject(1, 4, false, op1);
                        board.SetObject(1, 5, false, op1);
                        board.SetObject(2, 3, false, op1);
                        board.SetObject(2, 4, false, op1);
                        board.SetObject(2, 5, false, op1);
                        board.SetObject(3, 3, false, op1);
                        board.SetObject(3, 4, false, op1);
                        board.SetObject(3, 5, false, op1);
                        board.SetObject(4, 3, false, op1);
                        board.SetObject(4, 4, false, op1);
                        board.SetObject(4, 5, false, op1);
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=3 && pAgent.y<=5){
                            
                            board.SetObject(6, 3, false, op1);
                        board.SetObject(6, 4, false, op1);
                        board.SetObject(6, 5, false, op1);
                        board.SetObject(7, 3, false, op1);
                        board.SetObject(7, 4, false, op1);
                        board.SetObject(7, 5, false, op1);
                        board.SetObject(8, 3, false, op1);
                        board.SetObject(8, 4, false, op1);
                        board.SetObject(8, 5, false, op1);
                        board.SetObject(9, 3, false, op1);
                        board.SetObject(9, 4, false, op1);
                        board.SetObject(9, 5, false, op1);
                        board.SetObject(10, 3, false, op1);
                        board.SetObject(10, 4, false, op1);
                        board.SetObject(10, 5, false, op1);
                         }
                         else if(pAgent.x>=0 && pAgent.x<=4 && pAgent.y>=6 && pAgent.y<=8){
                            
                           board.SetObject(0, 6, false, op1);
                        board.SetObject(0, 7, false, op1);
                        board.SetObject(0, 8, false, op1);
                        board.SetObject(1, 6, false, op1);
                        board.SetObject(1, 7, false, op1);
                        board.SetObject(1, 8, false, op1);
                        board.SetObject(2, 6, false, op1);
                        board.SetObject(2, 7, false, op1);
                        board.SetObject(2, 8, false, op1);
                        board.SetObject(3, 6, false, op1);
                        board.SetObject(3, 7, false, op1);
                        board.SetObject(3, 8, false, op1);
                        board.SetObject(4, 6, false, op1);
                        board.SetObject(4, 7, false, op1);
                        board.SetObject(4, 8, false, op1);
                         }
                         else if(pAgent.x>=6 && pAgent.x<=10 && pAgent.y>=6 && pAgent.y<=8){
                           
                            board.SetObject(6, 6, false, op1);
                        board.SetObject(6, 7, false, op1);
                        board.SetObject(6, 8, false, op1);
                        board.SetObject(7, 6, false, op1);
                        board.SetObject(7, 7, false, op1);
                        board.SetObject(7, 8, false, op1);
                        board.SetObject(8, 6, false, op1);
                        board.SetObject(8, 7, false, op1);
                        board.SetObject(8, 8, false, op1);
                        board.SetObject(9, 6, false, op1);
                        board.SetObject(9, 7, false, op1);
                        board.SetObject(9, 8, false, op1);
                        board.SetObject(10, 6, false, op1);
                        board.SetObject(10, 7, false, op1);
                        board.SetObject(10, 8, false, op1);
                         }
                        }
                        else
                        {
                            dlm.addElement( MechanizmCzasu.GetCzas() + " eTraktor: " + "Pole musi zostać najpierw zasiane");
                        }}
                        break;
                    }
                     
                    case 6: //sprzedawanie
                    {                        
                            for (int j = 0; j < excmd.length; j++) {
                            if (excmd[j].arg) {
                              
                                if (!excmd[j].argCus) {
                                    int mainNo = excmd[j].cmd.GetNo();

                                    if (mainNo == no) {
                                        int argNo = excmd[j].cmd.GetNoArg();
                                        
                                        switch (argNo) {
                                            case 1: {
                                                addTextToList( "Ile kilogramów?");
                                                break;
                                            }
                                            case 2: {
                                                
                                                break;
                                            }
                                            case 3: {
                                                
                                                break;
                                            }
                                            case 4: {
                                                
                                                break;
                                            }
                                        }
                                       
                                    }
                                } 
                                }}
                            break;
                    }
                    case 7:
                    {
                            //kierowanie pomocnikiem
                            pozX=50;
                            pozY=380;
                            addTextToList( "Pomoc wróciła. Co dalej?");                   
                        break;
                    }
                    case 8: // gdzie jesteś
                    {
                        dlm.addElement(MechanizmCzasu.GetCzas() + " eTraktor: " + "Jestem na polu" + pAgent.x + " " + pAgent.y);    
                        dlm.addElement("Czy wykonać jakieś zadanie?");
                    }
                        
                    case 11:
                    {
                        if (Ulepszenia.u.SwiatlaStan() == true) { dlm.addElement(MechanizmCzasu.GetCzas() + " eTraktor: " + "Swiatla były już włączone");} else
                        { Ulepszenia.u.SwiatlaSwitch("on"); dlm.addElement(MechanizmCzasu.GetCzas() + " eTraktor: " + "Wlaczylem swiatla. Rozpoczęto pobór enrgii elektrycznej"); }
                        break;
                    }
                    
                    case 12:
                    {
                        if (Ulepszenia.u.SwiatlaStan() == false) { dlm.addElement(MechanizmCzasu.GetCzas() + " eTraktor: " + "Swiatla były już wyłączone");} else
                        { Ulepszenia.u.SwiatlaSwitch("off"); dlm.addElement(MechanizmCzasu.GetCzas() + " eTraktor: " + "Wyłączyłem światła"); }
                        break;
                    }
                        
                    case 13:
                    {
                        dlm.addElement(MechanizmCzasu.GetCzas() + " eTraktor: " + "Możesz skorzystać z rozmaitych komend: ");
                        dlm.addElement("    - oraj [numer pola] / wszystko");
                        dlm.addElement("    - wlacz/wylacz swiatla");
                        dlm.addElement("    - zbierz/posiej/podlej");
                        dlm.addElement("    - gdzie jestes");
                        dlm.addElement("    - przesun/pojedz");
                        dlm.addElement("    - i inne...");
                        
                        break;
                    }
                        
                    case 14:
                    {
                        if (Ulepszenia.u.SwiatlaStan() == false) { dlm.addElement(MechanizmCzasu.GetCzas() + " eTraktor: " + "Swiatla są wyłączone");} else
                        dlm.addElement(MechanizmCzasu.GetCzas() + " eTraktor: " + "Swiatla są włączone"); 
                        break;
                        
                    }
                        
                    case 15:
                    {
                        dlm.addElement(MechanizmCzasu.GetCzas() + " eTraktor: " + "Aktualny stan elektryczności - " + Zasoby.z.GetElektrycznosc() + "%"); 
                        break;
                        
                    }
                }
            }
        }
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
    }
    
    private void drawBoard(Graphics g)
    {
        
        g.drawImage(getSprite("backgorund.jpg"), 0  , 0 ,this);
        
        scaleX = getWidth() / board.width;
        scaleY = getHeight() / board.height;
        
        for(int i = 0;i < board.getWidth();i++)
            for(int j = 0;j < board.getHeight();j++)
            {
                //tutaj laduje sie obrazek traktora, ktory jest wykrywany jako ten p.agent i pomicnik,
                //ale on akurat mogl chyba sie ladowac w kazdym miejscu etody
                if(board.CheckPool(i, j, true))
                {
                    
                    g.drawImage(getSprite("tlo21.png"), i*scaleX, j*scaleY,this);    
                        
                    g.drawImage(getSprite("track.png"), i*scaleX-2, j*scaleY-2,this);
                    
                    g.drawImage(getSprite("techn.png"), pozX, pozY,this);
                        
                }
                else if(board.CheckPool(i, j, false))
                {
                    PoolObject po = board.GetObject(i, j, false);
                    if(po instanceof ObjectStore)
                    {     
                        //hangar
                        g.setColor(Color.GREEN);
                        g.fillRect(i*scaleX, j*scaleY, 40, 40);
                         g.drawImage(getSprite("han.jpg"), i*scaleX, j*scaleY,this);

                    }
                    if(po instanceof ObjectPackage)
                    {     
                        //kamienie
                        g.drawImage(getSprite("tlo21.png"), i*scaleX, j*scaleY,this);   
                        g.drawImage(getSprite("kamien.png"), i*scaleX+2, j*scaleY+2,this);
                         
                    }
                    if(po instanceof PlowObject)
                    {
                        //oranie
                        g.drawImage(getSprite("zaorane.jpg"),i*scaleX, j*scaleY,this);
                    }
                    if(po instanceof SowObject)
                    {
                        //zasianie
                        g.drawImage(getSprite("zasiane.jpg"),i*scaleX, j*scaleY,this);
                    }
                    if(po instanceof WaterObject)
                    {
                        //podlane
                        g.drawImage(getSprite("podlane.jpg"),i*scaleX, j*scaleY,this);
                    }
                    if(po instanceof CollectingObject)
                    {
                        //pole po zbiorze
                        g.drawImage(getSprite("pole.jpg"),i*scaleX, j*scaleY,this);
                    }
                }
            }
    }
    
    
    private boolean checkIfIsOccupied (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
            if(po instanceof ObjectStore)
            {
                return true;
            }
            
        }
        return false;
    }
     private boolean checkIfIsObstacle (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof ObjectPackage)
            {
                return true;
            }
        }
        return false;
    }
      
     private boolean checkIfIsPlow (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof PlowObject)
            {
                return true;
            }
        }
        return false;
    }
       
      private boolean checkIfIsSow (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof SowObject)
            {
                return true;
            }
        }
        return false;
    }
    
       private boolean checkIfIsWater (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof WaterObject)
            {
                return true;
            }
        }
        return false;
    }
       
       private boolean checkIfIsRoute (int x, int y)
    {
        if(board.CheckPool(x, y, false))
        {
            PoolObject po = board.GetObject(x, y, false);
             if(po instanceof RouteObject)
            {
                return true;
            }
        }
        return false;
    }
    
    
    


    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 302, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
