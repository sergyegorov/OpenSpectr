/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tl.ops.dev.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import org.tl.ops.Common;

/**
 *
 * @author root
 */
public class SpectralRawDataPreViewer extends javax.swing.JPanel {
    DrawPanel panel;
    /**
     * Creates new form SpectralRawDataViewer
     */
    public SpectralRawDataPreViewer() {
        initComponents();
        panel = new DrawPanel();
        pContainer.setLayout(new BorderLayout());
        pContainer.add(panel,BorderLayout.CENTER);
        setPreferredSize(new java.awt.Dimension(800,400));
    }
    
    class DrawPanel extends JPanel{
        Color[] palate = new Color[1024];
        public DrawPanel(){
            int[] positions = {0,256,512,768,1023};
            int[] rt = {0, 0, 0, 1, 1};
            int[] gt = {0, 0, 1, 1, 1};
            int[] bt = {0, 1, 0, 0, 1};
            for(int i = 0;i<palate.length;i++){
                int r = 0,g = 0,b = 0;
                for(int c = 0;c<positions.length;c++){
                    int dlt = Math.abs(i - positions[c]);
                    if(dlt > 255)
                        continue;
                    dlt = 255 - dlt;
                    r += (int)(rt[c]*dlt);
                    g += (int)(gt[c]*dlt);
                    b += (int)(bt[c]*dlt);
                }
                palate[i] = new Color(r,g,b);
            }
        }
        
        short dataMin,dataMax;
        SpectralRawData data;
        short[][][] paintData;
        public void showSpectr(SpectralRawData data){
            this.data = data;
            dataMin = 1000;
            dataMax = 3300;
            int sn = data.getSensorCount();
            paintData = new short[sn][][];
            int[] paintDataCount = new int[sn];
            for(int i = 0;i<paintData.length;i++){
                int n = data.getSensorFrameCount(i);
                paintData[i] = new short[n][];
            }
            int n = data.getRecordsCount();
            for(int i = 0;i<n;i++){
                SpectralRawDataRecord r = data.getRecord(i);
                paintData[r.Sensor][paintDataCount[r.Sensor]] = r.Data;
                paintDataCount[r.Sensor] ++;
                for(int j = 0;j<r.Data.length;j++){
                    if(r.Data[j] < dataMin)
                        dataMin = r.Data[j];
                    if(r.Data[j] > dataMax)
                        dataMax = r.Data[j];
                }
            }
        }
        
        @Override
        public void paint(Graphics g){
            try{
                Dimension psize = getSize();
                if(psize.height == 0 || psize.width == 0)
                    return;
                double vk = (palate.length-1)/(double)Math.sqrt(dataMax-dataMin);
                double k = psize.width/(double)palate.length;
                int step = (int)(1/k);
                step /= 2;
                if(step <= 0)
                    step = 1;
                if(k <= 1){
                    for(int i = 0;i<palate.length;i+=step){
                        int x = (int)(i*k);
                        g.setColor(palate[i]);
                        g.drawLine(x,0,x,10);
                    }
                } else {
                    for(int i = 0;i<palate.length;i+=step){
                        int x = (int)(i*k);
                        g.setColor(palate[i]);
                        g.fillRect(x,0,(int)k+1,10);
                    }
                }
                double snStep = psize.width/(double)paintData.length;
                double sn_from = 0;
                for(int s = 0;s<paintData.length;s++){
                    double sn_to = sn_from + snStep;
                    short[][] cdata = paintData[s];
                    double ky = cdata.length/(double)(psize.height-20);
                    double kx = cdata[0].length/(sn_to-sn_from-1);
                    for(int t = 0;t<cdata.length;t++){
                        int y_from = (int)(t/ky)+10;
                        int y_to = (int)((t+1)/ky)+11;
                        short[] pdata = cdata[t];
                        short max = pdata[0];
                        int prevx = (int)(sn_from);
                        for(int pixel = 0;pixel < pdata.length;pixel++){
                            int x = (int)(pixel/kx+sn_from);
                            if(prevx == x){
                                if(pdata[pixel] > max)
                                    max = pdata[pixel];
                                continue;
                            }
                            g.setColor(palate[(int)(Math.sqrt(max-dataMin)*vk)]);
                            g.drawLine(prevx, y_from, prevx, y_to);
                            prevx = x;
                            max = pdata[pixel];
                        }
                        g.setColor(palate[max]);
                        g.drawLine(prevx, y_from, prevx, y_to);
                    }
                    g.setColor(Color.white);
                    g.drawLine((int)sn_to-1, 10, (int)sn_to-1, psize.height-10);
                    sn_from = sn_to;
                }
            }catch(Exception ex){
                Common.Log.exception("Paint preview spectr exception ", ex);
            }
        }
    }

    public void showSpectr(SpectralRawData data){
        panel.showSpectr(data);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lLy = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lY = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lSn = new javax.swing.JLabel();
        pContainer = new javax.swing.JPanel();

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Ly:");

        lLy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lLy.setText("-");
        lLy.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setText("Y:");

        lY.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lY.setText("-");
        lY.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setText("Sn#");

        lSn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lSn.setText("-");
        lSn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lLy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lY, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lSn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(8, 8, 8)
                .addComponent(lLy)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lY)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lSn)
                .addGap(0, 139, Short.MAX_VALUE))
        );

        pContainer.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pContainerLayout = new javax.swing.GroupLayout(pContainer);
        pContainer.setLayout(pContainerLayout);
        pContainerLayout.setHorizontalGroup(
            pContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 757, Short.MAX_VALUE)
        );
        pContainerLayout.setVerticalGroup(
            pContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lLy;
    private javax.swing.JLabel lSn;
    private javax.swing.JLabel lY;
    private javax.swing.JPanel pContainer;
    // End of variables declaration//GEN-END:variables
}
