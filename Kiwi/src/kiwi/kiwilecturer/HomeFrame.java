package kiwi.kiwilecturer;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Interface for lecturer input.
 * @author Tala Ross(rsstal002)
 * @author Nikai Jagganath (jggnik001)
 * @author Steve Shun Wang (wngshu003)
 */
//TODO: handle crash close and close DB connection on close
public class HomeFrame extends javax.swing.JFrame {
    
    
    //Model instance variables: files to upload 
    
    /**
     * Students info csv file.
     */
    private String studentPath;
    
    /**
     * Question-answer pairs csv file.
     */
    private String questionsPath;
    
    /**
     * Array of csv files representing query data.
     */
    private String [] dataFilePaths;
    private String [] dataFileNames;
    
    /**
     * Path of the schema image.
     */
    private String schemaPath;
    
    //Constructor:
    
    /**
     * Creates new form HomeFrame.
     */
    Lecturer lecturer;
    boolean connected;
    
    public HomeFrame() {
        initComponents();

        txtaMarks.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        try {
            
            lecturer = new Lecturer();
            connected = lecturer.isConnected();
        } catch (IOException | ClassNotFoundException ex) {
            connected = false;
        }
        checkConnect();
    }
    
    private void checkConnect(){
        if (connected) {
            txtfFeedback.setText("Connected to database");
            enableButtons();
            btnReconnect.setEnabled(false);
        } else {
            txtfFeedback.setText("Not connected to database");
            disableButtons();
            btnReconnect.setEnabled(true);
        }
    }
    
    private void disableButtons(){
        btnGradeDescending.setEnabled(false);
        btnQueryData.setEnabled(false);
        btnQuestions.setEnabled(false);
        btnStudentCSV.setEnabled(false);
        btnUploadQueryData.setEnabled(false);
        btnView.setEnabled(false);
        btnUpdateDeadline.setEnabled(false);
        btnUploadQuestions.setEnabled(false);
        btnUploadStudent.setEnabled(false);
        btnInfoUpload.setEnabled(false);
        btnResetQueryData.setEnabled(false);
        btnResetQuestions.setEnabled(false);
        btnResetStudents.setEnabled(false);
        btnSchemaPath.setEnabled(false);
        btnSchemaUpload.setEnabled(false);
    }
    
