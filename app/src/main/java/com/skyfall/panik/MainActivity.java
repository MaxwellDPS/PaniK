package com.skyfall.panik;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
  /*  private static int getSerialNumber() {
        int messageId = 0;
        int newMessageId = (messageId + 1) % 65536;
        return messageId;
    }

    static SmsCbMessage createCmasSmsMessage(int serviceCategory,  String language, String body, int severity, int urgency, int certainty, int priority) {
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


    public static void testSendCmasPresAlert (CellBroadcastAlertService CBS, Context context) {
        SmsCbMessage cbMessage =
                createCmasSmsMessage(SmsCbConstants.MESSAGE_ID_CMAS_ALERT_PRESIDENTIAL_LEVEL,
                        "en",
                        CBS.PRES_ALERT,
                        SmsCbCmasInfo.CMAS_SEVERITY_EXTREME,
                        SmsCbCmasInfo.CMAS_URGENCY_EXPECTED,
                        SmsCbCmasInfo.CMAS_CERTAINTY_LIKELY,
                        SmsCbMessage.MESSAGE_PRIORITY_EMERGENCY);
        CBS.openEmergencyAlertNotification(cbMessage, context);
    }

    public static void testSendCmasExtremeAlert (CellBroadcastAlertService CBS, Context context) {
        SmsCbMessage cbMessage =
                createCmasSmsMessage(SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_OBSERVED,
                        "en",
                        CBS.EXTREME_ALERT,
                        SmsCbCmasInfo.CMAS_SEVERITY_EXTREME,
                        SmsCbCmasInfo.CMAS_URGENCY_EXPECTED,
                        SmsCbCmasInfo.CMAS_CERTAINTY_OBSERVED,
                        SmsCbMessage.MESSAGE_PRIORITY_EMERGENCY);

        CBS.openEmergencyAlertNotification(cbMessage, context);
    }

    public static void testSendCmasSevereAlert (CellBroadcastAlertService CBS, Context context) {
        SmsCbMessage cbMessage =
                createCmasSmsMessage(SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_OBSERVED,
                        "en",
                        CBS.SEVERE_ALERT,
                        SmsCbCmasInfo.CMAS_SEVERITY_SEVERE,
                        SmsCbCmasInfo.CMAS_URGENCY_IMMEDIATE,
                        SmsCbCmasInfo.CMAS_CERTAINTY_LIKELY,
                        SmsCbMessage.MESSAGE_PRIORITY_EMERGENCY);

        CBS.openEmergencyAlertNotification(cbMessage, context);
    }

    public static void testSendCmasAmberAlert (CellBroadcastAlertService CBS, Context context) {
        SmsCbMessage cbMessage =
                createCmasSmsMessage(SmsCbConstants.MESSAGE_ID_CMAS_ALERT_CHILD_ABDUCTION_EMERGENCY,
                        "en",
                        CBS.AMBER_ALERT,
                        SmsCbCmasInfo.CMAS_SEVERITY_UNKNOWN,
                        SmsCbCmasInfo.CMAS_URGENCY_UNKNOWN,
                        SmsCbCmasInfo.CMAS_CERTAINTY_UNKNOWN,
                        SmsCbMessage.MESSAGE_PRIORITY_EMERGENCY);

        CBS.openEmergencyAlertNotification(cbMessage, context);
    }

    public static void testSendCmasMonthlyTest (CellBroadcastAlertService CBS, Context context) {
        SmsCbMessage cbMessage =
                createCmasSmsMessage(SmsCbConstants.MESSAGE_ID_CMAS_ALERT_REQUIRED_MONTHLY_TEST,
                        "en",
                        CBS.MONTHLY_TEST_ALERT,
                        SmsCbCmasInfo.CMAS_SEVERITY_UNKNOWN,
                        SmsCbCmasInfo.CMAS_URGENCY_UNKNOWN,
                        SmsCbCmasInfo.CMAS_CERTAINTY_UNKNOWN,
                        SmsCbMessage.MESSAGE_PRIORITY_EMERGENCY);

        CBS.openEmergencyAlertNotification(cbMessage, context);
    }

    public static void testSendCmasExerciseTest (CellBroadcastAlertService CBS, Context context) {
        SmsCbMessage cbMessage =
                createCmasSmsMessage(SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXERCISE,
                        "en",
                        CBS.MONTHLY_TEST_ALERT,
                        SmsCbCmasInfo.CMAS_SEVERITY_UNKNOWN,
                        SmsCbCmasInfo.CMAS_URGENCY_UNKNOWN,
                        SmsCbCmasInfo.CMAS_CERTAINTY_UNKNOWN,
                        SmsCbMessage.MESSAGE_PRIORITY_EMERGENCY);

        CBS.openEmergencyAlertNotification(cbMessage, context);
    }
    ;

    public static void testSendPublicSafetyMessagesAlert (CellBroadcastAlertService CBS, Context context) {
        SmsCbMessage cbMessage =
                createCmasSmsMessage(SmsCbConstants.MESSAGE_ID_CMAS_ALERT_PUBLIC_SAFETY,
                        "en",
                        CBS.PUBLIC_SAFETY_MESSAGE,
                        SmsCbCmasInfo.CMAS_SEVERITY_UNKNOWN,
                        SmsCbCmasInfo.CMAS_URGENCY_UNKNOWN,
                        SmsCbCmasInfo.CMAS_CERTAINTY_UNKNOWN,
                        SmsCbMessage.MESSAGE_PRIORITY_NORMAL);

        CBS.openEmergencyAlertNotification(cbMessage, context);
    }

    public static void testSendStateLocalTestAlert (CellBroadcastAlertService CBS, Context context) {
        SmsCbMessage cbMessage =
                createCmasSmsMessage(SmsCbConstants.MESSAGE_ID_CMAS_ALERT_STATE_LOCAL_TEST,
                        "en",
                        CBS.STATE_LOCAL_ALERT,
                        SmsCbCmasInfo.CMAS_SEVERITY_UNKNOWN,
                        SmsCbCmasInfo.CMAS_URGENCY_UNKNOWN,
                        SmsCbCmasInfo.CMAS_CERTAINTY_UNKNOWN,
                        SmsCbMessage.MESSAGE_PRIORITY_NORMAL);
        CBS.openEmergencyAlertNotification(cbMessage, context);
    }

    public static void createCMAS (CellBroadcastAlertService CBS, Context context, int MessageType ) {
        SmsCbMessage cbMessage =
                createCmasSmsMessage(MessageType,
                        "en",
                        CBS.STATE_LOCAL_ALERT,
                        SmsCbCmasInfo.CMAS_SEVERITY_UNKNOWN,
                        SmsCbCmasInfo.CMAS_URGENCY_UNKNOWN,
                        SmsCbCmasInfo.CMAS_CERTAINTY_UNKNOWN,
                        SmsCbMessage.MESSAGE_PRIORITY_NORMAL);
        CBS.openEmergencyAlertNotification(cbMessage, context);
    }


        public static void testSendEtwsMessageEarthquake(CellBroadcastAlertService CBS, Context context, Activity activity, int serialNumber) {
            SmsCbMessage message = ETWS.createFromPdu(activity, CellBroadcastAlertService.etwsMessageNormal, serialNumber, SmsCbConstants.MESSAGE_ID_ETWS_EARTHQUAKE_WARNING );
            CBS.openEmergencyAlertNotification(message, context);
        }

        public static void testSendEtwsMessageTsunami(CellBroadcastAlertService CBS, Context context, Activity activity, int serialNumber) {
            SmsCbMessage message = ETWS.createFromPdu(activity, CellBroadcastAlertService.etwsMessageNormal, serialNumber, SmsCbConstants.MESSAGE_ID_ETWS_TSUNAMI_WARNING );
            CBS.openEmergencyAlertNotification(message, context);
        }

        public static void testSendEtwsMessageEarthquakeTsunami (CellBroadcastAlertService CBS, Context context, Activity activity, int serialNumber){
            SmsCbMessage message = ETWS.createFromPdu(activity, CellBroadcastAlertService.etwsMessageNormal, serialNumber, SmsCbConstants.MESSAGE_ID_ETWS_EARTHQUAKE_AND_TSUNAMI_WARNING );
            CBS.openEmergencyAlertNotification(message, context);
        }

        public static void testSendEtwsMessageOther(CellBroadcastAlertService CBS, Context context, Activity activity, int serialNumber){
            SmsCbMessage message = ETWS.createFromPdu(activity, CellBroadcastAlertService.etwsMessageNormal, serialNumber, SmsCbConstants.MESSAGE_ID_ETWS_OTHER_EMERGENCY_TYPE );
            CBS.openEmergencyAlertNotification(message, context);
        }

        public static void testSendEtwsMessageCancel(CellBroadcastAlertService CBS, Context context, Activity activity, int serialNumber){
            SmsCbMessage message = ETWS.createFromPdu(activity, CellBroadcastAlertService.etwsMessageCancel, serialNumber, 0);
            CBS.openEmergencyAlertNotification(message, context);
        }

        public static void testSendEtwsMessageTest(CellBroadcastAlertService CBS, Context context, Activity activity, int serialNumber){
            SmsCbMessage message = ETWS.createFromPdu(activity, CellBroadcastAlertService.etwsMessageTest, serialNumber, SmsCbConstants.MESSAGE_ID_ETWS_TEST_MESSAGE);
            CBS.openEmergencyAlertNotification(message, context);
        }

*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
        /*
        CellBroadcastAlertService CBS = new CellBroadcastAlertService();

*/ /*
        Button GsmCmasPresAlertButton = findViewById(R.id.button_gsm_cmas_pres_alert);
        GsmCmasPresAlertButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Context context = getApplicationContext();
                        testSendCmasPresAlert(CBS, context);
                    }
                });


        Button GsmCmasExtremeAlertButton = findViewById(R.id.button_gsm_cmas_extreme_alert);
        GsmCmasExtremeAlertButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Context context = getApplicationContext();
                        testSendCmasExtremeAlert(CBS, context);
                    }
                });


        Button GsmCmasSevereAlertButton = findViewById(R.id.button_gsm_cmas_severe_alert);
        GsmCmasSevereAlertButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Context context = getApplicationContext();
                        testSendCmasSevereAlert(CBS, context);
                    }
                });


        Button GsmCmasAmberAlertButton = findViewById(R.id.button_gsm_cmas_amber_alert);
        GsmCmasAmberAlertButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Context context = getApplicationContext();
                        testSendCmasAmberAlert(CBS, context);
                    }
                });


        Button GsmCmasMonthlyTestButton = findViewById(R.id.button_gsm_cmas_monthly_test);
        GsmCmasMonthlyTestButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Context context = getApplicationContext();
                        testSendCmasMonthlyTest(CBS, context);
                    }
                });


        Button GsmCmasExerciseTestButton = findViewById(R.id.button_gsm_cmas_exercise_test);
        GsmCmasExerciseTestButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Context context = getApplicationContext();
                        testSendCmasExerciseTest(CBS, context);
                    }
                });


        Button GsmPublicSafetyMessagesAlertTestButton = findViewById(R.id.button_gsm_public_safety_message);
        GsmPublicSafetyMessagesAlertTestButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Context context = getApplicationContext();
                        testSendPublicSafetyMessagesAlert(CBS,  context);
                    }
                });

        Button GsmStateLocalTestAlertButton = findViewById(R.id.button_gsm_state_local_test_alert);
        GsmStateLocalTestAlertButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        Context context = getApplicationContext();
                        testSendStateLocalTestAlert(CBS, context);
                    }
                });


        Button etwsEarthquakeTypeButton = (Button) findViewById(R.id.button_etws_earthquake_type);
        etwsEarthquakeTypeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    testSendEtwsMessageEarthquake(CBS, getApplicationContext(),MainActivity.this, getSerialNumber());
            }
        });


        Button etwsTsunamiTypeButton = (Button) findViewById(R.id.button_etws_tsunami_type);
        etwsTsunamiTypeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                testSendEtwsMessageTsunami(CBS, getApplicationContext(),MainActivity.this, getSerialNumber());
            }
        });


        Button etwsEarthquakeTsunamiTypeButton = (Button)
                findViewById(R.id.button_etws_earthquake_tsunami_type);
        etwsEarthquakeTsunamiTypeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    testSendEtwsMessageEarthquakeTsunami(CBS, getApplicationContext(),MainActivity.this, getSerialNumber());
            }
        });


        Button etwsOtherTypeButton = (Button) findViewById(R.id.button_etws_other_type);
        etwsOtherTypeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    testSendEtwsMessageOther(CBS, getApplicationContext(),MainActivity.this, getSerialNumber());
            }
        });


        Button etwsCancelTypeButton = (Button) findViewById(R.id.button_etws_cancel_type);
        etwsCancelTypeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    testSendEtwsMessageCancel(CBS, getApplicationContext(),MainActivity.this, getSerialNumber());
            }
        });


        Button etwsTestTypeButton = (Button) findViewById(R.id.button_etws_test_type);
        etwsTestTypeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    testSendEtwsMessageTest(CBS, getApplicationContext(),MainActivity.this, getSerialNumber());
            }
        });
*/

}
