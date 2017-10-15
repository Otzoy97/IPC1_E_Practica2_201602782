package lifegame;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;

public class GLife extends JFrame {
    JToggleButton status;
    JSlider jslider;
    JPanel pnl01, pnl02;
    int option;
    boolean ESTADO = false;
    long VELOCIDAD = (long) 60000/50;
    int[][] REF;
    JButton[][] LBL;
    
    public static void main(String[] args) {
        GLife init = new GLife();
        init.show();
    }
    
    private void showMessageDialog(){
        String str01 = JOptionPane.showInputDialog(null, "Ingrese el tamaño del tablero","Game of Life", JOptionPane.INFORMATION_MESSAGE);
        //
        if (str01!=null) {
            try {
                option = Integer.parseInt(str01);
                if (option < 1){
                    option = 1;
                }
            }catch (NumberFormatException e){
                JOptionPane.showMessageDialog(null, "Debe ingresar un valor entero", "Game of Life", JOptionPane.WARNING_MESSAGE);
                GLife init = new GLife();
                init.show();
            }
        } else {
            System.exit(0);
        }
    }
    
    private void gridLabel(){
        for (int y = 0 ; y < option ; y++){
            for (int x = 0 ; x < option ; x++) {
                int xx = x;
                int yy = y;
                LBL[x][y] = new JButton("  ");
                LBL[x][y].setFont(new Font("Courier New", Font.BOLD, 16));
                LBL[x][y].setBackground(Color.white);
                LBL[x][y].setOpaque(true);
                LBL[x][y].setBorder(BorderFactory.createLineBorder(Color.black));
                LBL[x][y].addActionListener( new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e){
                        LBL[xx][yy].setBackground(Color.black);
                        REF[xx][yy] = 1;
                    }
                });
                pnl01.add(LBL[x][y]);
            }
        }
    }
    
    
    private GLife(){
        setTitle("Game of Life");
        Loader obj = new Loader();
        //
        this.setLayout(new GridBagLayout());
        GridBagConstraints bag = new GridBagConstraints();
        this.setResizable(false);
        Container cp = this.getContentPane();
        //
        showMessageDialog();   
        //
        pnl01 = new JPanel(new GridLayout(option,option));
        pnl02 = new JPanel(new GridBagLayout());
        GridBagConstraints bag01 = new GridBagConstraints();
        //
        pnl01.setBorder(BorderFactory.createLineBorder(Color.black));
        //
        REF = new int[(int)Math.pow(option,2)][(int)Math.pow(option,2)];
        LBL = new JButton[(int)Math.pow(option,2)][(int)Math.pow(option,2)];
        //
        gridLabel();
        //
        status = new JToggleButton();
        status.setIcon(new ImageIcon(getClass().getResource("iniciar.png")));
        //
        status.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange()==ItemEvent.SELECTED){
                    status.setIcon(new ImageIcon(getClass().getResource("pausa.png"))); 
                    
                    if (obj.getState()==Thread.State.TIMED_WAITING){
                        obj.resume();
                        ESTADO = true;
                    } else if(obj.getState()==Thread.State.NEW){
                        obj.start();
                        ESTADO = true;
                    }
                } else {
                    obj.suspend();
                    ESTADO = true;
                    status.setIcon(new ImageIcon(getClass().getResource("iniciar.png")));
                }
            }
        });
        jslider = new JSlider();
        jslider.setMajorTickSpacing(10);
        jslider.setMinorTickSpacing(1);
        jslider.setMinimum(1);
        jslider.setMaximum(100);
        jslider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event){
                VELOCIDAD = (long)(10000/jslider.getValue()) ;
            }
        });
        //
        bag01.gridx = 0;
        bag01.gridy = 0;
        bag01.weightx = 1;
        bag01.fill = GridBagConstraints.BOTH;
        bag01.anchor = GridBagConstraints.CENTER;
        pnl02.add(jslider,bag01);
        bag01.gridx = 1;
        bag01.weightx = 0;
        pnl02.add(status,bag01);
        //
        bag.gridx = 0;
        bag.gridy = 0;
        cp.add(pnl01,bag);
        //
        bag.gridy = 1;
        bag.weightx = 1; 
        bag.fill = 1;
        cp.add(pnl02,bag);
        //
        this.pack();
        this.setLocationRelativeTo(null);
        //
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }  
    
    class Loader extends Thread implements Runnable{
        @Override
        public void run(){
            while(ESTADO == true){
                for(int i = 0 ; i < option;i++){
                    for(int j = 0 ; j < option ; j++){
                        Color color = radio(j,i);
                        LBL[j][i].setBackground(color);
                    }
                }
                actualizar();
                executeSLEEP();
            }
        }
        
        private void sleep() throws InterruptedException {
            Loader.sleep(VELOCIDAD);
        }
    
        public void executeSLEEP(){
            try {
                sleep();
            } catch (InterruptedException ex) {
                Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }      
        
        private Color radio(int x, int y){
            int contador = 0;
            int aux1;
            int aux2;
            boolean aux3;
            Color color = new Color(255,255,255);
            for (int i = -1 ; i < 2;i++ ){
                for (int j = -1; j < 2 ; j++){
                    aux1 = x+i;
                    aux2 = y+j;
                    if(i==0){
                        aux3 = (j==0);
                    } else {
                        aux3 = false;
                    }
                    if( ((aux1>-1) && (aux1<option)) && ( (aux2>-1)  && (aux2<option)) && (aux3==false)){
                        if(REF[aux1][aux2]==1){//por columna
                            contador++;   
                        }   
                    }
                }
            }
            if(REF[x][y]==1){
                if(contador < 2){
                    color = new Color(255,255,255);
                }
                if((contador==2)||(contador==3)){
                    color = new Color(0,0,0);
                }
                if(contador > 3){
                    color = new Color(255,255,255);
                }
            } else{
                if(contador==3){
                    color = new Color(0,0,0);
                }
            }
            return color;
        }
    
        private void actualizar(){
            Color color1 = Color.black;
            for (int i = 0 ; i < option ; i++ ){
                for (int j = 0; j < option ; j++){
                    Color color = LBL[j][i].getBackground();
                    if(color.equals(color1)){
                        System.out.println("Negro"+"("+(j)+", "+(i)+")");
                        REF[j][i] = 1;
                    } else {
                        System.out.println("Blanco"+"("+(j)+", "+(i)+")");
                        REF[j][i] = 0;
                    }
                }
            }
        }
    }
}
