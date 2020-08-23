package com.skyfall.panik;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class userActivity extends Activity{

        private static int getSerialNumber() {
            int messageId = 0;
            int newMessageId = (messageId + 1) % 65536;
            return messageId;
        }

        static SmsCbMessage createCmasSmsMessage(int serviceCategory,  String body, int priority ) {
            int messageClass = CellBroadcastAlertService.getCmasMessageClass(serviceCategory);
            if (priority == 121){
                priority = SmsCbMessage.MESSAGE_PRIORITY_EMERGENCY;
            }
            SmsCbCmasInfo cmasInfo =
                    new SmsCbCmasInfo(
                            messageClass,
                            SmsCbCmasInfo.CMAS_CATEGORY_OTHER,
                            SmsCbCmasInfo.CMAS_RESPONSE_TYPE_ASSESS,
                            SmsCbCmasInfo.CMAS_SEVERITY_UNKNOWN,
                            SmsCbCmasInfo.CMAS_URGENCY_UNKNOWN,
                            SmsCbCmasInfo.CMAS_CERTAINTY_UNKNOWN );
            SmsCbMessage msg = new SmsCbMessage(SmsCbMessage.MESSAGE_FORMAT_3GPP,0, getSerialNumber(),new SmsCbLocation("1234"),serviceCategory, "en", body,priority,null, cmasInfo, 0,0);
            return msg;
        }

    static SmsCbMessage createETWSMessage(Activity activity, byte[] Msg, int type) {
        SmsCbMessage message = ETWS.createFromPdu(activity, Msg, getSerialNumber(), type );
       return message;

    }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.user_def_alert);
            CellBroadcastAlertService CBS = new CellBroadcastAlertService();

            String[] CMASItems = new String[]{"Presidential alert",
                    "Emergency alert: Extreme",  "Emergency alert: Severe",
                    "Child abduction (Amber alert)", "Public safety message",
                    "State/Local test" , "Required Monthly Test",
                    "Emergency alert (exercise)","Emergency alert (operator)", "Broadcast message" };

            String[] ETWSItems = new String[]{"Earthquake warning", "Tsunami warning", "Earthquake and Tsunami Warning", "ETWS Test Message", "ETWS Other Message"};


            RadioGroup alertType = (RadioGroup)findViewById(R.id.alertType);
            EditText body = findViewById(R.id.editTextTextMultiLine);


            ArrayAdapter adapterCMAS = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CMASItems);
            ArrayAdapter adapterETWS = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ETWSItems);

            final int[] type = {0};

            final Spinner[] dropdown = {findViewById(R.id.spinner)};
            dropdown[0].setAdapter(adapterCMAS);
            alertType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    View radioButton = alertType.findViewById(checkedId);
                    int index = alertType.indexOfChild(radioButton);
                    switch (index) {
                        case 0:
                            dropdown[0].setAdapter(adapterCMAS);
                            type[0] = 0;
                            break;
                        case 1:
                            dropdown[0].setAdapter(adapterETWS);
                            type[0] = 1;
                           break;
                    }

                }
            });

            final int[] messageType = {SmsCbConstants.MESSAGE_ID_CMAS_ALERT_REQUIRED_MONTHLY_TEST, SmsCbConstants.MESSAGE_ID_ETWS_TSUNAMI_WARNING};
            final int[] PriType = {SmsCbMessage.MESSAGE_PRIORITY_EMERGENCY};
            dropdown[0].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            messageType[0] = SmsCbConstants.MESSAGE_ID_CMAS_ALERT_PRESIDENTIAL_LEVEL;
                            messageType[1] = SmsCbConstants.MESSAGE_ID_ETWS_EARTHQUAKE_WARNING;
                            break;
                        case 1:
                            messageType[0] = SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_OBSERVED;
                            messageType[1] = SmsCbConstants.MESSAGE_ID_ETWS_TSUNAMI_WARNING;
                            break;
                        case 2:
                            messageType[0] = SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_OBSERVED;
                            messageType[1] = SmsCbConstants.MESSAGE_ID_ETWS_EARTHQUAKE_AND_TSUNAMI_WARNING;
                            break;
                        case 3:
                            messageType[0] = SmsCbConstants.MESSAGE_ID_CMAS_ALERT_CHILD_ABDUCTION_EMERGENCY;
                            messageType[1] = SmsCbConstants.MESSAGE_ID_ETWS_TEST_MESSAGE;
                            break;
                        case 4:
                            messageType[0] = SmsCbConstants.MESSAGE_ID_CMAS_ALERT_PUBLIC_SAFETY;
                            messageType[1] = SmsCbConstants.MESSAGE_ID_ETWS_OTHER_EMERGENCY_TYPE;
                            break;
                        case 5:
                            messageType[0] = SmsCbConstants.MESSAGE_ID_CMAS_ALERT_STATE_LOCAL_TEST;
                            break;
                        case 6:
                            messageType[0] = SmsCbConstants.MESSAGE_ID_CMAS_ALERT_REQUIRED_MONTHLY_TEST;
                            break;
                        case 7:
                            messageType[0] = SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXERCISE;
                            break;
                        case 8:
                            messageType[0] = SmsCbConstants.MESSAGE_ID_CMAS_ALERT_OPERATOR_DEFINED_USE;
                            break;
                        case 9:
                            messageType[0] = 0;
                            PriType[0] = SmsCbMessage.MESSAGE_PRIORITY_NORMAL;
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    messageType[0] = SmsCbConstants.MESSAGE_ID_CMAS_ALERT_STATE_LOCAL_TEST;
                }

            });

            EditText delay = findViewById(R.id.editTextNumber);

            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch delayCheck = findViewById(R.id.switch1);

            Activity activity = this;

            /* Send Alert. */
            Button fire = findViewById(R.id.Fire);
            fire.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            Context context = getApplicationContext();
                            if (delayCheck.isChecked()){
                                final Handler handler = new Handler();
                                handler.postDelayed(() -> {
                                        onFire(CBS, context, activity, messageType[type[0]], body.getText().toString(), PriType[0], type[0]);
                                }, Integer.parseInt(delay.getText().toString()) * 1000);
                            }else {
                                onFire(CBS, context, activity, messageType[type[0]], body.getText().toString(), PriType[0], type[0]);
                            }
                        }
                    });
        }

    public void onFire(CellBroadcastAlertService CBS, Context context, Activity activity, int serviceCategory,  String body, int priority, int Type) {
        Intent AlertIntent = new Intent(this, CellBroadcastAlertService.class);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch muted = findViewById(R.id.switch_mute);

        AlertIntent.putExtra(CellBroadcastAlertService.MUTE, muted.isChecked());

        if (Type == 0){
            AlertIntent.putExtra(CellBroadcastAlertService.EXTRA_MESSAGE, createCmasSmsMessage(serviceCategory, body, priority));
        }else{
            AlertIntent.putExtra(CellBroadcastAlertService.EXTRA_MESSAGE, createETWSMessage(activity, CellBroadcastAlertService.etwsMessageNormal, serviceCategory));
        }
        startService(AlertIntent);
    }

}
