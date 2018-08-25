package kiwi.kiwistudent;

import java.io.IOException;
import java.text.DecimalFormat;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

//TODO: Create Assignment class to encapsulate logic and seperate from interface logic.
//TODO: When assignment class created, make functionality to load whole assignment and not just one question
//TODO: timers?
//TODO: deny access to tables with student/question info
//TODO: deny use of update statements
/**
 * Creates assignment interface.
 * @author Tala Ross(rsstal002)
 * @author Nikai Jagganath (jggnik001)
 * @author Steve Shun Wang (wngshu003)
 */
public class AssignmentFrame extends javax.swing.JFrame {
    
    
    //Model instance variables:
    //TODO: move most to assigment class
    
    
    /**
     * Student object containing and controlling information relevant to logged
     * in student.
     */
    static Student student;
    
    static boolean createdFlag;
    
    
    //Constructor:
    
    /**
     * Creates new form AssignmentFrame.
     * @param home Home frame to return to when done viewing grade.
     * @param student Student object representing logged in student.
     */
    public AssignmentFrame(Student student) {
        this.student = student;
        int resp = student.startAssignment();
        if (resp==Student.SUCCESS) {    //successful startup
            initComponents();
            jpbQuestionProgress.setMinimum(0);
            jpbQuestionProgress.setMaximum(student.getNoQuestions());
            jpbQuestionProgress.setStringPainted(true);
            txtaQuestion.setText("Question:\n" + student.getNextQuestion());
            btnNext.setEnabled(false);
            createdFlag = true;
        }
        else if (resp==Student.FAIL_DENY) {
            DecimalFormat d = new DecimalFormat("0.00");
            JOptionPane.showMessageDialog(this, "You have used up all your submissions."
                    + "\nYour final grade is: " + d.format(student.highestGrade), "Final Grade", JOptionPane.PLAIN_MESSAGE);
            createdFlag = false;
        }
        else {  //error in creation of assignment
            JOptionPane.showMessageDialog(this, "There was an error connecting to the database."
                    + "\nPlease start the assignment again.", "Startup Error", JOptionPane.ERROR_MESSAGE);
            createdFlag = false;
        }
    }
    
