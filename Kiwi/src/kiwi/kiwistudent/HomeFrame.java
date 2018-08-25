package kiwi.kiwistudent;

//TODO: logout functionality

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Creates home page interface.
 * @author Tala Ross(rsstal002)
 * @author Nikai Jagganath (jggnik001)
 * @author Steve Shun Wang (wngshu003)
 */
public class HomeFrame extends javax.swing.JFrame {
    
    
    //Model Instance Variables:
    
    /**
     * Student object containing and controlling information relevant to logged
     * in student.
     */
    static Student student;
    
    
    //Constructor:
    
    /**
     * Creates new form HomeFrame.
     * @param student Student object representing logged in student.
     */
    public HomeFrame(Student student) {
        this.student = student;
        initComponents();
        if (student.viewStats()) {
            DecimalFormat d = new DecimalFormat("0.00");
            txtGrade.setText(""+d.format(student.highestGrade));
            txtSubRemain.setText(""+student.noSubmissionsRemaining);
            txtDeadlineDate.setText(student.deadlineDay.toString());
            txtDeadlineTime.setText(student.deadlineTime.toString());
        }
        else {
            txtGrade.setText("Unavailable");
            txtSubRemain.setText("Unavailable");
            txtDeadlineDate.setText("Unavailable");
            txtDeadlineTime.setText("Unavailable");
            JOptionPane.showMessageDialog(this, "Could not access student information on database.", "Grade Access Error", JOptionPane.ERROR_MESSAGE);
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

        jPanel1 = new javax.swing.JPanel();
        btnStart = new javax.swing.JButton();
        lblGrade = new javax.swing.JLabel();
        txtGrade = new javax.swing.JTextField();
        lblSubRemain = new javax.swing.JLabel();
        txtSubRemain = new javax.swing.JTextField();
        lblDeadlineDate = new javax.swing.JLabel();
        txtDeadlineDate = new javax.swing.JTextField();
        lblDeadlineTime = new javax.swing.JLabel();
        txtDeadlineTime = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1024, 768));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                windowCloser(evt);
            }
        });

        jPanel1.setPreferredSize(new java.awt.Dimension(1024, 768));

        btnStart.setText("Start Assignment");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        lblGrade.setText("Highest Grade:");

        txtGrade.setEditable(false);

        lblSubRemain.setText("Submissions Remaining:");

        txtSubRemain.setEditable(false);
        txtSubRemain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSubRemainActionPerformed(evt);
            }
        });

        lblDeadlineDate.setText("Deadline Date:");

        txtDeadlineDate.setEditable(false);

        lblDeadlineTime.setText("Deadline Time:");

        txtDeadlineTime.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblDeadlineTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblGrade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDeadlineDate, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSubRemain, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSubRemain)
                    .addComponent(txtDeadlineDate)
                    .addComponent(txtGrade)
                    .addComponent(txtDeadlineTime))
                .addContainerGap())
            .addComponent(jSeparator1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(128, Short.MAX_VALUE)
                .addComponent(btnStart)
                .addGap(119, 119, 119))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(btnStart)
                .addGap(29, 29, 29)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGrade)
                    .addComponent(txtGrade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSubRemain)
                    .addComponent(txtSubRemain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDeadlineDate)
                    .addComponent(txtDeadlineDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDeadlineTime)
                    .addComponent(txtDeadlineTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    /**
     * Transitions to assignment frame.
     * @param evt Click "Start Assignment" button.
     */
    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        AssignmentFrame assignment = new AssignmentFrame(student);
        if (assignment.createdFlag) {
            this.setVisible(false);
            assignment.setVisible(true);
            this.dispose();
        }
        else {
            assignment.dispose();
        }
    }//GEN-LAST:event_btnStartActionPerformed

    private void txtSubRemainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSubRemainActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSubRemainActionPerformed

    private void windowCloser(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowCloser
        try {
                student.closeConnection();
            }
        catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Error closing sockets.", "Socket Error", JOptionPane.ERROR_MESSAGE);
            }
        System.exit(0);
    }//GEN-LAST:event_windowCloser

    
    //Main method:
    
    /**
     * Runs home page frame.
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HomeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HomeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HomeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HomeFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HomeFrame(student).setVisible(true);
            }
        });
    }

    
    //Interface instance variables:
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnStart;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblDeadlineDate;
    private javax.swing.JLabel lblDeadlineTime;
    private javax.swing.JLabel lblGrade;
    private javax.swing.JLabel lblSubRemain;
    private javax.swing.JTextField txtDeadlineDate;
    private javax.swing.JTextField txtDeadlineTime;
    private javax.swing.JTextField txtGrade;
    private javax.swing.JTextField txtSubRemain;
    // End of variables declaration//GEN-END:variables


}
