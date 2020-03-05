package com.clobotics.sockettest;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.startListen)
    Button startListen;
    @InjectView(R.id.stopListen)
    Button stopListen;
    @InjectView(R.id.receivedMsg)
    TextView receivedMsg;
    @InjectView(R.id.autoGroup)
    Button autoGroup;
    @InjectView(R.id.moveImg)
    Button moveImg;
    @InjectView(R.id.pack)
    Button pack;
    @InjectView(R.id.backup)
    Button backup;
    @InjectView(R.id.getStatus)
    Button getStatus;
    @InjectView(R.id.getTurbine)
    Button getTurbine;
    @InjectView(R.id.getImage)
    Button getImage;
    @InjectView(R.id.confirmTurbine)
    Button confirmTurbine;
    @InjectView(R.id.powerOffCC)
    Button powerOffCC;
    @InjectView(R.id.port_et)
    EditText portEt;
    @InjectView(R.id.customMsg)
    Button customMsg;
    @InjectView(R.id.farm_id_et)
    EditText farmIdEt;
    @InjectView(R.id.turbine_num_et)
    EditText turbineNumEt;
    @InjectView(R.id.turbine_info_input_layout)
    LinearLayout turbineInfoInputLayout;
    @InjectView(R.id.period_et)
    EditText periodEt;
    @InjectView(R.id.heartbeatMsg)
    TextView heartbeatMsg;
    @InjectView(R.id.imageView)
    ImageView imageView;
    private Timer getStatusTimer;
    private GetTurbineResult getTurbineResult;
    private int statusPeriod = 1000;
    private int port = 18000;
    private USBReceiver mUsbReceiver;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        receivedMsg.setMovementMethod(ScrollingMovementMethod.getInstance());
        heartbeatMsg.setMovementMethod(ScrollingMovementMethod.getInstance());
        mContext = this;
        //      registerReceiver();
    }

    public void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        mUsbReceiver = new USBReceiver();
        mContext.registerReceiver(mUsbReceiver, filter);
    }

    public void unregisterReceiver() {
        mContext.unregisterReceiver(mUsbReceiver);
    }


    /**
     * 需要手动开启ADB调试对话框
     */
    private void showMissingPermissionDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("请打开USB调试。\n\n可点击\"设置\"-\"开发者选项\"-打开USB调试，并重启APP！");
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //退出app
                finish();
            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //启动应用的设置 来手动开启权限
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                startActivity(intent);
            }
        });
        builder.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUsbConnectStateEventHappen(UsbConnectStateEvent event) {
        if (event.isConnected) {
            if (event.isAdbAvailable) {
                receivedMsg.append("\n" + "usb已连接，adb已打开");
                Toast.makeText(this, "可以通信啦", Toast.LENGTH_LONG).show();
            } else {
                receivedMsg.append("\n" + "adb未打开");
                showMissingPermissionDialog();
            }
        } else {
            receivedMsg.append("\n" + "usb断连");
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivedImageEventHappen(ReceivedImageEvent event) {
        imageView.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(Base64.decode(event.msg, Base64.DEFAULT))
                .into(imageView);
        PicDownload.saveImage("socketTest1.jpg", event.msg, this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivedMsgEventHappen(ReceivedMsgEvent event) {
        String msg = event.msg;
        int requestId = 0;
        try {
            requestId = new Gson().fromJson(msg, Base.class).getRequestId();
        } catch (JsonSyntaxException e) {
            Toast.makeText(this, "数据格式有误", Toast.LENGTH_LONG).show();
        }
        if (requestId == SocketConst.GET_STATUS) {
            //更新消息，并自动滚动下最后一条
            heartbeatMsg.append("\n" + "GET_STATUS: " + msg);
            int offset = heartbeatMsg.getLineCount() * heartbeatMsg.getLineHeight();
            if (offset > heartbeatMsg.getHeight()) {
                heartbeatMsg.scrollTo(0, offset - heartbeatMsg.getHeight());
            }
        } else {
            switch (requestId) {
                case SocketConst.AUTO_GROUP: //自动分组
                    msg = "AUTO_GROUP: " + msg;
                    break;
                case SocketConst.PACK: //打包
                    msg = "PACK: " + msg;
                    break;
                case SocketConst.BACKUP: //同步至U盘
                    msg = "BACKUP: " + msg;

                    break;
                case SocketConst.GET_TURBINE: //获取某个风机的缩略图，即分组结果
                    msg = "GET_TURBINE: " + msg;
                    getTurbineResult = new Gson().fromJson(event.msg, GetTurbineResult.class);
                    break;
                case SocketConst.GET_IMAGE: //获取原图
                    msg = "GET_IMAGE: " + msg;
                    GetImageResult getImageResult = new Gson().fromJson(event.msg, GetImageResult.class);

                    break;
                case SocketConst.CONFIRM_TURBINE: //发送某个风机的分组确认结果
                    msg = "CONFIRM_TURBINE: " + msg;

                    break;

                case SocketConst.POWER_OFF: //关闭机载电脑
                    msg = "POWER_OFF: " + msg;

                    break;
                default:
                    msg = "Other Msg: " + msg;
            }

            //更新消息，并自动滚动下最后一条
            if (requestId != SocketConst.GET_TURBINE){
                receivedMsg.append("\n" + msg);
            }else {
                receivedMsg.append("\n" + "获取缩略图");
            }
            int offset = receivedMsg.getLineCount() * receivedMsg.getLineHeight();
            if (offset > receivedMsg.getHeight()) {
                receivedMsg.scrollTo(0, offset - receivedMsg.getHeight());
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        unregisterReceiver();
        super.onDestroy();
    }

    @OnClick({R.id.startListen, R.id.stopListen, R.id.autoGroup, R.id.moveImg, R.id.pack, R.id.backup,
            R.id.getStatus, R.id.getTurbine, R.id.getImage, R.id.confirmTurbine, R.id.powerOffCC})
    public void onViewClicked(View view) {
        Base msg = new Base();
        switch (view.getId()) {
            case R.id.startListen:
                String portMsg = portEt.getText().toString();
                if (portMsg.length() > 0) {
                    port = Integer.parseInt(portMsg);
                }
                boolean enableAdb = (Settings.Global.getInt(getContentResolver(), Settings.Global.ADB_ENABLED, 0) > 0);//判断adb调试模式是否打开
                if (enableAdb) {
                    boolean connectUsb = (Settings.Global.getInt(getContentResolver(), Settings.Global.USB_MASS_STORAGE_ENABLED, 0) > 0);//判断adb调试模式是否打开
                    if (connectUsb) {
                        CCSocket.getInstance().startListen(port);
                        Toast.makeText(this, "监听端口：" + port, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "connectUsb：" + connectUsb, Toast.LENGTH_LONG).show();
                    }
                } else {
                    startDevelopmentActivity();//跳转到开发者选项界面
                }

                //输入框失去焦点
                portEt.setFocusable(false);
                portEt.setFocusableInTouchMode(true);
                break;
            case R.id.stopListen:
                CCSocket.getInstance().stopListen();
                break;
            case R.id.getStatus:
                String internal = periodEt.getText().toString();
                if (internal.length() > 0) {
                    statusPeriod = Integer.parseInt(internal);
                }
                startTimer();
                Toast.makeText(this, "getStatus周期：" + statusPeriod, Toast.LENGTH_LONG).show();
                //输入框失去焦点
                periodEt.setFocusable(false);
                periodEt.setFocusableInTouchMode(true);
                break;
            case R.id.autoGroup:
                msg.setRequestId(SocketConst.AUTO_GROUP);
                CCSocket.getInstance().sendUP2Message(new Gson().toJson(msg));
                break;
            case R.id.pack:
                msg.setRequestId(SocketConst.PACK);
                CCSocket.getInstance().sendUP2Message(new Gson().toJson(msg));
                break;
            case R.id.backup:
                msg.setRequestId(SocketConst.BACKUP);
                CCSocket.getInstance().sendUP2Message(new Gson().toJson(msg));
                break;
            case R.id.getTurbine:
                //获取缩略图，需要输入
                String farmId = farmIdEt.getText().toString();
                String turbineNum = turbineNumEt.getText().toString();
                if (farmId.length() > 0 && turbineNum.length() > 0) {
                    SendTurbineInfo sendTurbineInfo = new SendTurbineInfo();
                    sendTurbineInfo.setRequestId(SocketConst.GET_TURBINE);
                    TurbineInfo turbineInfo = new TurbineInfo();
                    turbineInfo.setWindFarmId(farmId);
                    turbineInfo.setTurbineName(turbineNum);
                    sendTurbineInfo.setData(turbineInfo);
                    CCSocket.getInstance().sendUP2Message(new Gson().toJson(sendTurbineInfo));
                } else {
                    turbineInfoInputLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "请输入风场id和风机号后再点击获取缩略图", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.getImage:
                if (getTurbineResult != null) {
                    SendImageInfo sendImageInfo = new SendImageInfo();
                    sendImageInfo.setRequestId(SocketConst.GET_IMAGE);
                    ImageInfo imageInfo = new ImageInfo();
                    imageInfo.setWindFarmId(getTurbineResult.getData().getWindFarmId());
                    imageInfo.setTurbineName(getTurbineResult.getData().getTurbineName());
                    if (getTurbineResult.getData().getPaths() != null
                            && getTurbineResult.getData().getPaths().size() > 0
                            && getTurbineResult.getData().getPaths().get(0).getInspections() != null
                            && getTurbineResult.getData().getPaths().get(0).getInspections().size() > 0) {
                        imageInfo.setInspectionTime(getTurbineResult.getData().getPaths().get(0).getInspections().get(0).getInspectionTime());
                        imageInfo.setId(getTurbineResult.getData().getPaths().get(0).getInspections().get(0).getThumbnails().get(0).getId());
                        imageInfo.setName(getTurbineResult.getData().getPaths().get(0).getInspections().get(0).getThumbnails().get(0).getName());
                        imageInfo.setDate(getTurbineResult.getData().getPaths().get(0).getInspections().get(0).getThumbnails().get(0).getDate());
                        sendImageInfo.setData(imageInfo);
                        CCSocket.getInstance().sendUP2Message(new Gson().toJson(sendImageInfo));
                        Toast.makeText(this, "正在获取第一张缩略图的原图", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "风机中没有图片数据！", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "请在获取缩略图之后再获取原图！", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.confirmTurbine:
                if (getTurbineResult != null) {
                    if (getTurbineResult.getData().getPaths() != null
                            && getTurbineResult.getData().getPaths().size() > 0
                            && getTurbineResult.getData().getPaths().get(0).getInspections() != null
                            && getTurbineResult.getData().getPaths().get(0).getInspections().size() > 0) {
                        SendTurbineConfirmed bean = turbineResultToTurbineConfirmed();
                        CCSocket.getInstance().sendUP2Message(new Gson().toJson(bean));
                        Toast.makeText(this, "若一条路径下有多次巡检，则确认第一次巡检为最终结果！", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "风机中没有图片数据！", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "请在获取缩略图之后再确认风机！", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.powerOffCC:
                msg.setRequestId(SocketConst.POWER_OFF);
                CCSocket.getInstance().sendUP2Message(new Gson().toJson(msg));//通知CC关机
                break;
//            case R.id.customMsg:
//                msg.setRequestId(SocketConst.PACK);
//                CCSocket.getInstance().sendUP2Message(new Gson().toJson(msg));
//                break;
//            case R.id.moveImg:
//                msg.setRequestId(SocketConst.PACK);
//                CCSocket.getInstance().sendUP2Message(new Gson().toJson(msg));
//                break;
        }
    }

    /**
     * 打开开发者模式界面,手动开启ADB调试对话框
     */
    private void startDevelopmentActivity() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("请打开USB调试。\n\n可点击\"设置\"-\"开发者选项\"-打开USB调试，并重启APP！");
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //退出app
                finish();
            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //启动应用的设置 来手动开启权限
                try {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                    startActivity(intent);
                } catch (Exception e) {
                    try {
                        ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.DevelopmentSettings");
                        Intent intent = new Intent();
                        intent.setComponent(componentName);
                        intent.setAction("android.intent.action.View");
                        startActivity(intent);
                    } catch (Exception e1) {
                        try {
                            Intent intent = new Intent("com.android.settings.APPLICATION_DEVELOPMENT_SETTINGS");//部分小米手机采用这种方式跳转
                            startActivity(intent);
                        } catch (Exception e2) {

                        }

                    }
                }
            }
        });
        builder.show();

    }


    private SendTurbineConfirmed turbineResultToTurbineConfirmed() {
        String windFarmId = getTurbineResult.getData().getWindFarmId();
        String turbineName = getTurbineResult.getData().getTurbineName();
        List<TurbineConfirmed.PathConfirmed> pathConfirmedList = new ArrayList<>();

        for (int i = 0; i < getTurbineResult.getData().getPaths().size(); i++) {
            GetTurbineResult.Turbine.Path path = getTurbineResult.getData().getPaths().get(i);
            String pathName = path.getPathName();
            List<ImageInfo> imageInfoList = new ArrayList<>();

            ImageInfo imageInfo;
            GetTurbineResult.Turbine.Path.Inspection inspection = path.getInspections().get(0);
            for (int k = 0; k < inspection.getThumbnails().size(); k++) {
                GetTurbineResult.Turbine.Path.Inspection.Image image = inspection.getThumbnails().get(k);
                imageInfo = new ImageInfo();
                imageInfo.setWindFarmId(windFarmId);
                imageInfo.setTurbineName(turbineName);
                imageInfo.setInspectionTime(image.getInspectionTime());
                imageInfo.setId(image.getId());
                imageInfo.setName(image.getName());
                imageInfo.setDate(image.getDate());
                imageInfoList.add(imageInfo);
            }
            TurbineConfirmed.PathConfirmed pathConfirmed = new TurbineConfirmed.PathConfirmed(pathName, imageInfoList);
            pathConfirmedList.add(pathConfirmed);
        }
        TurbineConfirmed turbineConfirmed = new TurbineConfirmed();
        turbineConfirmed.setWindFarmId(windFarmId);
        turbineConfirmed.setTurbineName(turbineName);
        turbineConfirmed.setPaths(pathConfirmedList);

        SendTurbineConfirmed sendTurbineConfirmed = new SendTurbineConfirmed();
        sendTurbineConfirmed.setRequestId(SocketConst.CONFIRM_TURBINE);
        sendTurbineConfirmed.setData(turbineConfirmed);
        return sendTurbineConfirmed;
    }

    /**
     * 定时任务获得进度数据
     */
    private void startTimer() {
        if (getStatusTimer == null) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Base msg = new Base();
                    msg.setRequestId(SocketConst.GET_STATUS);
                    CCSocket.getInstance().sendUP2Message(new Gson().toJson(msg));
                }
            };
            getStatusTimer = new Timer();
            getStatusTimer.schedule(task, 0, statusPeriod);
        } else {
            getStatusTimer.cancel();
            getStatusTimer = null;
        }
    }
}
