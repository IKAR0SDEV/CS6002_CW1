import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author khali
 */
public class Send extends JFrame {

    private final File[] file = new File[1];
    private JLabel jLabel1;


    public Send() {
        initComponents();
    }

    //GUI components initialization
    private void initComponents() {
        JPanel jPanel1 = new JPanel();
        JButton chooseFileBtn = new JButton();
        JButton sendFileBtn = new JButton();
        jLabel1 = new JLabel();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        chooseFileBtn.setText("Choose txt File");
        chooseFileBtn.addActionListener(this::chooseFileBtnActionPerformed);
        sendFileBtn.setText("Send File");
        sendFileBtn.addActionListener(evt -> {
            try {
                sendFileBtnActionPerformed();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(chooseFileBtn, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sendFileBtn, GroupLayout.PREFERRED_SIZE, 181, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(sendFileBtn, GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                        .addComponent(chooseFileBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setText("                            Choose a txt file to send");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
    }

    //ActionListener for the chooseFileBtn
    private void chooseFileBtnActionPerformed(java.awt.event.ActionEvent ev) {
        //Create a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a text file to send");
        //filter to accept only txt files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        fileChooser.setFileFilter(filter);
        //Accept only text files
        fileChooser.setAcceptAllFileFilterUsed(false);
        //Show the file chooser dialog
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            //Get the file and set it to the file field
            file[0] = fileChooser.getSelectedFile();
            //Set the label to show the file name
            jLabel1.setText("File name is: " + file[0].getName());
        }

    }

    //ActionListener for the sendFileBtn
    private void sendFileBtnActionPerformed() throws IOException {
        //Check if the file is not null, if it is, show an error message
        if (file[0] == null) {
            jLabel1.setText("No file selected, please choose one");
        } else {
            try {
                String username;
                //Create a new socket
                Socket socket = new Socket("localhost", 2003);
                //Create a new input stream
                FileInputStream fis = new FileInputStream(file[0].getAbsolutePath());
                //Create a new output stream to send the file
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                //Get username from text field, if empty show error message
                if (Client.usernameField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter your Username", "Enter Username!", JOptionPane.ERROR_MESSAGE);
                    return;
                } else username = Client.usernameField.getText();
                Client.messageArea.append(">>>" + username + ":\n");
                //Get the file name and store it in the ArrayList
                String fileName = file[0].getName();
                //After converting into bytes send the file name to the server
                byte[] fileNameBytes = fileName.getBytes();
                //Create the array of the size of the file
                byte[] fileContent = new byte[(int) file[0].length()];
                //Read the file and store it in the array
                fis.read(fileContent);
                //Send the file name to the server
                dos.write(fileNameBytes, 0, fileNameBytes.length);
                //Send file content to the server
                dos.write(fileContent , 0, fileContent.length);
                //Close frame after sending the file
                this.dispose();
            } catch (Exception e) {
                this.dispose();
                Client.messageArea.append("Error sending file\n No connection to the server");
            }
        }

    }
}