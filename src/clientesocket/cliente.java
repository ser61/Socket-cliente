package clientesocket;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import myevents.clientListennerEvent;
import myevents.clientMsgListenner;
import servidor.Paquete;

/**
 *
 * @author Sergio_W
 */
public class cliente extends JFrame implements ActionListener, clientMsgListenner{
    JButton btnConnect;
    JButton btnDesconnect;
    JButton btnenviar;
    JLabel lbEntrada;
    JLabel lbSalida;
    JLabel lbEstado;
    JTextField txtName;
    JTextField txtSend;
    JTextField txtIpAdress;
    JTextField txtPort;
    JTextArea textBox;
    JList list;
    DefaultListModel<String> client; //Para el JList
    ServidorListenner listenner;
    Socket cli;
    String name;

    public cliente(){
        super("Cliente");
        Init();
    }
    
    public void Init(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.lbEstado = new JLabel("ESTADO");
        this.lbEstado.setBounds(100, 10, 200, 30);
        this.lbEstado.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        add(this.lbEstado);
        
        txtIpAdress = new JTextField("127.0.0.1");
        txtIpAdress.setBounds(10, 50, 150, 30);
        add(txtIpAdress);
        
        txtPort = new JTextField("9090");
        txtPort.setBounds(170, 50, 150, 30);
        add(txtPort);
        
        btnConnect = new JButton("Conectar");
        btnConnect.setBounds(10, 90, 150, 30);
        btnConnect.addActionListener(this);
        add(this.btnConnect);
        
        txtName = new JTextField();
        txtName.setBounds(170, 90, 150, 30);
        add(txtName);
        
        this.btnDesconnect = new JButton("Desconectarse...");
        this.btnDesconnect.setBounds(10, 130, 310, 30);
        this.btnDesconnect.addActionListener(this);
        this.btnDesconnect.setEnabled(false);
        add(this.btnDesconnect);
        
        this.lbEntrada = new JLabel("Recepcion de Mensaje:");
        this.lbEntrada.setBounds(10, 170, 200, 30);
        this.lbEntrada.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        add(this.lbEntrada);
        
        textBox = new JTextArea();
        textBox.setBounds(10, 210, 310, 100);
        add(this.textBox);
        
        this.lbSalida = new JLabel("Enviar Mesaje: ");
        this.lbSalida.setBounds(10, 320, 200, 30);
        this.lbSalida.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        add(this.lbSalida);
        
        this.list = new JList();
        this.client = new DefaultListModel();
        this.list.setModel(this.client);
        this.list.setBounds(10, 360, 310, 200);
        add(this.list);
        
        this.txtSend = new JTextField();
        this.txtSend.setBounds(10, 570, 310, 30);
        add(this.txtSend);
        
        btnenviar = new JButton();
        btnenviar.setText("Enviar");
        btnenviar.setBounds(10, 610, 310, 30);
        btnenviar.addActionListener(this);
        btnenviar.setEnabled(false);
        add(btnenviar);
        
        setLayout(null);
        setSize(350, 690);
        setVisible(true);
    }
    
