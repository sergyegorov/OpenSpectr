/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.dev.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JPanel;
import org.tl.math.Analit;
import org.tl.math.MathTools;
import org.tl.math.TLExceptionMath;
import org.tl.ops.Common;
import org.tl.ops.db.LineDbFilterDialog;
import org.tl.ops.db.LineLibRecord;
import org.tl.util.DebugTools;
import org.tl.util.GUITools;

/**
 *
 * @author root
 */
public class SpectralRawDataPreViewPanel extends javax.swing.JPanel {
    final private SpectralRawDataPreViewPanel This;
    /**
     * Creates new form SpectralRawDataPreViewPanel
     */
    public SpectralRawDataPreViewPanel() {
        This = this;
        initComponents();
        draw = new ViewPanel();
        pRootPanel.setLayout(new BorderLayout());
        pRootPanel.add(draw,BorderLayout.CENTER);
        pRootPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
    
    ViewPanel draw;
    SpectralRawDispers initialDispes;
    public void setDispers(SpectralRawDispers disp){
        initialDispes = disp;
        draw.setDispers(disp);
    }
    
    SpectralPreview[] datas = new SpectralPreview[4];
    public SpectralPreview getView(int index){
        return datas[index];
    }
    
    public void showSpectr(int index,SpectralPreview data){
        datas[index] = data;
        draw.clear();
        SpectralPreview base = data;
        for(int i = 0;i<datas.length && base == null;i++)
            base = datas[i];
        if(base != null){
            int sn = base.MaximalValues.length;
            draw.setSensorCount(sn);
            for(int s = 0;s<sn;s++){
                draw.setSensorSize(s, base.MaximalValues[s].length,datas.length);
            }
            
            if(initialDispes == null)
                draw.setDispers(base.Dispers);
            else
                draw.setDispers(initialDispes);
            
            for(int d = 0;d<datas.length;d++){
                if(datas[d] == null)
                    continue;
                for(int s = 0;s<sn;s++){
                    draw.setData(s,d,datas[d].MinimalValues[s],
                            datas[d].MaximalValues[s],
                            datas[d].NullValues[s],
                            datas[d].OverloadLevels[s]);
                }
            }
        }
        draw.updateView();
        initScrollBar();
    }
    
    public void initScrollBar(){
        if(draw.ViewMaxLy > draw.MaxLy)
            draw.ViewMaxLy = draw.MaxLy;
        if(draw.ViewMinLy < draw.MinLy)
            draw.ViewMinLy = draw.MinLy;
        sbHorizontal.setValues((int)(draw.ViewMinLy),
                (int)(draw.ViewMaxLy-draw.ViewMinLy),
                (int)(draw.MinLy),(int)(draw.MaxLy));
    }
    
    public double CursorLy,CursorY;
    public ArrayList<Integer> CursorSn = new ArrayList<>();
    public ArrayList<Double> CursorPixels = new ArrayList<>();
    
    public SpectralMask getMask(File baseDir, String element,float ly,int cursorValueIndex) throws TLExceptionMath{
        SpectralMask sm = new SpectralMask(baseDir, element, ly);
        int sn = CursorSn.get(cursorValueIndex);
        double pixel = CursorPixels.get(cursorValueIndex);
        short[] dataMax = new short[draw.dataMin[sn].length];
        short[] dataMin = new short[draw.dataMin[sn].length];
        for(int i = 0;i<dataMax.length;i++){
            dataMin[i] = (short)(draw.dataMin[sn][i][0]);
            dataMax[i] = (short)(draw.dataMax[sn][i][0]);
        }
        short[] data = Analit.mix(dataMin, dataMax);
        //DebugTools.showData(GUITools.getParent(this), "Getting mask. Mix result", data);
        sm.initBy(data, ly, sn, (int)pixel, Integer.MAX_VALUE);//(int)draw.OverloadLevels[sn]);
        return sm;
    }
    
    public void updateView(){
        draw.updateView();
    }
    
    public void showAll(){
        bAllActionPerformed(null);
    }
    
    class ViewPanel extends JPanel{
        float[][][] dataMin;
        float[][][] dataMax;
        public float[][] Ly;
        boolean[] enRow;
        boolean[][] OverloadMinLevels,OverloadMaxLevels;
        
        public void updateView(){
            validate();
            revalidate();
            repaint();
        }
        
        public ViewPanel(){
            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try{
                        if(Dispers == null)
                            return;
                        CursorLy = getLy(e.getX());
                        Dispers.findPixelSensor(CursorLy, CursorSn);
                        CursorPixels.clear();
                        for(int i = 0;i<CursorSn.size();i++){
                            int sn = CursorSn.get(i);
                            double pixel = Dispers.getPixelForSensor(sn, CursorLy);
                            CursorPixels.add(pixel);
                            /*double ly = Dispers.getLambdaForSensor(sn, pixel);
                            double[] dat = new double[dataMin[sn].length];
                            for(int j = 0;j<dat.length;j++)
                                dat[j] = dataMin[sn][j][0];
                            DebugTools.showData(null, "SpectralRawDataPreviewPanel.mouseClick "+(CursorLy-ly), dat, pixel);//*/
                        }
                        CursorY = getY(e.getY());
                        draw.updateView();
                        if(cursorEvent != null)
                            cursorEvent.changed(This);
                    }catch(Exception ex){
                        Common.Log.exception("Move mouse exception", ex);
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
            addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    try{
                        
                    }catch(Exception ex){
                        Common.Log.exception("Move mouse exception", ex);
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    try{
                        if(Dispers == null){
                            lLy.setText("-");
                            lY.setText("-");
                            lSn.setText("-");
                            return;
                        }
                        double ly = getLy(e.getX());
                        lLy.setText(MathTools.getGoodValue(ly, 1));
                        double y = getY(e.getY());
                        lY.setText(Integer.toString((int)y));
                        if(Results.size() > 0 && e.getY() > getHeight()-10){
                            double fdlt = Double.MAX_VALUE;
                            for(int i = 0;i<Results.size();i++){
                                LineLibRecord rec = Results.get(i);
                                double dlt = Math.abs(rec.Ly-ly);
                                if(dlt < fdlt){
                                    fdlt = dlt;
                                    ResultSelected = i;
                                }
                            }
                            updateView();
                        }
                        ArrayList<Integer> sn = Dispers.getSensorList(ly, null);
                        String txt = "";
                        for(Integer s : sn)
                            txt += s+" ";
                        lSn.setText(txt);
                    }catch(Exception ex){
                        Common.Log.exception("Move mouse exception", ex);
                    }
                }
            });
            addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    int scroll = e.getWheelRotation();
                    double dlt = ViewMaxLy - ViewMinLy;
                    dlt /= 10;
                    dlt *= scroll;
                    dlt = -dlt;
                    ViewMaxLy -= dlt;
                    ViewMinLy += dlt;
                    if(ViewMinLy < MinLy)
                        ViewMinLy = MinLy;
                    if(ViewMaxLy > MaxLy)
                        ViewMaxLy = MaxLy;
                    updateView();
                    initScrollBar();
                }
            });
        }
        
        public void clear(){
            dataMin = null;
            kx = 0;
            dataSize = 0;
        }
        
        double getLy(int screenX){
            return ViewMinLy + (ViewMaxLy-ViewMinLy)*screenX/getWidth();
        }
        
        double getY(int screenY){
            int h = getHeight();
            //int screenY = psize.height-(int)(ky*(ret-MinVal))+5;
            //ky*(ret-MinVal) = psize.height + 5 - screenY;
            //ret-MinVal = (psize.height + 5 - screenY)/ky;
            //ret = (psize.height + 5 - screenY)/ky+MinVal;
            //double ret = MinVal + (MaxVal-MinVal)*(h - screenY - 5)/(h-10);
            double ret = (h + 5 - screenY)/ky+MinVal;
            return Math.pow(ret,1/powOrder);
        }
        
        public SpectralRawDispers Dispers;
        public float MinLy,MaxLy;
        public void setDispers(SpectralRawDispers disp){
            Dispers = disp;
            MinLy = Float.MAX_VALUE;
            MaxLy = -Float.MAX_VALUE;
            for(int s = 0;s<dataMin.length;s++){
                for(int p = 0;p<dataMin[s].length;p++){
                    Ly[s][p] = (float)Dispers.getLambdaForSensor(s, p);
                    if(MinLy > Ly[s][p])
                        MinLy = Ly[s][p];
                    if(MaxLy < Ly[s][p])
                        MaxLy = Ly[s][p];
                }
            }
            return;
        }
        
        int SN;
        public float MinVal,MaxVal;
        public void setSensorCount(int n){
            SN = n;
            MinVal = Float.MAX_VALUE;
            MaxVal = -Float.MAX_VALUE;
            dataMin = new float[n][][];
            dataMax = new float[n][][];
            OverloadMinLevels = new boolean[n][];
            OverloadMaxLevels = new boolean[n][];
            Ly = new float[n][];
        }
        
        public void setSensorSize(int n,int size,int rows){
            enRow = new boolean[rows];
            dataMin[n] = new float[size][rows];
            dataMax[n] = new float[size][rows];
            Ly[n] = new float[size];
            dataSize += size;
        }
        
        double powOrder = 0.5;
        public void setData(int sn,int row,
                short[] min,short[] max,short[] nul,
                short overloadLevel){
            enRow[row] = true;
            if(OverloadMinLevels[sn] == null){
                OverloadMinLevels[sn] = new boolean[min.length];
                OverloadMaxLevels[sn] = new boolean[max.length];
            }
            for(int i = 0;i<min.length;i++){
                float val = min[i]-nul[i];
                if(min[i] > overloadLevel)
                    OverloadMinLevels[sn][i] = true;
                if(val >= 0)
                    val = (float)Math.pow(val,0.5);
                else
                    val = (float)(-Math.pow(-val,0.5));
                dataMin[sn][i][row] = val;
                if(MinVal > val)
                    MinVal = val;
                
                val = max[i]-nul[i];
                if(max[i] > overloadLevel)
                    OverloadMaxLevels[sn][i] = true;
                if(val >= 0)
                    val = (float)Math.pow(val,powOrder);
                else
                    val = (float)(-Math.pow(-val,powOrder));
                dataMax[sn][i][row] = val;
                if(MaxVal < val)
                    MaxVal = val;
            }
            //OverloadLevels[sn] = overloadLevel;
        }

        Color[] palEMax = {Color.BLUE,Color.GREEN,Color.RED,Color.CYAN};
        Color[] palMax = {new Color(120,120,255),
            new Color(120,255,120),new Color(255,120,120),new Color(255,120,120)};
        Color[] palEMin = {Color.GRAY,Color.GRAY,Color.GRAY,Color.GRAY};
        public double ViewMinLy = -Double.MAX_VALUE,
                ViewMaxLy = Double.MAX_VALUE;
        double kx = 0,ky = 0;
        int dataSize = 0;
        @Override
        public void paint(Graphics g){
            try{
                Dimension psize = getSize();
                g.setColor(Color.white);
                g.fillRect(0, 0, psize.width, psize.height);
                if(dataMin == null || psize.width == 0 || psize.height == 0)
                    return;
                
                kx = psize.width/(ViewMaxLy - ViewMinLy);
                ky = (psize.height-10)/(MaxVal - MinVal);
                
                drawGreed(g, psize, kx, ky);
                drawLineDb(g, psize, kx, ky);
                drawGraph(g,psize,kx,ky,dataMin,palEMin,palEMin);
                drawGraph(g,psize,kx,ky,dataMax,palEMax,palMax);
                drawMarks(g, psize, kx, ky);
                
                g.setColor(Color.RED);
                int x = (int)(kx*(CursorLy-ViewMinLy));
                int y = psize.height-(int)(ky*(Math.pow(CursorY,powOrder)-MinVal))+5;
                g.drawLine(0,y,psize.width,y);
                g.drawLine(x, 0, x, psize.height);
            }catch(Exception ex){
                Common.Log.exception("Draw spectr exception...",ex);
            }
        }
        
        void drawMarks(Graphics g,Dimension psize, double kx,double ky){
            g.setColor(Color.blue);
            int y = 50;
            for(int i = 0;i<Marks.size();i++){
                Mark rec = Marks.get(i);
                if(rec.Ly < MinLy || rec.Ly > MaxLy)
                    continue;
                if(Math.abs(rec.Ly-MarksSelectedLy) > 0.1)
                    g.setColor(Color.ORANGE);
                else
                    g.setColor(Color.MAGENTA);
                int x = (int)(kx*(rec.Ly-ViewMinLy));
                g.drawLine(x, y, x, psize.height);
                if(rec.Enabled)
                    g.drawString(""+rec.Mark, x, y-2);
                else
                    g.drawString("("+rec.Mark+")", x, y-2);
                y += 15;
                if(y > psize.height)
                    y = 50;
            }
        }
        
        void drawLineDb(Graphics g,Dimension psize, double kx,double ky){
            g.setColor(Color.blue);
            final double pow = 0.7;
            for(int i = 0;i<Results.size();i++){
                LineLibRecord rec = Results.get(i);
                if(rec.Ly < MinLy || rec.Ly > MaxLy)
                    continue;
                if(i == ResultSelected)
                    g.setColor(Color.red);
                else
                    g.setColor(Color.blue);
                int x = (int)(kx*(rec.Ly-ViewMinLy));
                int y = psize.height-(int)((psize.height-20) * 
                        Math.pow(rec.Value+2,pow)/Math.pow(3000,pow));//(rec.Value/200.0));
                if(y < 20){
                    g.drawLine(x, 40, x, psize.height);
                    y = 20;
                    g.drawLine(x, 35, x, y);
                } else
                    g.drawLine(x, y, x, psize.height);
                if(i == ResultSelected){
                    g.drawLine(x,y-20,x,5);
                    y = 15;
                    g.drawString(""+rec.getElementName()+rec.IonLevel+" "+
                            MathTools.getGoodValue(rec.Ly,2)+" "+rec.Value, x, y-2);
                } else
                    g.drawString(""+rec.getElementName()+rec.IonLevel, x, y-2);
            }
        }
        
        void drawGreed(Graphics g,Dimension psize, double kx,double ky){
            int n = psize.width/100;
            double[] values = MathTools.getGoodValues(ViewMinLy, ViewMaxLy, n);
            if(values != null){
                g.setColor(Color.gray);
                for(int i = 0;i<values.length;i++){
                    int x = (int)(kx*(values[i]-ViewMinLy));
                    g.drawLine(x, 0, x, psize.height);
                }
                g.setColor(Color.black);
                for(int i = 0;i<values.length;i++){
                    int x = (int)(kx*(values[i]-ViewMinLy));
                    g.drawString(MathTools.getGoodValue(values[i], 1), x, 20);
                }
            }
            
            values = new double[]{0,10,50,100,250,500,1000,1500,2000,2500,3000,3500,4000};//MathTools.getGoodValues(Math.pow(MinVal,1/powOrder), Math.pow(MaxVal,1/powOrder), psize.height/50);
            g.setColor(Color.gray);
            for(int i = 0;i<values.length;i++){
                int y = psize.height-(int)(ky*(Math.pow(values[i],powOrder)-MinVal))+5;
                g.drawLine(0, y, psize.width, y);
            }
            g.setColor(Color.black);
            for(int i = 0;i<values.length;i++){
                int y = psize.height-(int)(ky*(Math.pow(values[i],powOrder)-MinVal))+5;
                g.drawString(MathTools.getGoodValue(values[i], 1), 5, y-2);
            }
        }
        
        void drawGraph(Graphics g,Dimension psize, double kx,double ky,float[][][] val,Color[] palEv,Color[] pal){
            for(int d = enRow.length-1;d>=0;d--){
                if(enRow[d] == false)
                    continue;
                for(int s = 0;s<SN;s++){
                    if(s%2 == 0)
                        g.setColor(palEv[d]);
                    else
                        g.setColor(pal[d]);
                    int px = (int)(kx*(Ly[s][0]-ViewMinLy));
                    int pminy = psize.height-(int)(ky*(val[s][0][d]-MinVal))+5;
                    for(int p = 0;p<Ly[s].length;p++){
                        int x = (int)(kx*(Ly[s][p]-ViewMinLy));
                        if(x >= 0 && x < psize.width){
                            int miny = psize.height-(int)(ky*(val[s][p][d]-MinVal))+5;
                            g.drawLine(px, pminy, x, miny);
                            pminy = miny;
                        }
                        px = x;
                    }
                }
            }
        }
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pToolBar = new javax.swing.JPanel();
        bAll = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        lLy = new javax.swing.JLabel();
        lY = new javax.swing.JLabel();
        lSn = new javax.swing.JLabel();
        bXPlus = new javax.swing.JButton();
        bXMinus = new javax.swing.JButton();
        bZ = new javax.swing.JButton();
        bLineDb = new javax.swing.JButton();
        sbHorizontal = new javax.swing.JScrollBar();
        pRootPanel = new javax.swing.JPanel();

        pToolBar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        bAll.setText("All");
        bAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAllActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.setMinimumSize(new java.awt.Dimension(250, 18));
        jPanel1.setPreferredSize(new java.awt.Dimension(250, 18));
        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        lLy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lLy.setText("-");
        lLy.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.add(lLy);

        lY.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lY.setText("-");
        lY.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.add(lY);

        lSn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lSn.setText("-");
        lSn.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.add(lSn);

        bXPlus.setText("X+");
        bXPlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bXPlusActionPerformed(evt);
            }
        });

        bXMinus.setText("X-");
        bXMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bXMinusActionPerformed(evt);
            }
        });

        bZ.setText("Z");
        bZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bZActionPerformed(evt);
            }
        });

        bLineDb.setText("Line Db");
        bLineDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLineDbActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pToolBarLayout = new javax.swing.GroupLayout(pToolBar);
        pToolBar.setLayout(pToolBarLayout);
        pToolBarLayout.setHorizontalGroup(
            pToolBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pToolBarLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bAll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bXPlus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bXMinus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bZ)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bLineDb)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pToolBarLayout.setVerticalGroup(
            pToolBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pToolBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(bAll)
                .addComponent(bXPlus)
                .addComponent(bXMinus)
                .addComponent(bZ)
                .addComponent(bLineDb))
        );

        sbHorizontal.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
        sbHorizontal.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                sbHorizontalAdjustmentValueChanged(evt);
            }
        });

        javax.swing.GroupLayout pRootPanelLayout = new javax.swing.GroupLayout(pRootPanel);
        pRootPanel.setLayout(pRootPanelLayout);
        pRootPanelLayout.setHorizontalGroup(
            pRootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 741, Short.MAX_VALUE)
        );
        pRootPanelLayout.setVerticalGroup(
            pRootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 327, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(sbHorizontal, javax.swing.GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE)
            .addComponent(pRootPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sbHorizontal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pRootPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    SpectralPreviewCursorEvent cursorEvent;
    public void setCursorEvent(SpectralPreviewCursorEvent event){
        cursorEvent = event;
    }
    
    private void bAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAllActionPerformed
        try{
            draw.ViewMinLy = draw.MinLy;
            draw.ViewMaxLy = draw.MaxLy;
            initScrollBar();
            draw.updateView();
        }catch(Exception ex){
            Common.Log.exception("All button action exception...", ex);
        }
    }//GEN-LAST:event_bAllActionPerformed

    private void bXPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bXPlusActionPerformed
        try{
            double dlt = draw.ViewMaxLy - draw.ViewMinLy;
            dlt /= 10;
            draw.ViewMinLy += dlt;
            draw.ViewMaxLy -= dlt;
            initScrollBar();
            draw.updateView();
        }catch(Exception ex){
            Common.Log.exception("All button action exception...", ex);
        }
    }//GEN-LAST:event_bXPlusActionPerformed

    private void bXMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bXMinusActionPerformed
        try{
            double dlt = draw.ViewMaxLy - draw.ViewMinLy;
            dlt /= 10;
            draw.ViewMinLy -= dlt;
            draw.ViewMaxLy += dlt;
            initScrollBar();
            draw.updateView();
        }catch(Exception ex){
            Common.Log.exception("All button action exception...", ex);
        }
    }//GEN-LAST:event_bXMinusActionPerformed

    private void bZActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bZActionPerformed
        try{
            double dlt = draw.ViewMaxLy - draw.ViewMinLy;
            dlt /= 6;
            draw.ViewMinLy = CursorLy-dlt;
            draw.ViewMaxLy = CursorLy+dlt;
            initScrollBar();
            draw.updateView();
        }catch(Exception ex){
            Common.Log.exception("All button action exception...", ex);
        }
    }//GEN-LAST:event_bZActionPerformed

    private void sbHorizontalAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_sbHorizontalAdjustmentValueChanged
        try{
            draw.ViewMinLy = sbHorizontal.getValue();
            draw.ViewMaxLy = sbHorizontal.getValue()+sbHorizontal.getVisibleAmount();
            draw.updateView();
        }catch(Exception ex){
            Common.Log.exception("All button action exception...", ex);
        }
    }//GEN-LAST:event_sbHorizontalAdjustmentValueChanged

    ArrayList<LineLibRecord> Results = new ArrayList<>();
    int ResultSelected;
    
    public LineLibRecord getSelectedLine(){
        if(ResultSelected < 0)
            return null;
        return Results.get(ResultSelected);
    }
    
    LineDbFilterDialog filter;
    private void bLineDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bLineDbActionPerformed
        try{
            if(filter == null){
                Window wind = GUITools.getParent(this);
                filter = new LineDbFilterDialog(wind, true);
            }
            filter.setVisible(true);
            Results = filter.Results;
            draw.updateView();
        }catch(Exception ex){
            Common.Log.exception("All button action exception...", ex);
        }
    }//GEN-LAST:event_bLineDbActionPerformed

    class Mark{
        public double Ly;
        public String Mark;
        public boolean Enabled;
        public Mark(double ly,String m,boolean enabled){
            Ly = ly;
            Mark = m;
            Enabled = enabled;
        }
    }
    
    ArrayList<Mark> Marks = new ArrayList<>();
    double MarksSelectedLy = 0;
    public void clearMark(){
        Marks.clear();
    }
    
    public void addMark(double ly,String name,boolean enabled){
        Marks.add(new Mark(ly,name,enabled));
    }
    
    public void setMarkSelectedLy(double ly){
        MarksSelectedLy = ly;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAll;
    private javax.swing.JButton bLineDb;
    private javax.swing.JButton bXMinus;
    private javax.swing.JButton bXPlus;
    private javax.swing.JButton bZ;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lLy;
    private javax.swing.JLabel lSn;
    private javax.swing.JLabel lY;
    private javax.swing.JPanel pRootPanel;
    private javax.swing.JPanel pToolBar;
    private javax.swing.JScrollBar sbHorizontal;
    // End of variables declaration//GEN-END:variables
}
