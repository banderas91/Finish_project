import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainClass extends JFrame {
    Scheme scheme;
    ImagePanel panel;
    JScrollPane jscpane;
    JSplitPane scppane;
    JButton plus, minus, offAll;
    JLabel fire;
    JCheckBox left2Ch, right2Ch;
    JCheckBox[] checkBoxes;
    int width, height;
    int backgroundScale = 800;
    int backgroundScaleMax = 1000;
    int backgroundScaleMin = 600;
    double x, y, x1, y1;
    int[][] sensors, arrows;
    String[] roomNames;
    boolean [] isFire;

    public MainClass() {
        fillSensorCoords();
        fillArrowsCoords();
        fillRoomsNames();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        width = (int)(screen.getWidth());
        height = (int)(screen.getHeight());

        scheme = new Scheme(width, height);
        scheme.setLayout(null);
        scheme.setBackground(Color.WHITE);
        scheme.setMinimumSize(new Dimension(200, height - 300));
        plus = new JButton("Увеличить");
        plus.setBounds(50, 20, 100, 30);
        minus = new JButton("Уменьшить");
        minus.setBounds(50, 60, 100, 30);
        plus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backgroundScale += 200;
                setBackground(backgroundScale, true);
                if (backgroundScale == backgroundScaleMax) {
                    plus.setEnabled(false);
                }
                minus.setEnabled(true);
            }
        });
        minus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backgroundScale -= 200;
                setBackground(backgroundScale, true);
                if (backgroundScale == backgroundScaleMin) {
                    minus.setEnabled(false);
                }
                plus.setEnabled(true);
            }
        });
        minus.setEnabled(true);
        Font font = new Font("Tahoma", Font.PLAIN, 10);
        fire = new JLabel("Датчики:");
        fire.setBounds(10, 100, 150, 20);
        left2Ch = new JCheckBox("на 2ом этаже в левом крыле");
        left2Ch.setFont(font);
        left2Ch.setBounds(10, 120, 180, 20);
        left2Ch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkBoxes[5].doClick();
            }
        });
        right2Ch = new JCheckBox("на 2ом этаже в правом крыле");
        right2Ch.setFont(font);
        right2Ch.setBounds(10, 140, 180, 20);
        right2Ch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkBoxes[40].doClick();
            }
        });
        checkBoxes = new JCheckBox[56];
        int k = 1;
        int s = 0;
        int h = 18;
        for(int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i] = new JCheckBox("№ " + (i + 1));
            checkBoxes[i].setFont(font);
            if(k == 1) {
                checkBoxes[i].setBounds(10, 160 + s * h, 60, h);
                k++;
            } else {
                if(k == 2) {
                    checkBoxes[i].setBounds(70, 160 + s * h, 60, h);
                    k++;
                }
                 else {
                    if(k == 3) {
                        checkBoxes[i].setBounds(130, 160 + s * h, 60, h);
                        k = 1;
                        s++;
                    }
                }
            }
            checkBoxes[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //offAllSensorsExcept(Integer.parseInt(e.getActionCommand().split(" ")[1]));
                    setBackground(backgroundScale, true);
                }
            });
            scheme.add(checkBoxes[i]);
        }
        offAll = new JButton("Выключить все");
        offAll.setBounds(40, 510, 120, 30);
        offAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                offAllSensors();
            }
        });
        scheme.add(offAll);
        scheme.add(right2Ch);
        scheme.add(left2Ch);
        scheme.add(fire);
        scheme.add(plus);
        scheme.add(minus);

        jscpane = new JScrollPane(panel);
        MouseAdapter scroll1 = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                PointerInfo m = MouseInfo.getPointerInfo();
                setCursor (Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                x = m.getLocation().getX();
                y = m.getLocation().getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        };
        MouseMotionAdapter scroll2 = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                PointerInfo m  = MouseInfo.getPointerInfo();
                x1 = m.getLocation().getX();
                y1 = m.getLocation().getY();
                int v = jscpane.getVerticalScrollBar().getValue();
                int h = jscpane.getHorizontalScrollBar().getValue();
                jscpane.getVerticalScrollBar().setValue(v + (int)(y - y1));
                jscpane.getHorizontalScrollBar().setValue(h + (int)(x - x1));
                x = x1;
                y = y1;
            }
        };
        MouseAdapter lightSensor = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() + jscpane.getHorizontalScrollBar().getValue();
                int y = e.getY() + jscpane.getVerticalScrollBar().getValue();
                double scale = (double) backgroundScale / 800;
                for(int i = 1; i < sensors.length; i++) {
                    if(Math.abs(sensors[i][0] * scale + sensors[0][0] / 2 * scale - x) < sensors[0][0] * 2 * scale) {
                        if(Math.abs(sensors[i][1] * scale + sensors[0][1] / 2 * scale - y) < sensors[0][1] * 2 * scale) {
                            checkBoxes[i - 1].doClick();
                            break;
                        }
                    }
                }
            }
        };
        jscpane.addMouseListener(lightSensor);
        jscpane.addMouseListener(scroll1);
        jscpane.addMouseMotionListener(scroll2);
        jscpane.setBackground(Color.WHITE);
        jscpane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                jscpane.repaint();
            }
        });
        jscpane.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                jscpane.repaint();
            }
        });
        setBackground(backgroundScale, false);

        scppane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scheme, jscpane);
        scppane.setEnabled(false);

        this.add(scppane);
        this.pack();
        this.setBackground(Color.RED);
        this.setBounds((width - (width - 300)) / 2, (height - (height - 100)) / 2, width - 300, height - 300);
    }

    public static void main(String []args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch (Exception e) {
            System.out.println("LookAndFeel Exception: " + e.toString());
        }
        MainClass mainFrame = new MainClass();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    public void setBackground(int height, boolean b) {
        int x = jscpane.getHorizontalScrollBar().getValue();
        int y = jscpane.getVerticalScrollBar().getValue();
        if(b) {
            jscpane.remove(panel);
        }
        double scale = (double) height / 800;
        isFire = new boolean[57];
        for(int i = 0; i < checkBoxes.length; i++) {
            isFire[i] = checkBoxes[i].isSelected();
        }
        panel = new ImagePanel(new ImageIcon("imgs/h" + height + ".png").getImage(), scale, sensors, roomNames, arrows, isFire);
        jscpane.setViewportView(panel);
        jscpane.revalidate();
        jscpane.repaint();
        jscpane.getHorizontalScrollBar().setValue(x);
        jscpane.getVerticalScrollBar().setValue(y);
    }

    public void fillSensorCoords() {
        sensors = new int[57][];
        sensors[0] = new int[]{16, 16};
        sensors[1] = new int[]{799, 468};
        sensors[2] = new int[]{885, 468};
        sensors[3] = new int[]{885, 347};
        sensors[4] = new int[]{885, 257};
        sensors[5] = new int[]{748, 257};
        sensors[6] = new int[]{676, 336};
        sensors[7] = new int[]{680, 280};
        sensors[8] = new int[]{623, 257};
        sensors[9] = new int[]{519, 257};
        sensors[10] = new int[]{549, 316};
        sensors[11] = new int[]{498, 337};
        sensors[12] = new int[]{495, 422};
        sensors[13] = new int[]{495, 488};
        sensors[14] = new int[]{400, 217};
        sensors[15] = new int[]{400, 118};
        sensors[16] = new int[]{519, 107};
        sensors[17] = new int[]{519, 174};
        sensors[18] = new int[]{623, 174};
        sensors[19] = new int[]{623, 107};
        sensors[20] = new int[]{748, 159};
        sensors[21] = new int[]{856, 125};
        sensors[22] = new int[]{909, 180};
        sensors[23] = new int[]{1020, 125};
        sensors[24] = new int[]{1073, 180};
        sensors[25] = new int[]{1045, 257};
        sensors[26] = new int[]{1197, 257};
        sensors[27] = new int[]{1177, 125};
        sensors[28] = new int[]{1230, 179};
        sensors[29] = new int[]{1275, 280};
        sensors[30] = new int[]{1351, 276};
        sensors[31] = new int[]{1351, 147};
        sensors[32] = new int[]{1441, 147};
        sensors[33] = new int[]{1577, 116};
        sensors[34] = new int[]{1577, 183};
        sensors[35] = new int[]{1647, 203};
        sensors[36] = new int[]{1685, 172};
        sensors[37] = new int[]{1853, 123};
        sensors[38] = new int[]{1971, 123};
        sensors[39] = new int[]{1971, 229};
        sensors[40] = new int[]{1853, 229};
        sensors[41] = new int[]{1733, 339};
        sensors[42] = new int[]{1743, 281};
        sensors[43] = new int[]{1633, 257};
        sensors[44] = new int[]{1505, 275};
        sensors[45] = new int[]{1505, 397};
        sensors[46] = new int[]{1597, 469};
        sensors[47] = new int[]{1505, 469};
        sensors[48] = new int[]{1358, 469};
        sensors[49] = new int[]{1358, 397};
        sensors[50] = new int[]{1206, 347};
        sensors[51] = new int[]{1111, 347};
        sensors[52] = new int[]{1111, 469};
        sensors[53] = new int[]{983, 489};
        sensors[54] = new int[]{981, 420};
        sensors[55] = new int[]{1007, 357};
        sensors[56] = new int[]{951, 322};
    }

    public void fillArrowsCoords() {
        arrows = new int[29][];
        arrows[1] = new int[]{454, 365, 454, 340};
        arrows[2] = new int[]{450, 430, 450, 405};
        arrows[3] = new int[]{545, 505, 545, 480};
        arrows[4] = new int[]{545, 445, 545, 420};
        arrows[5] = new int[]{445, 270, 470, 270};
        arrows[6] = new int[]{540, 310, 565, 310};
        arrows[7] = new int[]{610, 215, 635, 215};
        arrows[8] = new int[]{1960, 315, 1935, 315};
        arrows[9] = new int[]{2130, 270, 2105, 270};
        arrows[10] = new int[]{1725, 100, 1725, 125};
        arrows[11] = new int[]{865, 220, 840, 220};
        arrows[12] = new int[]{1025, 220, 1000, 220};
        arrows[13] = new int[]{1010, 320, 985, 320};
        arrows[14] = new int[]{1245, 220, 1270, 220};
        arrows[15] = new int[]{1390, 220, 1415, 220};
        arrows[16] = new int[]{1375, 365, 1400, 365};
        arrows[17] = new int[]{1465, 365, 1490, 365};
        arrows[18] = new int[]{1555, 225, 1580, 225};
        arrows[19] = new int[]{1635, 225, 1660, 225};//LAST
        arrows[20] = new int[]{840, 220, 865, 220};
        arrows[21] = new int[]{1000, 220, 1025, 220};
        arrows[22] = new int[]{985, 320, 1010, 320};
        arrows[23] = new int[]{1270, 220, 1245, 220};
        arrows[24] = new int[]{1415, 220, 1390, 220};
        arrows[25] = new int[]{1400, 365, 1375, 365};
        arrows[26] = new int[]{1490, 365, 1465, 365};
        arrows[27] = new int[]{1580, 225, 1555, 225};
        arrows[28] = new int[]{1660, 225, 1635, 225};
    }

    public void fillRoomsNames() {
        roomNames = new String[57];
        roomNames[1] = "Датчик №1 сработал в бухгалтерии";
        roomNames[2] = "Датчик №2 сработал в бухгалтерии";
        roomNames[3] = "Датчик №3 сработал в бухгалтерии";
        roomNames[4] = "Датчик №4 сработал в коридоре";
        roomNames[5] = "Датчик №5 сработал в коридоре";
        roomNames[6] = "Датчик №6 сработал на лестнице в левом крыле";
        roomNames[7] = "Датчик №7 сработал в коридоре";
        roomNames[8] = "Датчик №8 сработал в коридоре";
        roomNames[9] = "Датчик №9 сработал в коридоре";
        roomNames[10] = "Датчик №10 сработал в санузле";
        roomNames[11] = "Датчик №11 сработал в коридоре";
        roomNames[12] = "Датчик №12 сработал в коридоре";
        roomNames[13] = "Датчик №13 сработал в коридоре";
        roomNames[25] = "Датчик №25 сработал в коридоре";
        roomNames[26] = "Датчик №26 сработал в коридоре";
        roomNames[29] = "Датчик №29 сработал в коридоре";
        roomNames[30] = "Датчик №30 сработал в коридоре";
        roomNames[44] = "Датчик №44 сработал в коридоре";
        roomNames[43] = "Датчик №43 сработал в коридоре";
        roomNames[42] = "Датчик №42 сработал в коридоре";
        roomNames[40] = "Датчик №40 сработал в коридоре";
        roomNames[39] = "Датчик №39 сработал в коридоре";
        roomNames[38] = "Датчик №38 сработал в коридоре";
        roomNames[37] = "Датчик №37 сработал в коридоре";
        roomNames[41] = "Датчик №41 сработал на лестнице в правом крыле";
        roomNames[14] = "Датчик №14 сработал в кабинете отдела учета";
        roomNames[15] = "Датчик №15 сработал в кабинете отдела учета";
        roomNames[18] = "Датчик №18 сработал в кабинете главного бухгалтера";
        roomNames[19] = "Датчик №19 сработал в кабинете главного бухгалтера";
        roomNames[20] = "Датчик №20 сработал на ресепшн";
        roomNames[21] = "Датчик №21 сработал в кабинете руководителя управления перспективных разработок";
        roomNames[22] = "Датчик №22 сработал в кабинете руководителя управления перспективных разработок";
        roomNames[23] = "Датчик №23 сработал в кабинете инженера эксплуатации здания";
        roomNames[24] = "Датчик №24 сработал в кабинете инженера эксплуатации здания";
        roomNames[27] = "Датчик №27 сработал в кабинете руководителя лаборатории";
        roomNames[28] = "Датчик №28 сработал в кабинете администратора лаборатории";
        roomNames[31] = "Датчик №31 сработал в лаборатории";
        roomNames[32] = "Датчик №32 сработал в лаборатории";
        roomNames[33] = "Датчик №33 сработал в помещении администратора";
        roomNames[34] = "Датчик №34 сработал в помещении администратора";
        roomNames[35] = "Датчик №35 сработал в серверной";
        roomNames[36] = "Датчик №36 сработал в серверной";
        roomNames[45] = "Датчик №45 сработал в кабинете инженеров";
        roomNames[46] = "Датчик №46 сработал в кабинете инженеров";
        roomNames[47] = "Датчик №47 сработал в кабинете инженеров";
        roomNames[48] = "Датчик №48 сработал в кабинете кадровика";
        roomNames[49] = "Датчик №49 сработал в кабинете кадровика";
        roomNames[50] = "Датчик №50 сработал в кабинете директора";
        roomNames[51] = "Датчик №51 сработал в кабинете директора";
        roomNames[52] = "Датчик №52 сработал в кабинете директора";
        roomNames[53] = "Датчик №53 сработал в зам. директора по общим вопросам";
        roomNames[54] = "Датчик №54 сработал в зам. директора по общим вопросам";
        roomNames[55] = "Датчик №55 сработал в приемной";
        roomNames[56] = "Датчик №56 сработал в приемной";
        roomNames[16] = "Датчик №16 сработал в архиве";
        roomNames[17] = "Датчик №17 сработал в архиве ";
    }

    public void offAllSensors() {
        for(int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i].setSelected(false);
        }
        right2Ch.setSelected(false);
        left2Ch.setSelected(false);
        setBackground(backgroundScale, true);
    }

    public void offAllSensorsExcept(int n) {
        for(int i = 0; i < checkBoxes.length; i++) {
            if(i != (n - 1)) {
                checkBoxes[i].setSelected(false);
            }
        }
        right2Ch.setSelected(false);
        left2Ch.setSelected(false);
        setBackground(backgroundScale, true);
    }
}