    private void enableButtons(){
        btnGradeDescending.setEnabled(true);
        btnQueryData.setEnabled(true);
        btnQuestions.setEnabled(true);
        btnStudentCSV.setEnabled(true);
        btnUploadQueryData.setEnabled(true);
        btnView.setEnabled(true);
        btnUpdateDeadline.setEnabled(true);
        btnUploadQuestions.setEnabled(true);
        btnUploadStudent.setEnabled(true);
        btnInfoUpload.setEnabled(true);
        btnResetQueryData.setEnabled(true);
        btnResetQuestions.setEnabled(true);
        btnResetStudents.setEnabled(true);
        btnSchemaPath.setEnabled(true);
        btnSchemaUpload.setEnabled(true);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGroupAssignmentType = new javax.swing.ButtonGroup();
        pnlBase = new javax.swing.JPanel();
        pnlDeadline = new javax.swing.JPanel();
        lblUD = new javax.swing.JLabel();
        lblUDstudentNo = new javax.swing.JLabel();
        txtfUDstudentNo = new javax.swing.JTextField();
        jdcUDdate = new com.toedter.calendar.JDateChooser();
        lblUDdate = new javax.swing.JLabel();
        lblUDtime = new javax.swing.JLabel();
        jspUDhh = new javax.swing.JSpinner();
        jspUDmm = new javax.swing.JSpinner();
        jspUDss = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnUpdateDeadline = new javax.swing.JButton();
        pnlAssignment = new javax.swing.JPanel();
        lblNoSubmissions = new javax.swing.JLabel();
        jSpinNoSubmission = new javax.swing.JSpinner();
        jSpinNoQuestions = new javax.swing.JSpinner();
        lblNoQuestions = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        jdcDate = new com.toedter.calendar.JDateChooser();
        jspSS = new javax.swing.JSpinner();
        lblColon1 = new javax.swing.JLabel();
        jspMM = new javax.swing.JSpinner();
        jspHH = new javax.swing.JSpinner();
        lblTime = new javax.swing.JLabel();
        rbtnAssignment = new javax.swing.JRadioButton();
        rbtnClosedPrac = new javax.swing.JRadioButton();
        btnInfoUpload = new javax.swing.JButton();
        lblColon2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblPracType = new javax.swing.JLabel();
        pnlFiles = new javax.swing.JPanel();
        lblUpload = new javax.swing.JLabel();
        btnStudentCSV = new javax.swing.JButton();
        txtfStudentFilename = new javax.swing.JTextField();
        btnResetStudents = new javax.swing.JButton();
        btnUploadStudent = new javax.swing.JButton();
        txtfQuestionsFilename = new javax.swing.JTextField();
        btnUploadQuestions = new javax.swing.JButton();
        btnResetQuestions = new javax.swing.JButton();
        btnQuestions = new javax.swing.JButton();
        btnQueryData = new javax.swing.JButton();
        txtfQueryDataFilename = new javax.swing.JTextField();
        btnResetQueryData = new javax.swing.JButton();
        btnUploadQueryData = new javax.swing.JButton();
        btnSchemaPath = new javax.swing.JButton();
        txtfSchemaPath = new javax.swing.JTextField();
        btnSchemaUpload = new javax.swing.JButton();
        plnGrades = new javax.swing.JPanel();
        lblViewGrades = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtaMarks = new javax.swing.JTextArea();
        btnGradeDescending = new javax.swing.JButton();
        btnView = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtfFeedback = new javax.swing.JTextField();
        btnReconnect = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1600, 1024));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                windowCloser(evt);
            }
        });

        pnlBase.setPreferredSize(new java.awt.Dimension(1550, 1024));

        pnlDeadline.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlDeadline.setPreferredSize(new java.awt.Dimension(728, 384));

        lblUD.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lblUD.setText("Student Deadline Update");

        lblUDstudentNo.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblUDstudentNo.setText("Student Number");

        txtfUDstudentNo.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        jdcUDdate.setDateFormatString("yyyy-MM-dd");
        jdcUDdate.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        lblUDdate.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblUDdate.setText("New Deadline Date");

        lblUDtime.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblUDtime.setText("New Deadline Time");

        jspUDhh.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jspUDhh.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));

        jspUDmm.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jspUDmm.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jspUDss.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jspUDss.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel1.setText(":");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel2.setText(":");

        btnUpdateDeadline.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnUpdateDeadline.setText("Update");
        btnUpdateDeadline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateDeadlineActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlDeadlineLayout = new javax.swing.GroupLayout(pnlDeadline);
        pnlDeadline.setLayout(pnlDeadlineLayout);
        pnlDeadlineLayout.setHorizontalGroup(
            pnlDeadlineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDeadlineLayout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(pnlDeadlineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblUDstudentNo)
                    .addComponent(lblUDdate)
                    .addComponent(lblUDtime))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                .addGroup(pnlDeadlineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jdcUDdate, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlDeadlineLayout.createSequentialGroup()
                        .addComponent(jspUDhh, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jspUDmm, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jspUDss, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtfUDstudentNo, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(253, 253, 253))
            .addGroup(pnlDeadlineLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblUD)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDeadlineLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnUpdateDeadline, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39))
        );
        pnlDeadlineLayout.setVerticalGroup(
            pnlDeadlineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDeadlineLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblUD)
                .addGap(42, 42, 42)
                .addGroup(pnlDeadlineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUDstudentNo)
                    .addComponent(txtfUDstudentNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(pnlDeadlineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDeadlineLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jdcUDdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDeadlineLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(lblUDdate)))
                .addGap(35, 35, 35)
                .addGroup(pnlDeadlineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jspUDhh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jspUDmm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jspUDss, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUDtime))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 122, Short.MAX_VALUE)
                .addComponent(btnUpdateDeadline)
                .addGap(54, 54, 54))
        );

        pnlAssignment.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblNoSubmissions.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblNoSubmissions.setText("Number of submissions allowed");

        jSpinNoSubmission.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jSpinNoSubmission.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        jSpinNoQuestions.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jSpinNoQuestions.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        lblNoQuestions.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblNoQuestions.setText("Number of assignment questions");

        lblDate.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblDate.setText("Deadline Date");

        jdcDate.setDateFormatString("yyyy-MM-dd");
        jdcDate.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        jspSS.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jspSS.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        lblColon1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblColon1.setText(":");

        jspMM.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jspMM.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        jspHH.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jspHH.setModel(new javax.swing.SpinnerNumberModel(0, 0, 23, 1));

        lblTime.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblTime.setText("Deadline Time");

        btnGroupAssignmentType.add(rbtnAssignment);
        rbtnAssignment.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        rbtnAssignment.setSelected(true);
        rbtnAssignment.setText("Assignment");

        btnGroupAssignmentType.add(rbtnClosedPrac);
        rbtnClosedPrac.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        rbtnClosedPrac.setText("Closed Test");
        rbtnClosedPrac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnClosedPracActionPerformed(evt);
            }
        });

        btnInfoUpload.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnInfoUpload.setText("Upload Assignment Info");
        btnInfoUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInfoUploadActionPerformed(evt);
            }
        });

        lblColon2.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblColon2.setText(":");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jLabel3.setText("Assignment Information");

        lblPracType.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblPracType.setText("Type of Practical");

        javax.swing.GroupLayout pnlAssignmentLayout = new javax.swing.GroupLayout(pnlAssignment);
        pnlAssignment.setLayout(pnlAssignmentLayout);
        pnlAssignmentLayout.setHorizontalGroup(
            pnlAssignmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAssignmentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAssignmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAssignmentLayout.createSequentialGroup()
                        .addGroup(pnlAssignmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNoSubmissions)
                            .addComponent(lblDate))
                        .addGap(25, 25, 25)
                        .addComponent(jdcDate, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlAssignmentLayout.createSequentialGroup()
                        .addGroup(pnlAssignmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(lblPracType)
                            .addGroup(pnlAssignmentLayout.createSequentialGroup()
                                .addComponent(lblNoQuestions)
                                .addGap(18, 18, 18)
                                .addGroup(pnlAssignmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jSpinNoSubmission, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jSpinNoQuestions, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(pnlAssignmentLayout.createSequentialGroup()
                                .addComponent(lblTime)
                                .addGap(148, 148, 148)
                                .addGroup(pnlAssignmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnlAssignmentLayout.createSequentialGroup()
                                        .addComponent(rbtnClosedPrac)
                                        .addGap(75, 75, 75)
                                        .addComponent(rbtnAssignment))
                                    .addGroup(pnlAssignmentLayout.createSequentialGroup()
                                        .addComponent(jspHH, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblColon2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jspMM, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblColon1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jspSS, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAssignmentLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnInfoUpload)
                .addGap(34, 34, 34))
        );
        pnlAssignmentLayout.setVerticalGroup(
            pnlAssignmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAssignmentLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(39, 39, 39)
                .addGroup(pnlAssignmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlAssignmentLayout.createSequentialGroup()
                        .addGroup(pnlAssignmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNoSubmissions)
                            .addComponent(jSpinNoSubmission, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31)
                        .addGroup(pnlAssignmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNoQuestions)
                            .addComponent(jSpinNoQuestions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(33, 33, 33)
                        .addComponent(lblDate, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jdcDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlAssignmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTime)
                    .addComponent(jspHH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jspMM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblColon1)
                    .addComponent(jspSS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblColon2))
                .addGap(32, 32, 32)
                .addGroup(pnlAssignmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPracType)
                    .addComponent(rbtnClosedPrac)
                    .addComponent(rbtnAssignment))
                .addGap(32, 32, 32)
                .addComponent(btnInfoUpload)
                .addGap(45, 45, 45))
        );

        pnlFiles.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblUpload.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lblUpload.setText("CSV Files");

        btnStudentCSV.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnStudentCSV.setText("Select student csv file");
        btnStudentCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStudentCSVActionPerformed(evt);
            }
        });

        txtfStudentFilename.setEditable(false);
        txtfStudentFilename.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        btnResetStudents.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnResetStudents.setText("Reset");
        btnResetStudents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetStudentsActionPerformed(evt);
            }
        });

        btnUploadStudent.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnUploadStudent.setText("Upload");
        btnUploadStudent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadStudentActionPerformed(evt);
            }
        });

        txtfQuestionsFilename.setEditable(false);
        txtfQuestionsFilename.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        btnUploadQuestions.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnUploadQuestions.setText("Upload");
        btnUploadQuestions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadQuestionsActionPerformed(evt);
            }
        });

        btnResetQuestions.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnResetQuestions.setText("Reset");
        btnResetQuestions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetQuestionsActionPerformed(evt);
            }
        });

        btnQuestions.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnQuestions.setText("Select questions csv file");
        btnQuestions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuestionsActionPerformed(evt);
            }
        });

        btnQueryData.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnQueryData.setText("Select query data csv files");
        btnQueryData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQueryDataActionPerformed(evt);
            }
        });

        txtfQueryDataFilename.setEditable(false);
        txtfQueryDataFilename.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        btnResetQueryData.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnResetQueryData.setText("Reset");
        btnResetQueryData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetQueryDataActionPerformed(evt);
            }
        });

        btnUploadQueryData.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnUploadQueryData.setText("Upload");
        btnUploadQueryData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadQueryDataActionPerformed(evt);
            }
        });

        btnSchemaPath.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnSchemaPath.setText("Select schema image");
        btnSchemaPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSchemaPathActionPerformed(evt);
            }
        });

        txtfSchemaPath.setEditable(false);
        txtfSchemaPath.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N

        btnSchemaUpload.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnSchemaUpload.setText("Upload Schema");
        btnSchemaUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSchemaUploadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFilesLayout = new javax.swing.GroupLayout(pnlFiles);
        pnlFiles.setLayout(pnlFilesLayout);
        pnlFilesLayout.setHorizontalGroup(
            pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnlFilesLayout.createSequentialGroup()
                                .addComponent(btnResetStudents, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnUploadStudent))
                            .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(pnlFilesLayout.createSequentialGroup()
                                    .addComponent(btnStudentCSV, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                                    .addComponent(txtfStudentFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pnlFilesLayout.createSequentialGroup()
                                    .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(btnQueryData, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnQuestions, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(txtfQueryDataFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtfQuestionsFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(pnlFilesLayout.createSequentialGroup()
                                            .addComponent(btnResetQuestions, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(btnUploadQuestions))
                                        .addGroup(pnlFilesLayout.createSequentialGroup()
                                            .addComponent(btnResetQueryData, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(btnUploadQueryData))))))
                        .addGroup(pnlFilesLayout.createSequentialGroup()
                            .addGap(14, 14, 14)
                            .addComponent(lblUpload))
                        .addGroup(pnlFilesLayout.createSequentialGroup()
                            .addComponent(btnSchemaPath, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(21, 21, 21)
                            .addComponent(txtfSchemaPath)))
                    .addComponent(btnSchemaUpload, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        pnlFilesLayout.setVerticalGroup(
            pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFilesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblUpload)
                .addGap(18, 18, 18)
                .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStudentCSV)
                    .addComponent(txtfStudentFilename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUploadStudent)
                    .addComponent(btnResetStudents))
                .addGap(18, 18, 18)
                .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnQuestions)
                    .addComponent(txtfQuestionsFilename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUploadQuestions)
                    .addComponent(btnResetQuestions))
                .addGap(18, 18, 18)
                .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnQueryData)
                    .addComponent(txtfQueryDataFilename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUploadQueryData)
                    .addComponent(btnResetQueryData))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlFilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSchemaPath)
                    .addComponent(txtfSchemaPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSchemaUpload)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        plnGrades.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        plnGrades.setPreferredSize(new java.awt.Dimension(728, 361));

        lblViewGrades.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        lblViewGrades.setText("View Student Grades");

        txtaMarks.setEditable(false);
        txtaMarks.setColumns(20);
        txtaMarks.setRows(5);
        jScrollPane1.setViewportView(txtaMarks);

        btnGradeDescending.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnGradeDescending.setText("Grade Descending");
        btnGradeDescending.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGradeDescendingActionPerformed(evt);
            }
        });

        btnView.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnView.setText("Student Number Ascending");
        btnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout plnGradesLayout = new javax.swing.GroupLayout(plnGrades);
        plnGrades.setLayout(plnGradesLayout);
        plnGradesLayout.setHorizontalGroup(
            plnGradesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plnGradesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(plnGradesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plnGradesLayout.createSequentialGroup()
                        .addComponent(lblViewGrades)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, plnGradesLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnView)
                        .addGap(18, 18, 18)
                        .addComponent(btnGradeDescending, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        plnGradesLayout.setVerticalGroup(
            plnGradesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(plnGradesLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(lblViewGrades)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addGap(18, 18, 18)
                .addGroup(plnGradesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnView)
                    .addComponent(btnGradeDescending))
                .addGap(19, 19, 19))
        );

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel4.setText("SLQ AutoMarker");

        txtfFeedback.setEditable(false);

        btnReconnect.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        btnReconnect.setText("Reconnect");
        btnReconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReconnectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlBaseLayout = new javax.swing.GroupLayout(pnlBase);
        pnlBase.setLayout(pnlBaseLayout);
        pnlBaseLayout.setHorizontalGroup(
            pnlBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBaseLayout.createSequentialGroup()
                .addContainerGap(35, Short.MAX_VALUE)
                .addGroup(pnlBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(pnlBaseLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtfFeedback, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnReconnect))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlBaseLayout.createSequentialGroup()
                        .addGroup(pnlBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlAssignment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(28, 28, 28)
                        .addGroup(pnlBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(plnGrades, javax.swing.GroupLayout.DEFAULT_SIZE, 734, Short.MAX_VALUE)
                            .addComponent(pnlDeadline, javax.swing.GroupLayout.PREFERRED_SIZE, 734, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        pnlBaseLayout.setVerticalGroup(
            pnlBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBaseLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(pnlBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtfFeedback, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReconnect))
                .addGap(18, 18, 18)
                .addGroup(pnlBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlAssignment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDeadline, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlBaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(plnGrades, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                    .addComponent(pnlFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 212, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlBase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    
    //Action performed methods:
    /**
     * Gets student csv file selected from file chooser and checks that it is
     * in fact a csv file.
     * @param evt Click "Select student csv file"
     */
    private void btnStudentCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStudentCSVActionPerformed
        
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fc.showOpenDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            //check that file is a csv
            File temp= fc.getSelectedFile();
            if (!(temp.getName().substring(temp.getName().lastIndexOf("."))).equals(".csv")) {
                JOptionPane.showMessageDialog(this, "Incorrect file upload.\nPlease upload a .csv file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                studentPath = temp.getAbsolutePath();
                txtfStudentFilename.setText(studentPath);
            }
        }
    }//GEN-LAST:event_btnStudentCSVActionPerformed
    
    /**
     * Gets questions csv file selected from file chooser and checks that it is
     * in fact a csv file.
     * @param evt Click "Select questions csv file"
     */
    private void btnQuestionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuestionsActionPerformed
        
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fc.showOpenDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            File temp= fc.getSelectedFile();
            if (!(temp.getName().substring(temp.getName().lastIndexOf("."))).equals(".csv")) {
                JOptionPane.showMessageDialog(this, "Incorrect file upload.\nPlease upload a .csv file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                questionsPath = temp.getAbsolutePath();
                txtfQuestionsFilename.setText(questionsPath);
            }
        }
    }//GEN-LAST:event_btnQuestionsActionPerformed
    
    /**
     * Gets query data csv files selected from file chooser and checks that
     * they are in fact a csv file.
     * @param evt Click "Select student csv files"
     */
    private void btnQueryDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQueryDataActionPerformed
        
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.home")));
        fc.setMultiSelectionEnabled(true);
        int result = fc.showOpenDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            File [] temp= fc.getSelectedFiles();
            String [] names = new String [temp.length];
            String [] paths = new String [temp.length];
            String selected="";
            boolean flag=true;
            for (int i=0; i<temp.length; i++) {
                if (!(temp[i].getName().substring(temp[i].getName().lastIndexOf("."))).equals(".csv")) {
                    JOptionPane.showMessageDialog(this, "Incorrect file upload for one or more files.\nPlease upload .csv files.", "Error", JOptionPane.ERROR_MESSAGE);
                    flag=false;
                    break;
                }
                names[i] = temp[i].getName().substring(0, temp[i].getName().lastIndexOf("."));
                paths[i] = temp[i].getAbsolutePath();
                selected+= temp[i].getAbsolutePath()+", ";
            }
            if(flag){
                dataFilePaths = paths;
                dataFileNames = names;
                txtfQueryDataFilename.setText(selected.substring(0, selected.length()-2));
            }
        }
    }//GEN-LAST:event_btnQueryDataActionPerformed
    
    //TODO: check for failed upload and remove all "falsely" created tables and rows
    /**
     * Uploads the selected csv files.
     * @param evt Click "Upload"
     */
    private void btnUploadQueryDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadQueryDataActionPerformed
        
        String response;
        
        if (dataFilePaths!=null) {
            response = lecturer.uploadQueryData(dataFilePaths, dataFileNames);
            if (response.equals(Lecturer.SUCCESS)) {
                JOptionPane.showMessageDialog(this, "Uploaded .csv files succesfully.", "Upload successful", JOptionPane.PLAIN_MESSAGE);
            }
            else{
                JOptionPane.showMessageDialog(this, "The following files were not uploaded.\nFiles: "+response, "Upload unsuccessful", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        //clean up:
        dataFilePaths = null;
        txtfQueryDataFilename.setText("");
        
    }//GEN-LAST:event_btnUploadQueryDataActionPerformed
    
    /**
     * Gets table of student grades in ascending student number order from the
     * database.
     * @param evt Click "Student Number Ascending"
     */
    private void btnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewActionPerformed
        
        String response = lecturer.viewGradeAscStudent();
        if (response.equals(Lecturer.FAIL_CONNECT)) {
            txtaMarks.setText("Error reading grades from Students table");
        } else {
            txtaMarks.setText(response);
        }
        
    }//GEN-LAST:event_btnViewActionPerformed
    
    /**
     * Gets table of student grades in descending grade order from the
     * database.
     * @param evt Click "Grade Descending"
     */
    private void btnGradeDescendingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGradeDescendingActionPerformed
        
        String response = lecturer.viewGradeDescGrade();
        if (response.equals(Lecturer.FAIL_CONNECT)) {
            txtaMarks.setText("Error reading grades from Students table");
        } else {
            txtaMarks.setText(response);
        }
        
    }//GEN-LAST:event_btnGradeDescendingActionPerformed

    private void btnReconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReconnectActionPerformed
        
        try {
            System.out.println("Trying to reconnect...");
            if (lecturer == null) { //An instance of Lecturer was not created on creation of the HomeFrame
                try {
                    lecturer = new Lecturer(); //Try make a new instance of lecturer and connect to DB
                    lecturer.connectToDB();
                    connected = lecturer.isConnected();
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error creating a new lecturer");
                }
            } else {
                lecturer.connectToDB();
                connected = lecturer.isConnected();
            }
        } catch (IOException | ClassNotFoundException ex) {
            connected = false;
        }
        
        if (!connected) {
            JOptionPane.showMessageDialog(this, "Connection to database failed.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }        
        checkConnect();
        
    }//GEN-LAST:event_btnReconnectActionPerformed

    private void btnInfoUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInfoUploadActionPerformed
        
        //Send assignment information to save on server
        
        String response;
        
        try {
            jSpinNoSubmission.commitEdit();
            jSpinNoQuestions.commitEdit();
            jspHH.commitEdit();
            jspMM.commitEdit();
            jspSS.commitEdit();
        } catch (ParseException ex) {
            Logger.getLogger(HomeFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String noSubmissions = ""+(Integer) jSpinNoSubmission.getValue();
        String noQuestions = ""+ (Integer) jSpinNoQuestions.getValue();
        String date = ((JTextField)jdcDate.getDateEditor().getUiComponent()).getText();
        String hour = ""+(Integer) jspHH.getValue();
        String minute = ""+(Integer) jspMM.getValue();
        String second = ""+(Integer) jspSS.getValue();
        
        String time = "";
        if (Integer.parseInt(hour)<10){
            time = "0" + hour + ":";
        }else{
            time = hour +":";
        }
        if (Integer.parseInt(minute)<10){
            time = time + "0" + minute + ":";
        }else{
            time = time + minute + ":";
        }
        if (Integer.parseInt(second)<10){
            time = time + "0" + second;
        }else{
            time = time + second;
        }
        
        
        boolean closedPrac = false;
        
        //get whether the prac is closed or open
        if (rbtnClosedPrac.isSelected()) {
            closedPrac = true;
        } 
        else if(rbtnAssignment.isSelected()){
            closedPrac = false;
        }
        
        if(noSubmissions.equals("") || noQuestions.equals("") || date.equals("") || time.equals("")){
            JOptionPane.showMessageDialog(this, "Please fill in blank fields", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else{
            
            response = lecturer.uploadAssignmentInfo(noSubmissions, noQuestions, date, time, closedPrac);
            if (response.equals(Lecturer.SUCCESS)) {
                JOptionPane.showMessageDialog(this, "Uploaded assignment information succesfully.", "Upload successful", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Assignment information not uploaded.", "Upload unsuccessful", JOptionPane.ERROR_MESSAGE);
            }
            //clean up:
            jSpinNoQuestions.setValue(1);
            jSpinNoSubmission.setValue(1);
            jdcDate.setDate(null);
            jspHH.setValue(0);
            jspMM.setValue(0);
            jspSS.setValue(0);
        }
        
    }//GEN-LAST:event_btnInfoUploadActionPerformed

    private void btnSchemaPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSchemaPathActionPerformed
        
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fc.showOpenDialog(this);
        if (result==JFileChooser.APPROVE_OPTION) {
            File image = fc.getSelectedFile();
            if (image==null || !(image.getName().substring(image.getName().lastIndexOf("."))).equalsIgnoreCase(".jpg")) {
                JOptionPane.showMessageDialog(this, "Incorrect file upload.\nPlease upload a .png image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else {
                schemaPath = image.getAbsolutePath();
                txtfSchemaPath.setText(schemaPath);
            }
        }
        
    }//GEN-LAST:event_btnSchemaPathActionPerformed

    private void btnUploadStudentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadStudentActionPerformed
        
        String response;
        
        if (studentPath!=null) {
            response = lecturer.uploadStudents(studentPath);
            switch (response) {
                case Lecturer.SUCCESS:
                    JOptionPane.showMessageDialog(this, 
                            "Uploaded students csv file succesfully.", 
                            "Upload Successful",JOptionPane.PLAIN_MESSAGE);
                    break;
                case Lecturer.FAIL_INPUT:
                    JOptionPane.showMessageDialog(this, 
                            "Students file was not uploaded.\n"
                            + "Provided csv file format is incorrect.\n\n"
                            + "Expected Format:\n"
                            + "StudentNo\\r\\n\n"
                            + "<student_number_1>\\r\\n\n"
                            + "<student_number_2>\\r\\n\n"
                            + "...", 
                            "Upload Unsuccessful", JOptionPane.ERROR_MESSAGE);
                    break;
                case Lecturer.FAIL_NULL:
                    JOptionPane.showMessageDialog(this, 
                            "Students file was not uploaded.\n"
                            + "Assignment information needs to be entered before\n"
                            + "the students csv file can be saved.", 
                            "Upload Unsuccessful", JOptionPane.ERROR_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, 
                            "Students file was not uploaded.\n"
                            + "Problem connecting to database.", 
                            "Upload Unsuccessful", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        
        //clean up:
        studentPath = null;
        txtfStudentFilename.setText("");
        
    }//GEN-LAST:event_btnUploadStudentActionPerformed

    private void btnUploadQuestionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadQuestionsActionPerformed
        
        String response;
        
        if (questionsPath!=null) {
            response = lecturer.uploadQuestions(questionsPath);
            switch (response) {
                case Lecturer.SUCCESS:
                    JOptionPane.showMessageDialog(this, 
                            "Uploaded questions csv file succesfully.", 
                            "Upload Successful",JOptionPane.PLAIN_MESSAGE);
                    break;
                case Lecturer.FAIL_INPUT:
                    JOptionPane.showMessageDialog(this, 
                            "Questions file was not uploaded.\n"
                            + "Provided csv file format is incorrect.\n\n"
                            + "Expected Format:\n"
                            + "Question,Answer,Difficulty,Type\\r\\n\n"
                            + "\"<question1>\",\"<answer1>\",<difficulty1>,<type1>\\r\\n\n"
                            + "\"<question2>\",\"<answer2>\",<difficulty2>,<type2>\\r\\n\n"
                            + "...," 
                            + "where Type in {select, arithmetic, update}.",
                            "Upload Unsuccessful", JOptionPane.ERROR_MESSAGE);
                    break;
                case Lecturer.FAIL_QUESTIONS:
                    JOptionPane.showMessageDialog(this, "Questions file was not uploaded. Please ensure the following:\n"
                            + "1. There are sufficient type-difficulty pairs.\n"
                            + "2. The assignment information has been uploaded.");
                    break;
                default:
                    JOptionPane.showMessageDialog(this, 
                            "Questions file was not uploaded."
                            + "Problem connecting to database.", 
                            "Upload Unsuccessful", JOptionPane.ERROR_MESSAGE);
                    break;
            }
        }
        
        //clean up:
        questionsPath = null;
        txtfQuestionsFilename.setText("");
        
    }//GEN-LAST:event_btnUploadQuestionsActionPerformed

    private void windowCloser(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowCloser
        if (lecturer!=null) {
            try {
                lecturer.closeConnection();
            } 
            catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Error closing sockets.", "Socket Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
        System.exit(0);
    }//GEN-LAST:event_windowCloser

    private void btnUpdateDeadlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateDeadlineActionPerformed
        
        String response = "";
        
        try {
            jspHH.commitEdit();
            jspMM.commitEdit();
            jspSS.commitEdit();
        } catch (ParseException ex) {
            Logger.getLogger(HomeFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        String studentNo = txtfUDstudentNo.getText();
        String date = ((JTextField)jdcUDdate.getDateEditor().getUiComponent()).getText();
        String hour = ""+(Integer) jspUDhh.getValue();
        String minute = ""+(Integer) jspUDmm.getValue();
        String second = ""+(Integer) jspUDss.getValue();
        String time = "";
        if (Integer.parseInt(hour)<10){
            time = "0" + hour + ":";
        }else{
            time = hour +":";
        }
        if (Integer.parseInt(minute)<10){
            time = time + "0" + minute + ":";
        }else{
            time = time + minute + ":";
        }
        if (Integer.parseInt(second)<10){
            time = time + "0" + second;
        }else{
            time = time + second;
        }
        
        if (!studentNo.equals("")&&studentNo.length()==6&&!date.equals(null))
        {
            response = lecturer.updateStudentDeadline(studentNo,date,time);
            if (response.equals(Lecturer.SUCCESS)) {
                JOptionPane.showMessageDialog(this, "Assignment deadline for "+studentNo+" has been updated.", "Update successful", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Deadline update for "+studentNo+" was unsuccessful.", "Unsuccessful update", JOptionPane.ERROR_MESSAGE);
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Invalid Studnet number or date!","Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    
        //clean up:
        txtfUDstudentNo.setText("");
        jdcUDdate.setDate(null);
        jspUDhh.setValue(0);
        jspUDmm.setValue(0);
        jspUDss.setValue(0);
        
    }//GEN-LAST:event_btnUpdateDeadlineActionPerformed

    private void btnSchemaUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSchemaUploadActionPerformed
        
        //Save schema image onto server
        System.out.println("Schema path: "+schemaPath);
        
        String response;

        if(schemaPath==null){
            JOptionPane.showMessageDialog(this, "Please select a schema image to upload", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else{
            response = lecturer.uploadSchema(schemaPath);
            if (response.equals(Lecturer.SUCCESS)) {
                JOptionPane.showMessageDialog(this, "Uploaded schema succesfully.", "Upload successful", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Schema not uploaded.", "Upload unsuccessful", JOptionPane.ERROR_MESSAGE);
            }
            //clean up:
            txtfSchemaPath.setText("");            
        }
        
        
    }//GEN-LAST:event_btnSchemaUploadActionPerformed

    private void btnResetStudentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetStudentsActionPerformed
        
        //reset the students table
        
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the Students table?", "Warning", JOptionPane.WARNING_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            String response = lecturer.resetStudents();
            if (response.equals(Lecturer.SUCCESS)) {
                JOptionPane.showMessageDialog(this, "Succesfully cleared Students table.", "Reset successful", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Students table was not cleared.", "Reset unsuccessful", JOptionPane.ERROR_MESSAGE);
            }             
        }
        
    }//GEN-LAST:event_btnResetStudentsActionPerformed

    private void btnResetQuestionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetQuestionsActionPerformed
        
        //reset the questions table
        
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the Questions table?", "Warning", JOptionPane.WARNING_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
        String response = lecturer.resetQuestions();
            if (response.equals(Lecturer.SUCCESS)) {
                JOptionPane.showMessageDialog(this, "Succesfully cleared Questions table.", "Reset successful", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Questions table was not cleared.", "Reset unsuccessful", JOptionPane.ERROR_MESSAGE);
            }
        }  
        
    }//GEN-LAST:event_btnResetQuestionsActionPerformed

    private void btnResetQueryDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetQueryDataActionPerformed
       
        //reset the query data tables
        
        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete all Query Data tables?", "Warning", JOptionPane.WARNING_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            String response = lecturer.resetQueryData();
            if (response.equals(Lecturer.SUCCESS)) {
                JOptionPane.showMessageDialog(this, "Succesfully cleared Query Data tables.", "Reset successful", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Query Data tables were not cleared.", "Reset unsuccessful", JOptionPane.ERROR_MESSAGE);
            }
        }   
    }//GEN-LAST:event_btnResetQueryDataActionPerformed

    private void rbtnClosedPracActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnClosedPracActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbtnClosedPracActionPerformed
    
    
    //Main method:
    
    /**
     * Runs the interface.
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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HomeFrame().setVisible(true);
            }
        });
    }

    
    //Interface instance variables:
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGradeDescending;
    private javax.swing.ButtonGroup btnGroupAssignmentType;
    private javax.swing.JButton btnInfoUpload;
    private javax.swing.JButton btnQueryData;
    private javax.swing.JButton btnQuestions;
    private javax.swing.JButton btnReconnect;
    private javax.swing.JButton btnResetQueryData;
    private javax.swing.JButton btnResetQuestions;
    private javax.swing.JButton btnResetStudents;
    private javax.swing.JButton btnSchemaPath;
    private javax.swing.JButton btnSchemaUpload;
    private javax.swing.JButton btnStudentCSV;
    private javax.swing.JButton btnUpdateDeadline;
    private javax.swing.JButton btnUploadQueryData;
    private javax.swing.JButton btnUploadQuestions;
    private javax.swing.JButton btnUploadStudent;
    private javax.swing.JButton btnView;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinNoQuestions;
    private javax.swing.JSpinner jSpinNoSubmission;
    private com.toedter.calendar.JDateChooser jdcDate;
    private com.toedter.calendar.JDateChooser jdcUDdate;
    private javax.swing.JSpinner jspHH;
    private javax.swing.JSpinner jspMM;
    private javax.swing.JSpinner jspSS;
    private javax.swing.JSpinner jspUDhh;
    private javax.swing.JSpinner jspUDmm;
    private javax.swing.JSpinner jspUDss;
    private javax.swing.JLabel lblColon1;
    private javax.swing.JLabel lblColon2;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblNoQuestions;
    private javax.swing.JLabel lblNoSubmissions;
    private javax.swing.JLabel lblPracType;
    private javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblUD;
    private javax.swing.JLabel lblUDdate;
    private javax.swing.JLabel lblUDstudentNo;
    private javax.swing.JLabel lblUDtime;
    private javax.swing.JLabel lblUpload;
    private javax.swing.JLabel lblViewGrades;
    private javax.swing.JPanel plnGrades;
    private javax.swing.JPanel pnlAssignment;
    private javax.swing.JPanel pnlBase;
    private javax.swing.JPanel pnlDeadline;
    private javax.swing.JPanel pnlFiles;
    private javax.swing.JRadioButton rbtnAssignment;
    private javax.swing.JRadioButton rbtnClosedPrac;
    private javax.swing.JTextArea txtaMarks;
    private javax.swing.JTextField txtfFeedback;
    private javax.swing.JTextField txtfQueryDataFilename;
    private javax.swing.JTextField txtfQuestionsFilename;
    private javax.swing.JTextField txtfSchemaPath;
    private javax.swing.JTextField txtfStudentFilename;
    private javax.swing.JTextField txtfUDstudentNo;
    // End of variables declaration//GEN-END:variables

}
