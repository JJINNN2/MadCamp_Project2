package com.example.project2_photo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class JavaSocketClient {
    public static void main(String[] args) {
        try {
            int portnumber = 7022;
            Socket socket = new Socket("http://192.249.19.242", portnumber);

            ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
            outstream.writeObject("Hello Android Town");
            outstream.flush();

            ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
            System.out.println(instream.readObject());
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
