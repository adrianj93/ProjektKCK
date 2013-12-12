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
/**
 *
 * @author s384027
 */
public class GamePanel extends javax.swing.JPanel {
    DefaultListModel dlm; 
    Interpreter interpreter;
    Board board;
    Agent agent;
    
    int scaleX;
    int scaleY;
    
    public GamePanel()
    {
        initComponents();
        board = new Board(10,10);      
        setObjectsRandom();
        interpreter = new Interpreter("src\\Traktor\\keyword.txt"); 
    }
    
    public void SetDLM(DefaultListModel _dlm)
    {
        dlm = _dlm;
    }
    
    private void setObjectsRandom()
    {
        Random rand = new Random();
        int x,y;
        
        agent = new Agent();
        board.SetObject(board.getWidth()/2, board.getHeight()/2,true, agent);
        
        int countPackage = 5;
        
        ObjectStore os1 = new ObjectStore(5);
        ObjectStore os2 = new ObjectStore(2);
        board.SetObject(0, 0, false, os1);
        board.SetObject(0, board.height-1, false, os2);
        
        while((countPackage--) > 0)
        {
            ObjectPackage op = new ObjectPackage();
            
            do
            {
                x = rand.nextInt(board.width - 1);
                y = rand.nextInt(board.height - 1);
            }while(board.CheckPool(x, y, false));
            
            board.SetObject(x, y, false, op);
        }
        
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
        dlm.addElement("A: " + text);
    }
    
    public void Run(String text) {
        ExecCommand[] excmd = interpreter.PrepareText(text);
        boolean isOccupied = false;

        if (excmd.length == 0) {
            addTextToList(Informations.UnknownCommand);
        }

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
                                                if (pAgent.x > 0) {   
                                                    isOccupied =  checkIfIsOccupied(pAgent.x-1, pAgent.y);
                                                    pAgent.x -= 1;                                                                                                       
                                                }
                                                else
                                                {
                                                    addTextToList(Informations.OutsideEdge);
                                                }
                                                break;
                                            }
                                            case 2: {
                                                if (pAgent.y > 0) {
                                                    isOccupied =  checkIfIsOccupied(pAgent.x, pAgent.y - 1);
                                                    pAgent.y -= 1;
                                                }
                                                else
                                                {
                                                    addTextToList(Informations.OutsideEdge);
                                                }
                                                break;
                                            }
                                            case 3: {
                                                if (pAgent.x < board.width - 1) {
                                                    isOccupied =  checkIfIsOccupied(pAgent.x+1, pAgent.y);
                                                    pAgent.x += 1;
                                                }
                                                else
                                                {
                                                    addTextToList(Informations.OutsideEdge);
                                                }
                                                break;
                                            }
                                            case 4: {
                                                if (pAgent.y < board.height - 1) {
                                                    isOccupied =  checkIfIsOccupied(pAgent.x, pAgent.y + 1);
                                                    pAgent.y += 1;
                                                }
                                                else
                                                {
                                                    addTextToList(Informations.OutsideEdge);
                                                }
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
                                            pAgent.x = excmd[j].GetX();
                                            pAgent.y = excmd[j].GetY();
                                        }
                                        else
                                        {
                                            addTextToList(Informations.OutsideEdge);
                                        }
                                    }
                                    else
                                    {
                                        addTextToList(Informations.OutsideEdge);
                                    }
                                    
                                }
                                
                                if(!isOccupied)
                                {
                                    board.DeleteObject(xOld, yOld, true);
                                    board.SetObject(pAgent.x, pAgent.y, true, agent); 
                                }
                                else
                                {   
                                    addTextToList(Informations.ExistStore);
                                }
                            }
                        }
                        break;
                    }
                    case 2: //podnoszenie
                    {
                        if (board.CheckPool(pAgent.x, pAgent.y,false))
                        {
                            ObjectPackage obj = (ObjectPackage)board.GetObject(pAgent.x, pAgent.y, false);
                            agent.AddPackage(obj);
                            board.DeleteObject(pAgent.x, pAgent.y, false);
                        }
                        break;
                    }
                    case 3: //opuszczanie
                    {                        
                            ObjectPackage obj = agent.GetPackage();
                            board.SetObject(pAgent.x, pAgent.y, false, obj);
                        
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
        scaleX = getWidth() / board.width;
        scaleY = getHeight() / board.height;
        
        for(int i = 0;i < board.getWidth();i++)
            for(int j = 0;j < board.getHeight();j++)
            {
                if(board.CheckPool(i, j, true))
                {
                        g.setColor(Color.BLACK);
                        g.fillRect(i*scaleX, j*scaleY, 30, 30);
                }
                else if(board.CheckPool(i, j, false))
                {
                    PoolObject po = board.GetObject(i, j, false);
                    if(po instanceof ObjectStore)
                    {           
                         g.setColor(Color.RED);
                        g.fillRect(i*scaleX, j*scaleY, 30, 30);
                    }
                    if(po instanceof ObjectPackage)
                    {                     
                         g.setColor(Color.BLUE);
                        g.fillRect(i*scaleX, j*scaleY, 30, 30);
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
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 414, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 302, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