    @Override
    public void setVisible(boolean isVisible) {
        if (!createdFlag) {
            super.setVisible(false);
            HomeFrame home = new HomeFrame(student);
            home.setVisible(true);
            this.dispose();
        }
        else {
            super.setVisible(isVisible);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        txtaQuestion = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtaFeedback = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        txtfAnswer = new javax.swing.JTextField();
        btnSubmit = new javax.swing.JButton();
        btnShowSchema = new javax.swing.JButton();
        btnCheck = new javax.swing.JButton();
        txtfMark = new javax.swing.JTextField();
        btnHome = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtaOutput = new javax.swing.JTextArea();
        jpbQuestionProgress = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1024, 768));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                windowCloser(evt);
            }
        });

        jPanel1.setPreferredSize(new java.awt.Dimension(1024, 768));

        txtaQuestion.setEditable(false);
        txtaQuestion.setColumns(20);
        txtaQuestion.setRows(5);
        txtaQuestion.setText("Question:");
        jScrollPane1.setViewportView(txtaQuestion);

        txtaFeedback.setEditable(false);
        txtaFeedback.setColumns(20);
        txtaFeedback.setRows(5);
        txtaFeedback.setText("Feedback:");
        jScrollPane2.setViewportView(txtaFeedback);

        jLabel1.setText("Enter your answer:");

        btnSubmit.setText("Submit");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        btnShowSchema.setText("Show Schema");
        btnShowSchema.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowSchemaActionPerformed(evt);
            }
        });

        btnCheck.setText("Check Output");
        btnCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckActionPerformed(evt);
            }
        });

        txtfMark.setEditable(false);

        btnHome.setText("Home");
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });

        btnNext.setText("Next");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        jLabel2.setText("Mark:");

        txtaOutput.setEditable(false);
        txtaOutput.setColumns(20);
        txtaOutput.setRows(5);
        txtaOutput.setText("Output:");
        jScrollPane4.setViewportView(txtaOutput);

        jpbQuestionProgress.setToolTipText("");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSubmit))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnShowSchema))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnHome)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                        .addComponent(jpbQuestionProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(75, 75, 75)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtfMark, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnNext))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(27, 27, 27)
                        .addComponent(txtfAnswer))
                    .addComponent(jScrollPane4)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(btnShowSchema)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtfAnswer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSubmit)
                    .addComponent(btnCheck))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnHome)
                        .addComponent(btnNext)
                        .addComponent(txtfMark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(jpbQuestionProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    //Action performed methods:
    
    /**
     * Gets answer statement from text field and uses this to mark assignment.
     * @param evt Click "Submit" button.
     */
    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        String answer = txtfAnswer.getText();
        if (answer.equals("")) {    //empty answer field
            int confirm = JOptionPane.showConfirmDialog(this, "You did not provide an answer."
                    + "\nAre you sure you want to submit?", "Empty Submit Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm!=JOptionPane.YES_OPTION) {  //don't submit
                return;
            }
        }
        
        int resp = student.submit(answer);
        if (resp==Student.SUCCESS) { //submitted correctly to server
            txtfMark.setText(student.getCurrentMarkString());
            txtaFeedback.setText(student.getCurrentFeedback());
            btnSubmit.setEnabled(false);
            btnNext.setEnabled(true);
            jpbQuestionProgress.setValue(student.getNextQuestionNo()-1);
        }
        else if (resp==Student.FAIL_DENY) {    //lecturer answer was wrong
            JOptionPane.showMessageDialog(this, "Couldn't run lecturer's sql statement.\n"
                    + "Please contact your lecturer about this."
                    + "\nImportant note: This question will not be included in your assignment grade calculation.",
                    "Submission Error", JOptionPane.ERROR_MESSAGE);
            btnSubmit.setEnabled(false);
            btnNext.setEnabled(true);
            jpbQuestionProgress.setValue(student.getNextQuestionNo()-1);
        }
        else {  //DB connection error
            JOptionPane.showMessageDialog(this, "There was an error connecting to the database."
                    + "Please contact your lecturer about this, if the problem persists."
                    + "\nImportant note: This question will not be included in your assignment grade calculation.",
                    "Submission Error", JOptionPane.ERROR_MESSAGE);
            btnSubmit.setEnabled(false);
            btnNext.setEnabled(true);
            jpbQuestionProgress.setValue(student.getNextQuestionNo()-1);
        }
    }//GEN-LAST:event_btnSubmitActionPerformed
    
    /**
     * Goes back to home frame.
     * @param evt Click "Home" button.
     */
    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        if (!student.isAssignmentDone()) {  //assignment isn't complete
            int confirm = JOptionPane.showConfirmDialog(this, "You did not complete the assignment."
                    + "\nIf you quit now, you will receive 0 for this submission.\n"
                    + "Are you sure you want to quit?", "Quit Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm!=JOptionPane.YES_OPTION) {  //don't quit
                return;
            }
        }
        if (student.leaveAssignment()) {    //successful processing
            this.setVisible(false);
            HomeFrame home = new HomeFrame(student);
            home.setVisible(true);
            this.dispose();
        }
        else {
            int confirm = JOptionPane.showConfirmDialog(this, "There was an error saving your grade. If you leave now, your grade may not be saved.\n"
                    + "We suggest you re-attempt to leave the assignment so that your grade is properly saved."
                    + "Would you like to reattempt?", "Grade Saving Error", JOptionPane.YES_NO_OPTION);
            if (confirm==JOptionPane.YES_OPTION) {  //don't quit
                return;
            }
            else {  //quit anyway
                this.setVisible(false);
                HomeFrame home = new HomeFrame(student);
                home.setVisible(true);
                this.dispose();
            }
        }
    }//GEN-LAST:event_btnHomeActionPerformed
    
    /**
     * Goes to next question.
     * Mark for current question set to zero if not submitted.
     * @param evt 
     */
    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        if (student.isAssignmentDone()) {
            btnNext.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Your final grade is: " + student.getFinalGrade(), "Assignment Grade", JOptionPane.PLAIN_MESSAGE);
        }
        else {
            txtaQuestion.setText("Question:\n" + student.getNextQuestion());
            txtaFeedback.setText("");
            txtaOutput.setText("");
            txtfMark.setText("");
            txtfAnswer.setText("");
            btnSubmit.setEnabled(true);
            btnNext.setEnabled(false);
        }
    }//GEN-LAST:event_btnNextActionPerformed
    
    /**
     * Shows pop-up frame with picture representing the query data schema.
     * @param evt Click "Show Schema" button.
     */
    private void btnShowSchemaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowSchemaActionPerformed
        //ImageIcon icon = new ImageIcon("schema.jpg");
        ImageIcon icon = new ImageIcon(student.getSchemaImage());
        JOptionPane.showMessageDialog(this, "", "Query Database Schema", JOptionPane.PLAIN_MESSAGE, icon);
    }//GEN-LAST:event_btnShowSchemaActionPerformed
    
    /**
     * Processes student's sql statement in text field and shows what the
     * output would be.
     * @param evt Click "Check Output" button.
     */
    private void btnCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckActionPerformed
        String statement = txtfAnswer.getText();
        if (student.check(statement)) { //successful processing
            txtaOutput.setText(student.getCurrentCheckOutput());
        }
        else {
            JOptionPane.showMessageDialog(this, "There was an error connecting to the database."
                    + "\nPlease retry your statement check.", "Checking Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCheckActionPerformed

    private void windowCloser(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowCloser
        boolean done;
        if (!(done = student.isAssignmentDone())) {  //assignment isn't complete
            int confirm = JOptionPane.showConfirmDialog(this, "You did not complete the assignment."
                    + "\nIf you quit now, you will receive 0 for this submission.\n"
                    + "Are you sure you want to quit?", "Quit Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm!=JOptionPane.YES_OPTION) {  //don't quit
                return;
            }
        }
        if (student.leaveAssignment()) {    //successful processing
            try {
                    student.closeConnection();
            } 
            catch (IOException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(this, "Error closing sockets.", "Socket Error", JOptionPane.ERROR_MESSAGE);
            }
            System.exit(0);
        }
        else {  //unsuccessful processing
            if (done) { //finished assignment
                int confirm = JOptionPane.showConfirmDialog(this, "There was an error saving your grade. If you leave now, your grade may not be saved.\n"
                    + "We suggest you re-attempt to leave the assignment so that your grade is properly saved."
                    + "Would you like to reattempt?", "Grade Saving Error", JOptionPane.YES_NO_OPTION);
                if (confirm==JOptionPane.YES_OPTION) {  //don't quit
                    return;
                }
                else {  //quit anyway
                    try {
                            student.closeConnection();
                    } 
                    catch (IOException | ClassNotFoundException ex) {
                            JOptionPane.showMessageDialog(this, "Error closing sockets.", "Socket Error", JOptionPane.ERROR_MESSAGE);
                    }
                    System.exit(0);
                }
            }
            else {  //not finished assignment
                try {
                        student.closeConnection();
                } 
                catch (IOException | ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(this, "Error closing sockets.", "Socket Error", JOptionPane.ERROR_MESSAGE);
                }
                System.exit(0);
            }
        }
    }//GEN-LAST:event_windowCloser

    
    //Main method:
    
    /**
     * Runs assignment frame.
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
            java.util.logging.Logger.getLogger(AssignmentFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AssignmentFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AssignmentFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AssignmentFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AssignmentFrame(student).setVisible(true);
            }
        });
    }

    
    //Interface instance variables:
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheck;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnShowSchema;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JProgressBar jpbQuestionProgress;
    private javax.swing.JTextArea txtaFeedback;
    private javax.swing.JTextArea txtaOutput;
    private javax.swing.JTextArea txtaQuestion;
    private javax.swing.JTextField txtfAnswer;
    private javax.swing.JTextField txtfMark;
    // End of variables declaration//GEN-END:variables
    
    
    
}
