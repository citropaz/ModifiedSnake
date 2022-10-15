package com.zetcode;

import java.util.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Board extends JPanel implements ActionListener {

    private final int B_WIDTH = 300;
    private final int B_HEIGHT = 300;
    private final int DOT_SIZE = 10;
    private final int ALL_DOTS = 900;


    public int DELAY = 60;


    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];
    private int applecount;
    private int dots;
    private int apple_x;
    private int apple_y;
    private int high_score = 0;
    private int tile[][] = new int[(B_HEIGHT)/10][(B_WIDTH)/10];
    private int startpos;
    private int mapcode = 0;

    
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;
    private boolean over = false;
    private boolean newhs = false;
    
    
    private Timer timer;
    private Image ball;
    private Image apple;
    private Image head;
    private Image block;
    private Image ground;

    public Board() {
        
        initBoard();
    }
    
    private void initBoard() {


        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));

        loadImages();
        initGame();
    }

    private void loadImages() {

        ImageIcon iid = new ImageIcon("src/resources/dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("src/resources/myapple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("src/resources/head.png");
        head = iih.getImage();
        
        ImageIcon iib = new ImageIcon("src/resources/block.png");
        block = iib.getImage();
        
        ImageIcon iig = new ImageIcon("src/resources/ground.png");
        ground = iig.getImage();
    }
    
    private void map(int code)
    {
    	File file = new File("src//com//zetcode//tile.txt");
        try
        {
        Scanner read = new Scanner(file);
        
        mapcode = read.nextInt();
        while(mapcode != code)
        {
        	for(int i = 1;i <= 32;i++)
        	{
        		read.nextLine();
        	}
        	mapcode = read.nextInt();
        }
        
        startpos = read.nextInt();
        
        for(int i = 0;i <= (B_HEIGHT - 10)/10;i++)
        	for(int j = 0;j <= (B_WIDTH - 10)/10;j++)
        		tile[i][j] = read.nextInt();
        }
        catch (FileNotFoundException e)
        {
        	
        }
    }

    private void initGame() {
    	
    	newhs = false;
        dots = 3;
        applecount = 0;
        
        map(2);
        
        for(int i=0;i<B_WIDTH;i+=10)
        {
            cantUse.put(i,new HashMap<Integer,Boolean>());
        }
        for(int i=0;i<tile.length;i++)
        {
        	for(int j=0;j<tile[0].length;j++)
        	{
        		if(tile[i][j]==1)
        		{
        			cantUse.get(j*10).put(i*10, true);
        		}
        	}
        }
        for (int z = 0; z < dots; z+=1) {
            x[z] = 30 - z * 10;
            y[z] = startpos;
            cantUse.get(x[z]).put(y[z],true);

        }



        leftDirection = false;
        rightDirection = true;
        upDirection = false;
        downDirection = false;
        
        locateApple();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }
    
    private void doDrawing(Graphics g) {
        
        if (inGame) {

        	for(int i = 0;i <= (B_WIDTH - 10)/10;i++)
            	for(int j = 0;j <= (B_HEIGHT - 10)/10;j++)
            	{
            		if(tile[i][j] == 1)
            		{
            			g.drawImage(block, j*10, i*10, this);
            		}
            		else if(tile[i][j] == 0)
            		{
            			g.drawImage(ground, j*10, i*10, this);
            		}
            	}
        	
            g.drawImage(apple, apple_x, apple_y, this);

            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                	g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }
            
            

            Toolkit.getDefaultToolkit().sync();

        } else {

            gameOver(g);
        }        
    }

    private void gameOver(Graphics g) {
        
        String msg = "Game Over";
        String score = "Current Score: " + (dots-3);
        String restart = "Press \"SPACE\" to restart";
        String hs  = "High Score: " + high_score;
        String nhs = "New High Score!";
        
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);
        
        
        //new
        if(newhs == true)
        {
            g.setColor(Color.pink);
            g.setFont(small);
            g.drawString(nhs, (B_WIDTH - metr.stringWidth(nhs)) / 2, B_HEIGHT / 2 - 50);
        }
        
        
        //over
        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2 - 20);
        
        
        //score
        g.setColor(Color.red);
        g.drawString(score, (B_WIDTH - metr.stringWidth(score)) / 2, B_HEIGHT / 2);
        
        
        //highscore
        g.setColor(Color.yellow);
        g.drawString(hs, (B_WIDTH - metr.stringWidth(hs)) / 2, B_HEIGHT / 2 + 20);
        
        
        //restart
        small = new Font("Helvetica", Font.BOLD, 14);
        g.setFont(small);
        g.setColor(Color.cyan);
        g.drawString(restart, (B_WIDTH - metr.stringWidth(restart)) / 2, B_HEIGHT / 2 + 60);
    }

    private void checkApple() {

        if ((x[0] == apple_x) && (y[0] == apple_y)) {

            dots++;
            applecount++;
            cantUse.get(x[dots-1]).put(y[dots-1],true);
            locateApple();
        }
    }

    private void move() {

        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }
        cantUse.get(x[dots-1]).remove(y[dots-1]);


        if (leftDirection) {
            x[0] -= DOT_SIZE;
            if (x[0] < 0) {
                x[0] = B_WIDTH-10;

            }

        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
            if (x[0] > B_WIDTH-10) {
                x[0] = 0;

            }
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
            if (y[0] < 0 ) {
                y[0] = B_HEIGHT-10;

            }
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
            if (y[0] > B_HEIGHT-10) {
                y[0] = 0;

            }
        }
        cantUse.get(x[0]).put(y[0],true);

    }
    
    private void checkCollision() {

        for (int z = dots; z > 0; z--) {

            if ((z > 3) && (x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }
        
        if(tile[y[0]/10][x[0]/10] == 1) inGame = false;
        
        if (!inGame) {
        	timer.stop();
        	over = true;
        	if(dots - 3 > high_score)
        	{
        		high_score = dots - 3;
        		newhs = true;
        	}
        }
    }
    class pair
    {
        private int a, b;
        public int getA()
        {
            return this.a;
        }
        public int getB()
        {
            return this.b;
        }

        pair(int a,int b)
        {
            this.a=a;
            this.b=b;
        }
    }
    private ArrayList<pair>emptySlot=new ArrayList<pair>();
    private HashMap<Integer,HashMap<Integer,Boolean>>cantUse=new HashMap<Integer,HashMap<Integer,Boolean>>();
    private void locateApple()
    {
        emptySlot.clear();
        // need to reset apple_x and apple_y
        for(int i=0;i<B_WIDTH;i+=DOT_SIZE)
        {
            for(int j=0;j<B_HEIGHT;j+=DOT_SIZE)
            {
                if(cantUse.containsKey(i))
                {
                    if(cantUse.get(i).containsKey(j))
                    {
                        continue;
                    }
                }
                emptySlot.add(new pair(i,j));
            }
        }
        int nextAppleLocation=ThreadLocalRandom.current().nextInt(0,emptySlot.size());
        apple_x=emptySlot.get(nextAppleLocation).getA();
        apple_y=emptySlot.get(nextAppleLocation).getB();




    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkCollision();
            move();
            checkApple();
        }

        repaint();

    }
    private boolean pause=false;
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_SPACE) && over)
            {
            	inGame = true;
            	initGame();
            	over = false;
            }
            
            if (key == KeyEvent.VK_ESCAPE)
            {
                if(!pause) {
                    timer.stop();

                    pause=!pause;
                }
                else
                {
                    timer.start();
                    pause=!pause;
                }
            }
            
            if ((key == KeyEvent.VK_LEFT) && (!rightDirection) && (Math.abs(y[1]-y[0])>0)) 
            {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection) && (Math.abs(y[1]-y[0])>0)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection) && (Math.abs(x[1]-x[0])>0)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ( (key == KeyEvent.VK_DOWN) && (!upDirection) && ((Math.abs(x[1]-x[0])>0))) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }
        }
    }
}
