package Traktor;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import Traktor.FrameUlepszenia;
import Traktor.FrameZbiory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.Timer;

public class GameFrame extends java.awt.Frame {

    DefaultListModel dlm;
    int zasoby;
    
    
    
    public String getStringPogoda() {
        String pogoda;
        
        switch(MechanizmCzasu.getPogoda()){
            case 1: 
              pogoda = "słońce";
              jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/newpackage/weathericons/dzien.png")));
              break;
            case 2: 
                pogoda = "deszcz";
                jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/newpackage/weathericons/deszcz.png")));
                break;
            case 3: 
                pogoda = "śnieg";
                jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/newpackage/weathericons/snieg.png")));
                break;
            case 4: 
                pogoda = "wiatr/mgła";
                jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/newpackage/weathericons/chmury.png")));
                break;
            case 5: 
                pogoda = "burza";
                jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/newpackage/weathericons/burza.png")));
                
                break;
            default: pogoda = "nie pobrano";
                break;
        }
        return pogoda;
    }
 
    public GameFrame() {        
        initComponents();
       
        init();
    }

    private void init()
    {
        dlm = new DefaultListModel();
        talkList.setModel(dlm);
        gamePanel.SetDLM(dlm);
        
    
    
    }
  
    private void ShowTime(){
     Zegar zegar = new Zegar();
    jPanel1.add(zegar);
    zegar.start();
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        commandText = new javax.swing.JTextField();
        commandButton = new javax.swing.JButton();
        gamePanel = new Traktor.GamePanel();
        talkScrollPanel = new javax.swing.JScrollPane();
        talkList = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jButtonZbiory = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jProgressBar3 = new javax.swing.JProgressBar();
        jProgressBar1 = new javax.swing.JProgressBar();
        jProgressBar2 = new javax.swing.JProgressBar();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        Time = new javax.swing.JPanel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 238, 199));
        jPanel1.setMaximumSize(new java.awt.Dimension(800, 500));
        jPanel1.setMinimumSize(new java.awt.Dimension(800, 500));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 500));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        commandText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                commandText_KeyPressed(evt);
            }
        });
        jPanel1.add(commandText, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 290, 30));

        commandButton.setText("Wyślij");
        commandButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commandButtonActionPerformed(evt);
            }
        });
        jPanel1.add(commandButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 50, 80, 30));

        javax.swing.GroupLayout gamePanelLayout = new javax.swing.GroupLayout(gamePanel);
        gamePanel.setLayout(gamePanelLayout);
        gamePanelLayout.setHorizontalGroup(
            gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
        );
        gamePanelLayout.setVerticalGroup(
            gamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 430, Short.MAX_VALUE)
        );

        jPanel1.add(gamePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 50, 390, 430));

        talkList.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        talkList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        talkScrollPanel.setViewportView(talkList);

        jPanel1.add(talkScrollPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 370, 250));

        jPanel2.setBackground(new java.awt.Color(255, 238, 199));

        jLabel2.setFont(new java.awt.Font("Segoe UI Light", 2, 18)); // NOI18N
        jLabel2.setText("eTraktor - ekran główny");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(176, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 380, -1));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/newpackage/weathericons/noc.png"))); // NOI18N
        jLabel7.setMaximumSize(new java.awt.Dimension(50, 50));
        jLabel7.setMinimumSize(new java.awt.Dimension(50, 50));
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 370, -1, -1));

        jLabel8.setText(MechanizmCzasu.GetPoraDnia());
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 370, -1, -1));

        jLabel9.setText(MechanizmCzasu.GetCzas());
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 390, -1, -1));

        jLabel10.setText(getStringPogoda());
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 420, -1, -1));

        jLabel13.setText(MechanizmCzasu.GetDzienNoc());
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 350, -1, -1));

        jButtonZbiory.setText("Zbiory");
        jButtonZbiory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonZbioryActionPerformed(evt);
            }
        });
        jPanel1.add(jButtonZbiory, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 13, -1, 30));

        jButton1.setText("Sklep z ulepszeniami");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 13, 160, 30));

        jLabel3.setText("Stan konta:");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 20, -1, -1));

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(Zasoby.z.GetStanKontaString());
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 20, 57, -1));

        jPanel3.setBackground(jPanel1.getBackground());

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Zasoby");

        jProgressBar1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jProgressBar1.setMaximumSize(new java.awt.Dimension(20, 14));

        jProgressBar2.setValue(Zasoby.z.GetElektrycznosc());

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Stan paliwa");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Elektryczność");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel11)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jProgressBar3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jProgressBar2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jProgressBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 200, 90));

        javax.swing.GroupLayout TimeLayout = new javax.swing.GroupLayout(Time);
        Time.setLayout(TimeLayout);
        TimeLayout.setHorizontalGroup(
            TimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
        );
        TimeLayout.setVerticalGroup(
            TimeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 50, Short.MAX_VALUE)
        );

        jPanel1.add(Time, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 440, 130, 50));
        Zegar zegar = new Zegar();
        add(zegar);
        zegar.start();

        add(jPanel1, java.awt.BorderLayout.NORTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Exit the Application
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm

    private void commandButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commandButtonActionPerformed
       String cmd = commandText.getText();
        
        addTextToList(cmd);
        gamePanel.Run(cmd);       
        commandText.setText("");
    }//GEN-LAST:event_commandButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        new FrameUlepszenia().setVisible(true);
        zasoby = Zasoby.z.GetStanKonta();
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButtonZbioryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonZbioryActionPerformed
        new FrameZbiory().setVisible(true);
    }//GEN-LAST:event_jButtonZbioryActionPerformed

    private void commandText_KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_commandText_KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            commandButtonActionPerformed(null);
            commandText.grabFocus();
        }
    }//GEN-LAST:event_commandText_KeyPressed

    private void addTextToList(String text)
    {
        dlm.addElement("Operator: " + text);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameFrame().setVisible(true);
                
                
                
            
            }
        });
    }
    
  

   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Time;
    private javax.swing.JButton commandButton;
    private javax.swing.JTextField commandText;
    private Traktor.GamePanel gamePanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonZbiory;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JProgressBar jProgressBar2;
    private javax.swing.JProgressBar jProgressBar3;
    private javax.swing.JList talkList;
    private javax.swing.JScrollPane talkScrollPanel;
    // End of variables declaration//GEN-END:variables
}