class ImagePanel extends JPanel {
    private Image img;
    private int[][] sensors, arrows;
    String[] roomNames;
    private boolean[] isFire;
    private boolean light;
    private double scale;

    public ImagePanel(Image img, double scale, int[][] sensors, String[] roomNames, int[][] arrows, boolean[] isFire) {
        this.roomNames = roomNames;
        this.img = img;
        this.scale = scale;
        this.sensors = new int[sensors.length][2];
        System.arraycopy(sensors, 0, this.sensors, 0, sensors.length );
        this.arrows = new int[arrows.length][4];
        System.arraycopy(arrows, 0, this.arrows, 0, arrows.length );
        this.isFire = new boolean[isFire.length];
        System.arraycopy(isFire, 0, this.isFire, 0, isFire.length );

        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);
    }

    public void paintComponent(Graphics g) {
        light = false; //проверка на пожар
        g.drawImage(img, 0, 0, null);
        g.setColor(Color.RED);//Цвет датчиков
        int line = 0;
        g.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        for(int i = 0; i < sensors.length; i++) {
            if(isFire[i] == true) {
                light = true;
                g.fillRect((int)Math.round(sensors[i + 1][0] * scale), (int)Math.round(sensors[i + 1][1] * scale),
                        (int)Math.round(sensors[0][0] * scale), (int)Math.round(sensors[0][1] * scale));
                g.drawString(roomNames[i + 1], (10 - (int)this.getLocation().getX()), (20 - (int)this.getLocation().getY() + line));
                line += 20;
            }
        }

        if(light) {
            g.setColor(Color.GREEN);//Цвет стрелок
            boolean l;

            for(int i = 1; i < arrows.length; i++) {
                l = true;

                if(i == 11) {
                    for(int j = 4; j < 7; j++) {
                        if(isFire[j] == true) {
                            l = false;
                            break;
                        }
                    }
                    if(isFire[19] == true) {
                        l = false;
                    }
                }
                if(i == 20) {
                    for(int j = 0; j < 57; j++) {
                        if(j != 19) {
                            if(j == 4) {
                                j = 7;
                            }
                            if(isFire[j] == true) {
                                l = false;
                                break;
                            }
                        }
                    }
                }
                if(i == 12 || i == 13) {
                    for(int j = 0; j < 22; j++) {
                        if(j == 7) {
                            j = 19;
                        }
                        if(isFire[j] == true) {
                            l = false;
                            break;
                        }
                    }
                }
                if(i == 21 || i == 22) {
                    for(int j = 7; j < 19; j++) {
                        if(isFire[j] == true) {
                            l = false;
                            break;
                        }
                    }
                    for(int j = 22; j < 56; j++) {
                        if(isFire[j] == true) {
                            l = false;
                            break;
                        }
                    }
                }
                if(i == 14) {
                    for(int j = 28; j < 49; j++) {
                        if(isFire[j] == true) {
                            l = false;
                            break;
                        }
                    }
                }
                if(i == 23) {
                    for(int j = 0; j < 57; j++) {
                        if(j == 28) {
                            j = 49;
                        }
                        if(isFire[j] == true) {
                            l = false;
                            break;
                        }
                    }
                }
                if(i == 15 || i == 16) {
                    for(int j = 32; j < 47; j++) {
                        if(j == 36) {
                            j = 40;
                        }
                        if(isFire[j] == true) {
                            l = false;
                            break;
                        }
                    }
                }
                if(i == 24 || i == 25) {
                    for(int j = 0; j < 57; j++) {
                        if(j == 32) {
                            j = 36;
                        }
                        if(j == 40) {
                            j = 46;
                        }
                        if(isFire[j] == true) {
                            l = false;
                            break;
                        }
                    }
                }
                if(i == 17) {
                    for(int j = 32; j < 44; j++) {
                        if(j == 36) {
                            j = 40;
                        }
                        if(isFire[j] == true) {
                            l = false;
                            break;
                        }
                    }
                }
                if(i == 26) {
                    for(int j = 0; j < 57; j++) {
                        if(j == 32) {
                            j = 36;
                        }
                        if(j == 40) {
                            j = 44;
                        }
                        if(isFire[j] == true) {
                            l = false;
                            break;
                        }
                    }
                }
                if(i == 18) {
                    for(int j = 34; j < 43; j++) {
                        if(j == 36) {
                            j = 40;
                        }
                        if(isFire[j] == true) {
                            l = false;
                            break;
                        }
                    }
                }
                if(i == 27) {
                    for(int j = 0; j < 57; j++) {
                        if(j == 34) {
                            j = 36;
                        }
                        if(j == 40) {
                            j = 43;
                        }
                        if(isFire[j] == true) {
                            l = false;
                            break;
                        }
                    }
                }
                if(i == 19) {
                    for(int j = 40; j < 42; j++) {
                        if(isFire[j] == true) {
                            l = false;
                            break;
                        }
                    }
                }
                if(i == 28) {
                    for(int j = 0; j < 57; j++) {
                        if(j == 40) {
                            j = 42;
                        }
                        if(isFire[j] == true) {
                            l = false;
                            break;
                        }
                    }
                }
                if(l) {
                    drawArrow(g, arrows[i][0], arrows[i][1], arrows[i][2], arrows[i][3]);
                }
            }
        }
    }

    public void drawArrow(Graphics g, int x1, int y1, int x2, int y2) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3.0f));//Толщина линий стрелок

        x1 = (int)Math.round(x1 * scale);
        x2 = (int)Math.round(x2 * scale);
        y1 = (int)Math.round(y1 * scale);
        y2 = (int)Math.round(y2 * scale);
        g2.drawLine(x1, y1, x2, y2);
        if(x1 == x2) { //Вертикальная стрелка
            int y3 = y1 < y2 ? y2 - 10 : y2 + 10;
            g2.drawLine(x2, y2, x2 + 5, y3);
            g2.drawLine(x2, y2, x2 - 5, y3);
        } else {  //Горизонтальная стрелка
            int x3 = x1 < x2 ? x2 - 10 : x2 + 10;
            g2.drawLine(x2, y2, x3, y2 + 5);
            g2.drawLine(x2, y2, x3, y2 - 5);
        }
    }
}
