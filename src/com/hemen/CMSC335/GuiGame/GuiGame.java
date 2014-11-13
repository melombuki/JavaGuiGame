package com.hemen.CMSC335.GuiGame;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class GuiGame extends JFrame implements KeyListener {
    
    private JLabel player;
    private ArrayList<JLabel> enemies;
    private ArrayList<Thread> enemyMovers;
    private Insets insets;
    private Dimension size;
    private int MAX_X = 370;
    private int MAX_Y = 360;
    
    private boolean isRunning = true;
    
    private JLabel goal;
    
    public GuiGame() {
        // Set up the GUI window
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addKeyListener(this);
        setLayout(null);
        insets = getInsets();
        setTitle("GuiGame");
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(400, 100));
        setMaximumSize(new Dimension(400, 100));
        setSize(new Dimension(400, 100));
        MAX_X = getSize().width - 30;
        MAX_Y = getSize().height - 40;

        // Set the player start location
        player = new JLabel("P");
        size = player.getPreferredSize();
        player.setBounds(insets.left,
                insets.top,
                size.width,
                size.height);
        
        add(player);
        
        // Set the goal/win game object location
        goal = new JLabel("Win!");
        size = goal.getPreferredSize();
        goal.setBounds(MAX_X + insets.left,
                MAX_Y + insets.top,
                size.width,
                size.height);
        
        add(goal);
        
        // Set the initial enemy start locations
        enemies = new ArrayList<JLabel>();
        enemyMovers = new ArrayList<Thread>();
        
        // Create mover threads
        for(int i = 0; i < 5; i ++) {
            final JLabel enemy = new JLabel("E");
            enemies.add(enemy);
            
            enemyMovers.add(new Thread(new Runnable() {
                public void run() {
                    int moveDir = 1; // controls up or down movement
                    int speed = 4;   // move speed of enemy
                    
                    while(isRunning) {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        
                        if(enemy.getBounds().y == 0 || enemy.getBounds().y == MAX_Y) {
                            moveDir *= -1;
                        }
                        
                        // Only move 80% of the time, makes it a little harder
                        if(Math.random() > 0.2) {
                            enemy.setBounds(enemy.getBounds().x,
                                    clamp(enemy.getBounds().y + (speed * moveDir), 0, MAX_Y),
                                    size.width,
                                    size.height);
                            
                            if(enemy.getBounds().intersects(player.getBounds()))
                                playLoose();
                        }
                    }
                }
            }));
        }
        
        // Set enemy locations
        for(int i = 0; i < enemies.size(); i++) {
            size = enemies.get(i).getPreferredSize();
            enemies.get(i).setBounds(50 + insets.left + (65 * i), 
                    insets.top + (15 * i),
                    size.width,
                    size.height);
            
        add(enemies.get(i));
        }
    }
    
    public static void main(String[] args) {
        // Create and run the game
        GuiGame g = new GuiGame();
        
        for(Thread t : g.enemyMovers)
            t.start();
        
        g.setVisible(true);
    }

    public void keyPressed(KeyEvent e) {
        size = player.getPreferredSize();
        
        // Move the player game object
        switch (e.getKeyCode()) {
        case KeyEvent.VK_RIGHT:
            player.setBounds(player.getBounds().x + 5,
                    player.getBounds().y,
                    size.width,
                    size.height);
            break;
        case KeyEvent.VK_LEFT:
            player.setBounds(player.getBounds().x - 5,
                    player.getBounds().y,
                    size.width,
                    size.height);
            break;
        case KeyEvent.VK_UP:
            player.setBounds(player.getBounds().x,
                    player.getBounds().y - 5,
                    size.width,
                    size.height);
            break;
        case KeyEvent.VK_DOWN:
            player.setBounds(player.getBounds().x,
                    player.getBounds().y + 5,
                    size.width,
                    size.height);
            break;
        }
        
        checkCollisions();
    }
    
    // Check to see if the player hit anything
    private void checkCollisions() {
        // Check if player hit the goal
        if(player.getBounds().intersects(goal.getBounds())) {
            playWin();
        }
        
        // Check if the player hit an enemy
        for(JLabel enemy : enemies) {
            if(player.getBounds().intersects(enemy.getBounds())) {
                player.setText(".");
                playLoose();
            }
        }
        
        // Keep the player in the window
        size = player.getPreferredSize();
        player.setBounds(clamp(player.getBounds().x, 0, MAX_X),
                         clamp(player.getBounds().y, 0, MAX_Y),
                         size.width,
                         size.height);     
    }
    
    // Tell the player they win
    private void playWin() {
        isRunning = false;
        JOptionPane.showMessageDialog(new JFrame("1 up"), "You won!!");
        System.exit(0);
    }
    
    // Tell the player they lose
    private void playLoose() {
        isRunning = false;
        JOptionPane.showMessageDialog(new JFrame("Game over"), "You Loose!!");
        System.exit(0);
    }
    
    // This method was found on stackoverflow and can be found at and was written by: 
    //  http://stackoverflow.com/questions/2683442/where-can-i-find-the-clamp-function-in-netwas 
    //  Author: Lee 
    //  Date: 21 April 2010
    public static <T extends Comparable<T>> T clamp(T val, T min, T max) {
        if (val.compareTo(min) < 0) return min;
        else if(val.compareTo(max) > 0) return max;
        else return val;
    }

    // Unused and therefore unimplemented
    public void keyReleased(KeyEvent e) {
    }
    public void keyTyped(KeyEvent e) {   
    }
}
