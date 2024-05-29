package controller;

import Util.TOTPUtil;
import io.jboot.app.JbootApplication;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@RequestMapping("/")
public class IndexController extends JbootController {


    static  String ipAddress="";

    static  String opeAddress="";


    //Create a queue to store the IP of the data requester
    static Queue queue = new LinkedList();

    //The list of data requested by the front end
    static List mesList=new LinkedList();
    static int message=0;

    static String downfile="";

    static ServerSocket ss;

    static int localPort=0;

    //Dynamic TOTP key
    static Integer secret=0;
    //store timestamp
    static Long timestemp=0L;

    private final static Logger logger = Logger.getLogger(IndexController.class);

    synchronized public void index(){
      try {

          mesList.add(0);
          mesList.add("");

          ipAddress=getIPAddress();

          ss = new ServerSocket();

          ss.bind(new InetSocketAddress(ipAddress,0));
          localPort = ss.getLocalPort();
//          System.out.println(ss.toString());
//          System.out.println("open server "+localPort+" port to listen on");

          render("downRes.html");
      }catch(IOException e) {
          logger.info(e);
      }
    }

    synchronized public void downfile() {

       try
       {
           String req="";

           if(req!=""&&req!=null) req=getPara("opeAddress");
//           System.out.println(req);

           if(req!=""&&req!=null){
               queue.offer(req);
               message++;
           }


           if(downfile!=""&&downfile!=null) downfile=getPara("name");
//           System.out.println(downfile);

           if(timestemp!=null) timestemp= Long.valueOf((getPara("timestemp")));
//           System.out.println(timestemp);

           // Seed for HMAC-SHA256 - 32 bytes
           String seed32 = "3132333435363738393031323334353637383930"
                   + "313233343536373839303132";

           long T0 = 0;
           long X = 30;
           String steps = "0";

           long T = (new Date().getTime() - T0) / X;
           steps = Long.toHexString(T).toUpperCase();
           while (steps.length() < 16)
               steps = "0" + steps;

           System.out.println(TOTPUtil.generateTOTP256(seed32,steps,"8"));

           secret=Integer.valueOf(TOTPUtil.generateTOTP256(seed32,steps,"8"));

           render("downRes.html");
       }catch (Exception e)
       {
           logger.info(e);
       }

    }

    /*
    * (Polling) The front-end page sends an ajax request every 5s
    * to check whether there is request data in the back-end
    * */
    synchronized public  List request(){
        try{
            mesList.set(0,message);    //total number of requests
            if(queue.size()>0){
                if(opeAddress==""||opeAddress==null){
                    opeAddress=(String) queue.poll();
                }
            }
            mesList.set(1,opeAddress);   //Requester IP

            mesList.set(2,localPort); //
            return  mesList;
        }catch (Exception e){
            logger.info(e);
            return mesList;
        }
    }

    public static void main(String[] args) throws IOException {
        JbootApplication.run(args);
    }


    synchronized public void downqukuai(){
      try {
          String downname = getPara("name");
          //String fpath = getSession().getServletContext().getRealPath("/upload");
//          System.out.println(downname);
          File file=new File(downname);
          if (file.exists()) {
//              System.out.println(file.length());
//              System.out.println("download successful");
              renderFile(file);
          } else {
              renderJson("File does not exist,download failed");
          }
      }catch (Exception e){
          logger.info(e);
      }
    }


    /*
    * The main code for sending data,
    * using a dynamic key to encrypt data when sending data
    * */
    synchronized public void send(){
        try {
//            System.out.println(opeAddress);
//            System.out.println(downfile);
            if(opeAddress.equals("127.0.0.1")){
//                System.out.println("==");
                opeAddress="192.168.32.1";
            }else{
//                System.out.println("!=");
            }

            try {

                for(;;) {

                    Socket sock=ss.accept();

                    InetAddress addr=sock.getInetAddress();
//                    System.out.println("connection from "+addr.getHostName()+"\n"+"ip:"+addr.getHostAddress());


                    try (
                            InputStream in = new BufferedInputStream(new FileInputStream(downfile));
                            OutputStream out = sock.getOutputStream();
                    )
                    {

                        int data=-1;
                        int totalBytes=0;
                        while ((data=in.read())>-1){
                            totalBytes+=data;
                            out.write(data+secret);
                        }
                        int bytesSent = totalBytes;

//                        System.out.println(String.format("%d bytes sent.", bytesSent));
                    }

                    sock.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            message--;
            opeAddress="";     //After the request is answered, delete the data
            renderText("File transferred successfully");
        }catch (Exception e){
            logger.info(e);
        }
    }

    synchronized public void refuse(){

       try{
           message--;
           opeAddress="";     //After the request is answered, delete the data

//           System.out.println("Refuse to transfer files");
           renderText("refused");
       }catch (Exception e){
           logger.info(e);
       }
    }
}