    public void conectar(String ipAdress, int port){
        if (txtName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe Ingresar un Nombre de Cliente!!");
        }else{
            try {
                cli = new Socket(ipAdress, port);
                Paquete nombreCliente = new Paquete(txtName.getText());
                this.name = txtName.getText();
                ObjectOutputStream flujo = new ObjectOutputStream(cli.getOutputStream());
                flujo.writeObject(nombreCliente);
                this.client.addElement("Servidor");
                
                this.listenner = new ServidorListenner(cli);
                this.listenner.addEventListener(this);
                this.listenner.start();
                conexion();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "El Servidor No esta disponible...");
                desconexion();
            }
        }
    }
    
    public void desconectar(){
        try {
            this.listenner.stop();
            this.cli.close();
            desconexion();
        } catch (IOException ex) {
            System.err.println("Error al desconectar: " + ex.getMessage());
        }
        this.btnDesconnect.setEnabled(false);
        this.btnenviar.setEnabled(false);
        this.btnConnect.setEnabled(true);
    }
    
    public void updateLis(LinkedList<String> clients){
        for (String client1 : clients) {
            if (!client1.equalsIgnoreCase(this.name)) {
                this.client.addElement(client1);
            }
        }
    }

    public void sendMessage(int index, String msj){
        if (txtSend.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe Ingresar algun mensaje!!");
        }else{
            try {
                ObjectOutputStream flujo = new ObjectOutputStream(cli.getOutputStream());
                Paquete msg;
                if (index <= 0) {
                    msg = new Paquete(txtSend.getText());
                }else{
                    msg = new Paquete(this.client.get(index), msj);
                    msg.setCliente(this.name);
                }
                flujo.writeObject(msg);
            } catch (IOException ex) {
                System.err.println("No se puede Enviar Mensaje!!!");
            }
        }
    }

    private boolean existClient(String cliente) {
        if (cliente.equalsIgnoreCase(this.name)) {
            return true;
        }
        return false;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnenviar) {
            int index = this.list.getSelectedIndex();
            sendMessage(index, this.txtSend.getText());
            this.txtSend.setText("");
        }else if (e.getSource() == btnConnect) {
            conectar(this.txtIpAdress.getText(), Integer.parseInt(this.txtPort.getText()));
        }else if (e.getSource() == btnDesconnect) {
            desconectar();
        }
    }

    @Override
    public void onRead(clientListennerEvent ev) {
        switch (ev.getMsg().getTipo()) {
            case SERVER:
                this.textBox.append("Servidor: " + ev.getMsg().getText() + "\n");
                break;
            case CLIENT:
                this.textBox.append(ev.getMsg().getCliente() + ": " 
                        + ev.getMsg().getText() + "\n");
                break;
            case LIST:
                updateLis(ev.getMsg().getClients());
                break;
            case ADD:
                if (!existClient(ev.getMsg().getCliente())) {
                    client.addElement(ev.getMsg().getCliente());
                }
                break;
            case DELETE:
                for (int i = 0; i < this.client.size(); i++) {
                    if (this.client.get(i).equalsIgnoreCase(ev.getMsg().getCliente())) {
                        client.remove(i);
                        break;
                    }
                }
                break;
            case EXIST:
                JOptionPane.showMessageDialog(null, "El nombre: " + ev.getMsg().getText() + " ya esta en uso!!");
                this.desconectar();
                break;
            default:
                throw new AssertionError();
        }
    }

    @Override
    public void onDesconnected(clientListennerEvent ev) {
        desconexion();
        this.lbEstado.setText("Conectando.....");
        String ipAdress = this.txtIpAdress.getText();
        int port = Integer.parseInt(this.txtPort.getText());
        boolean b = true;
        while (b) {
            try {
                cli = new Socket(ipAdress, port);
                Paquete nombreCliente = new Paquete(txtName.getText());
                this.name = txtName.getText();
                ObjectOutputStream flujo = new ObjectOutputStream(cli.getOutputStream());
                flujo.writeObject(nombreCliente);
                this.client.addElement("Servidor");
                conexion();
            } catch (IOException ex) {
                //tName()).log(Level.SEVERE, null, ex);
            }

            this.listenner = new ServidorListenner(cli);
            this.listenner.addEventListener(this);
            this.listenner.start();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (cli.isConnected()) {
                b = false;
            }
        }
    }
    
    @Override
    public void onReconnected(clientListennerEvent ev) {
        listenner = new ServidorListenner(cli);
        this.listenner.addEventListener(this);
        this.listenner.start();
        this.client.addElement("Servidor");
        conexion();
    }

    public void conexion(){
        super.setTitle(this.name);
        this.lbEstado.setText("CONECTADO...");
        this.lbEstado.setForeground(Color.GREEN);
        this.btnDesconnect.setEnabled(true);
        this.btnenviar.setEnabled(true);
        this.btnConnect.setEnabled(false);
    }
    
    public void desconexion(){
        super.setTitle("Cliente");
        this.lbEstado.setText("DESCONECTADO!!!");
        this.client.clear();
        this.textBox.setText("");
        this.lbEstado.setForeground(Color.RED);
        this.btnConnect.setEnabled(true);
        this.btnDesconnect.setEnabled(false);
        this.btnenviar.setEnabled(false);
    }
}
