package com.clobotics.sockettest.socket;

import android.text.TextUtils;
import android.util.Log;

import com.clobotics.sockettest.event.ReceivedMsgEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Decoder.BASE64Encoder;

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

    private int port;

    public static CCSocket getInstance() {
        if (mCCSocket == null) {
            mCCSocket = new CCSocket();
        }
        return mCCSocket;
    }

    private CCSocket() {
        threadPool = Executors.newCachedThreadPool();
    }

    public void startListen(int port) {
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
            serverSocket = new ServerSocket(SocketConst.SOCKET_PORT);
            while (isEnabled) {
                final Socket socket = serverSocket.accept();
                socket.setSoTimeout(SocketConst.SOCKET_TIMEOUT);
                threadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            InputStream is = null;
                            try {
                                is = socket.getInputStream();
                                os = socket.getOutputStream();
                                byte[] b = new byte[4096];

                                //数据格式：标识符（$,1byte）+ 命令id（requestId,2byte） + sum(2byte) + index(2byte) + data (3072byte）+ 校验位(2byte)
                                //创建一对多数据结构，key 为id，value保存sum index 和data，
                                HashMap<Integer, SocketApiBean> dataMap = new HashMap<>();
                                while (isEnabled && (is.read(b)) != -1) {
                                    if (b[0] == '$') {
                                        byte[] temp = new byte[4];
                                        System.arraycopy(b, 1, temp, 0, 4);
                                        int id = byteArrayToInt(temp);
                                        System.arraycopy(b, 5, temp, 0, 4);
                                        int sum = byteArrayToInt(temp);
                                        System.arraycopy(b, 9, temp, 0, 4);
                                        int index = byteArrayToInt(temp);
                                        String msg = new String(b, 13, 3072, "UTF-8");
                                        if (dataMap.containsKey(id)) {
                                            SocketApiBean bean = dataMap.get(id);
                                            bean.setIndex(index);
                                            bean.setData(bean.getData().append(msg));
                                            dataMap.put(id, bean);
                                        } else {
                                            SocketApiBean bean = new SocketApiBean(sum, index, new StringBuilder(msg));
                                            dataMap.put(id, bean);
                                        }
                                        if (dataMap.get(id).getSum() == dataMap.get(id).getIndex() + 1) {
                                            //表示数据接收完成
                                            callProcessedMethod(dataMap.get(id).getData().toString());
                                        }
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

    public void sendUP2Message(final int requestId, final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (os != null) {
                    try {
                        byte[] idBytes = intToByteArray(requestId);
                        byte[] b = msg.getBytes("UTF-8");
                        int sum = b.length / 3072;
                        if (b.length % 3072 != 0) {
                            sum += 1;
                        }
                        byte[] sumBytes = intToByteArray(sum);
                        byte[] sendBytes = new byte[3089];
                        for (int i = 0; i < sum; i++) {
                            byte[] indexBytes = intToByteArray(i);
                            sendBytes[0] = '$';//0位置共1个字节存储消息标识符$
                            System.arraycopy(idBytes, 0, sendBytes, 1, 4);//1-2位置个共4个字节存储id
                            System.arraycopy(sumBytes, 0, sendBytes, 5, 4);//3-4位置个共4个字节存储sum
                            System.arraycopy(indexBytes, 0, sendBytes, 9, 4);//5-6位置个共4个字节存储index
                            //计算剩余数据长度，若大于3072，则拷贝长度为3072，否则取剩余长度
                            int remainingLen = b.length - i*3072;
                            remainingLen = remainingLen > 3072 ? 3072: remainingLen;
                            System.arraycopy(b, i * 3072, sendBytes, 3085 - remainingLen, remainingLen);//7-3079位置个共3072个字节存储data,长度不足3072则高位留空
                            byte[] checkSumBytes = sumCheck(sendBytes, 4);//校验和
                            System.arraycopy(checkSumBytes, 0, sendBytes, 3085, 4);//3079-3080位置个共2个字节存储校验和
                            os.write(sendBytes);
                            Log.d(TAG, "sendUP2Message:" + i + ",sendBytesMsg:" + new String(sendBytes, "UTF-8"));
                        }
                        Log.d(TAG, "sendUP2Message:" + msg + ",len:" + msg.length());
                    } catch (SocketException e) {
                        Log.d(TAG, "SocketException:" + e.toString());
                    } catch (UnsupportedEncodingException e) {
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

    /**
     * 校验和
     *
     * @param msg    需要计算校验和的byte数组
     * @param length 校验和位数
     * @return 计算出的校验和数组
     */
    private static byte[] sumCheck(byte[] msg, int length) {
        long mSum = 0;
        byte[] mByte = new byte[length];

        /** 逐Byte添加位数和 */
        for (byte byteMsg : msg) {
            long mNum = ((long) byteMsg >= 0) ? (long) byteMsg : ((long) byteMsg + 256);
            mSum += mNum;
        } /** end of for (byte byteMsg : msg) */

        /** 位数和转化为Byte数组 */
        for (int liv_Count = 0; liv_Count < length; liv_Count++) {
            mByte[length - liv_Count - 1] = (byte) (mSum >> (liv_Count * 8) & 0xff);
        } /** end of for (int liv_Count = 0; liv_Count < length; liv_Count++) */

        return mByte;
    }


    static boolean isStartThread = false;
    static Socket socket1;

    public static void main(String[] args) throws IOException {
        socket1 = new Socket("127.0.0.1", 18000);
        System.out.println("任意字符, 回车键发送Toast");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String msg = scanner.next();
            sendToast(msg);
        }


//        sendToast(imageToBase64("C:\\Users\\27253\\Desktop\\sample1.JPG"));
    }

    /**
     * 将图片转换成Base64编码的字符串
     */
    public static String imageToBase64(String path) {
//        if(TextUtils.isEmpty(path)){
//            return null;
//        }
        File file = new File(path);
        System.out.println("数据长度：" + file.length());

        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(file);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            BASE64Encoder encoder = new BASE64Encoder();
            result = encoder.encode(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    public static void sendToast(String msg) throws IOException {

        byte[] b = msg.getBytes("UTF-8");
        byte[] lenBytes = intToByteArray(b.length);
        byte[] sendBytes = new byte[b.length + 5];
        sendBytes[0] = '$';//0位置共1个字节存储消息标识符$
        System.arraycopy(lenBytes, 0, sendBytes, 1, 4);//1-4位置个共4个字节存储数据长度
        System.arraycopy(b, 0, sendBytes, 5, b.length);//5位置之后存储数据

        OutputStream dos = socket1.getOutputStream();
        String aa = new String(sendBytes, "UTF-8");
        System.out.println("发送内容:" + aa + ",len:" + aa.length());
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
                            if (b[0] == '$') {
                                byte[] lenBytes = new byte[4];
                                System.arraycopy(b, 1, lenBytes, 0, 4);
                                int ll = byteArrayToInt(lenBytes);
                                b = new byte[ll];
                                isReadLen = false;
                            }
                        } else {
                            isReadLen = true;
                            String msg = new String(b, 0, len);
                            System.out.println("接受到内容:" + msg + ",len:" + msg.length());
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
