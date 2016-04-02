package entity;

import client.AddressPort;
import client.ErrorCodes;
import client.MessageType;
import client.RequesterMsg;
import marshalling.Marshaller;
import marshalling.UnMarshaller;

import javax.naming.SizeLimitExceededException;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;


public class FileServer
{
    public FileServer() throws IOException
    {
        new FileServerThread().start();
    }
}

