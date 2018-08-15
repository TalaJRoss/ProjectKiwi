package kiwilecturer;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * 
 * @author Tala Ross(rsstal002)
 */
public class Home extends javax.swing.JFrame {
    
    private File studentFile;
    private File questionsFile;
    private File [] dataFiles;
    
    /**
     * Creates new form Home
     */
    public Home() {
        initComponents();
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
        lblUpload = new javax.swing.JLabel();
        btnStudentCSV = new javax.swing.JButton();
        btnQuestions = new javax.swing.JButton();
        btnQueryData = new javax.swing.JButton();
        txtfQuestionsFilename = new javax.swing.JTextField();
        txtfQueryDataFilename = new javax.swing.JTextField();
        txtfStudentFilename = new javax.swing.JTextField();
        btnUpload = new javax.swing.JButton();
        lblViewGrades = new javax.swing.JLabel();
        btnView = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtaMarks = new javax.swing.JTextArea();
        btnGradeDescending = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblUpload.setText("Upload CSV Files:");

        btnStudentCSV.setText("Select student csv file");
        btnStudentCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStudentCSVActionPerformed(evt);
            }
        });

        btnQuestions.setText("Select questions csv");
        btnQuestions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuestionsActionPerformed(evt);
            }
        });

        btnQueryData.setText("Select query data csv");
        btnQueryData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQueryDataActionPerformed(evt);
            }
        });

        txtfQuestionsFilename.setEditable(false);

        txtfQueryDataFilename.setEditable(false);

        txtfStudentFilename.setEditable(false);
        txtfStudentFilename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtfStudentFilenameActionPerformed(evt);
            }
        });

        btnUpload.setText("Upload");
        btnUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadActionPerformed(evt);
            }
        });

        lblViewGrades.setText("View Student Grades:");

        btnView.setText("Student Number Ascending");
        btnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewActionPerformed(evt);
            }
        });

        txtaMarks.setEditable(false);
        txtaMarks.setColumns(20);
        txtaMarks.setRows(5);
        jScrollPane1.setViewportView(txtaMarks);

        btnGradeDescending.setText("Grade Descending");
        btnGradeDescending.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGradeDescendingActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblUpload)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnStudentCSV)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtfStudentFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(btnQuestions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnQueryData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(55, 55, 55))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(lblViewGrades)
                                .addGap(83, 83, 83)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtfQueryDataFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtfQuestionsFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUpload)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnGradeDescending)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnView))))
                    .addComponent(jScrollPane1))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblUpload)
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStudentCSV)
                    .addComponent(txtfStudentFilename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnQuestions)
                    .addComponent(txtfQuestionsFilename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnQueryData)
                    .addComponent(txtfQueryDataFilename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnUpload)
                .addGap(49, 49, 49)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblViewGrades)
                    .addComponent(btnView)
                    .addComponent(btnGradeDescending))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnStudentCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStudentCSVActionPerformed
        
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fc.showOpenDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            //check that file is a csv
            File temp= fc.getSelectedFile();
            if (!(temp.getName().substring(temp.getName().lastIndexOf("."))).equals(".csv")) {
                JOptionPane.showMessageDialog(null, "Incorrect file upload.\nPlease upload a .csv file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                studentFile = temp;
                txtfStudentFilename.setText(studentFile.getAbsolutePath());
            }
        }
        
        //use file to upload
    }//GEN-LAST:event_btnStudentCSVActionPerformed

    private void btnQuestionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuestionsActionPerformed
        
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fc.showOpenDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            File temp= fc.getSelectedFile();
            if (!(temp.getName().substring(temp.getName().lastIndexOf("."))).equals(".csv")) {
                JOptionPane.showMessageDialog(null, "Incorrect file upload.\nPlease upload a .csv file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                questionsFile = temp;
                txtfQuestionsFilename.setText(questionsFile.getAbsolutePath());
            }
        }
        
        //use file to upload
    }//GEN-LAST:event_btnQuestionsActionPerformed

    private void btnQueryDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQueryDataActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.home")));
        fc.setMultiSelectionEnabled(true);
        int result = fc.showOpenDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            File [] temp= fc.getSelectedFiles();
            String selected="";
            boolean flag=true;
            for (File file: temp) {
                if (!(file.getName().substring(file.getName().lastIndexOf("."))).equals(".csv")) {
                    JOptionPane.showMessageDialog(null, "Incorrect file upload for one or more files.\nPlease upload .csv files.", "Error", JOptionPane.ERROR_MESSAGE);
                    flag=false;
                    break;
                }
                selected+= file.getAbsolutePath()+", ";
            }
            if(flag){
                dataFiles = temp;
                txtfQueryDataFilename.setText(selected.substring(0, selected.length()-2));
            }
        }
        
        //use file to upload
    }//GEN-LAST:event_btnQueryDataActionPerformed

    private void btnUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadActionPerformed
        if (studentFile!=null) {
            Lecturer.uploadStudents(studentFile);
        }
        if (questionsFile!=null) {
            Lecturer.uploadQuestions(questionsFile);
        }
        if (dataFiles!=null) {
            Lecturer.uploadQueryData(dataFiles);
        }
        JOptionPane.showMessageDialog(null, "Uploaded .csv files succesfully.", "Done",JOptionPane.PLAIN_MESSAGE);
        //clean up:
        studentFile = null;
        txtfStudentFilename.setText("");
        questionsFile = null;
        txtfQuestionsFilename.setText("");
        dataFiles = null;
        txtfQueryDataFilename.setText("");
    }//GEN-LAST:event_btnUploadActionPerformed

    private void txtfStudentFilenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtfStudentFilenameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtfStudentFilenameActionPerformed

    private void btnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewActionPerformed
        txtaMarks.setText(Lecturer.viewGradeAscStudent());
    }//GEN-LAST:event_btnViewActionPerformed

    private void btnGradeDescendingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGradeDescendingActionPerformed
        txtaMarks.setText(Lecturer.viewGradeDescGrade());
    }//GEN-LAST:event_btnGradeDescendingActionPerformed

    /**
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
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Home().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGradeDescending;
    private javax.swing.JButton btnQueryData;
    private javax.swing.JButton btnQuestions;
    private javax.swing.JButton btnStudentCSV;
    private javax.swing.JButton btnUpload;
    private javax.swing.JButton btnView;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblUpload;
    private javax.swing.JLabel lblViewGrades;
    private javax.swing.JTextArea txtaMarks;
    private javax.swing.JTextField txtfQueryDataFilename;
    private javax.swing.JTextField txtfQuestionsFilename;
    private javax.swing.JTextField txtfStudentFilename;
    // End of variables declaration//GEN-END:variables
}
