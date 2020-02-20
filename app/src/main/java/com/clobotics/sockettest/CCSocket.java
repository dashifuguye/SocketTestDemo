package com.clobotics.sockettest;

import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Aya
 * @date: 2019/12/26
 * @decription:
 */
public class CCSocket {
    private String TAG = "CCSocket";
    private static CCSocket mCCSocket;
    private static OutputStream os = null;
    private ServerSocket serverSocket = null;
    private boolean isEnabled;
    private final ExecutorService threadPool;//线程池

    private  int port;

    public static CCSocket getInstance() {
        if (mCCSocket == null) {
            mCCSocket = new CCSocket();
        }
        return mCCSocket;
    }

    private CCSocket() {
        threadPool = Executors.newCachedThreadPool();
    }

    public void startListen(int port){
       this.port = port;
        isEnabled = true;
        new Thread() {
            public void run() {
                serverSocketThread();
            }
        }.start();
    }

    public void stopListen() {
        if (serverSocket != null) {
            try {
                isEnabled = false;
                serverSocket.close();
                serverSocket = null;
                threadPool.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void serverSocketThread() {
        try {
            if (serverSocket != null) {
                return;
            }
            serverSocket = new ServerSocket(port);
            while (isEnabled) {
                final Socket socket = serverSocket.accept();
                socket.setSoTimeout(30000);
                threadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            InputStream is = null;
                            try {
                                is = socket.getInputStream();
                                os = socket.getOutputStream();
                                byte[] b = new byte[5];
                                int len = 0;
                                boolean isReadLen = true;
                                while (isEnabled && (len = is.read(b)) != -1) {
                                    if (isReadLen) {
                                        if (b[0]=='$'){
                                            byte[] lenBytes = new byte[4];
                                            System.arraycopy(b, 1, lenBytes, 0, 4);
                                            int ll = byteArrayToInt(lenBytes);
                                            Log.d(TAG,"接受数据长度:" + ll);
                                            b = new byte[ll];
                                            isReadLen = false;
                                        }
                                    } else {
                                        isReadLen = true;
                                        String msg = new String(b, 0, len, "UTF-8");
                                        Log.d(TAG,"接受到内容:" + msg+",len:"+msg.length());
                                        if(!TextUtils.isEmpty(msg.trim())) {
                                            callProcessedMethod(msg);
                                        }
                                        b = new byte[5];
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "SocketError" + "发生异常:" + e.getMessage());
                            } finally {
                                if (is != null) {
                                    is.close();
                                }
                                if (os != null) {
                                    os.flush();
                                    os.close();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callProcessedMethod(final String msg) {
        Log.d(TAG, "ReceiveMessage:" + msg);
        EventBus.getDefault().post(new ReceivedMsgEvent(msg));
    }

    public void sendUP2Message(final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (os != null) {
                    try {
                        byte[] b = msg.getBytes("UTF-8");
                        byte[] lenBytes = intToByteArray(b.length);
                        byte[] sendBytes = new byte[b.length + 5];
                        sendBytes[0] = '$';//0位置共1个字节存储消息标识符$
                        System.arraycopy(lenBytes, 0, sendBytes, 1, 4);//1-4位置个共4个字节存储数据长度
                        System.arraycopy(b, 0, sendBytes, 5, b.length);//5位置之后存储数据
                        os.write(sendBytes);
                        Log.d(TAG, "sendUP2Message:" + msg +",len:"+msg.length());
                    } catch (SocketException e){
                        Log.d(TAG,"SocketException:"+e.toString());
                    }catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //byte 数组与 int 的相互转换
    private static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    private static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }


    static boolean isStartThread = false;
    static Socket socket1;

    public static void main(String[] args) throws IOException {
        System.out.println("任意字符, 回车键发送Toast");
        socket1 = new Socket("127.0.0.1", 8000);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String msg = scanner.next();
            sendToast(msg);
        }
    }

    public static void sendToast(String msg) throws IOException {

        byte[] b = msg.getBytes("UTF-8");
        byte[] lenBytes = intToByteArray(b.length);
        byte[] sendBytes = new byte[b.length + 5];
        sendBytes[0] = '$';//0位置共1个字节存储消息标识符$
        System.arraycopy(lenBytes, 0, sendBytes, 1, 4);//1-4位置个共4个字节存储数据长度
        System.arraycopy(b, 0, sendBytes, 5, b.length);//5位置之后存储数据

        OutputStream dos = socket1.getOutputStream();
        String aa = new String(sendBytes,"UTF-8");
        System.out.println("发送内容:" + aa+",len:"+aa.length());
        dos.write(sendBytes);
        if (!isStartThread) {
            isStartThread = true;
            testStart(socket1);
        }
    }

    public static void testStart(final Socket socket) {
        new Thread() {
            public void run() {
                try {
                    System.out.println("等待消息传递");
                    InputStream is = socket.getInputStream();
                    byte[] b = new byte[5];
                    int len = 0;
                    boolean isReadLen = true;
                    while ((len = is.read(b)) != -1) {
                        if (isReadLen) {
                            if (b[0]=='$'){
                                byte[] lenBytes = new byte[4];
                                System.arraycopy(b, 1, lenBytes, 0, 4);
                                int ll = byteArrayToInt(lenBytes);
                                b = new byte[ll];
                                isReadLen = false;
                            }
                        } else {
                            isReadLen = true;
                            String msg = new String(b, 0, len);
                            System.out.println("接受到内容:" + msg+",len:"+msg.length());
                            sendToast(msg);
                            b = new byte[5];
                        }
                    }
                    System.out.println("消息传递结束");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
