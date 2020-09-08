package com.skyfall.panik;


import android.annotation.SuppressLint;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompatExtras;
import androidx.core.content.pm.PackageInfoCompat;


import androidx.annotation.RequiresApi;

import com.skyfall.panik.CBUtils.ETWS;
import com.skyfall.panik.CBUtils.SmsCbCmasInfo;
import com.skyfall.panik.CBUtils.SmsCbConstants;
import com.skyfall.panik.CBUtils.SmsCbEtwsInfo;
import com.skyfall.panik.CBUtils.SmsCbMessage;
import com.skyfall.panik.misc.CellBroadcastResources;
import com.skyfall.panik.notification.CellBroadcastAlertAudio;
import com.skyfall.panik.notification.CellBroadcastAlertDialog;

import java.util.ArrayList;

public class CellBroadcastAlertService extends Service {

    public static final int[] mVibrationPattern = new int[]{0, 2000, 500, 1000, 500, 1000, 500, 2000, 500, 1000, 500, 1000};

    /** Identifier for getExtra() when adding this object to an Intent. */
    public static final String SMS_CB_MESSAGE_EXTRA =  "com.skyfall.panik..SMS_CB_MESSAGE";

    /** Use the same notification ID for non-emergency alerts. */
    public static final int NOTIFICATION_ID = 1;
    public static final String EXTRA_MESSAGE = "message";
    public static final String MUTE = "mute";
    /**
     * Notification channel containing for non-emergency alerts.
     */
    static final String NOTIFICATION_CHANNEL_NON_EMERGENCY_ALERTS = "broadcastMessagesNonEmergency";

    /**
     * Notification channel for emergency alerts. This is used when users sneak out of the
     * noisy pop-up for a real emergency and get a notification due to not officially acknowledged
     * the alert and want to refer it back later.
     */
    static final String NOTIFICATION_CHANNEL_EMERGENCY_ALERTS = "broadcastMessages";
    private AudioManager mAudioManager;
    private Context mContext;
    private boolean mute;
    private static final String TAG = "CBAlertService";
    /**
     * Notification channel for emergency alerts during voice call. This is used when users in a
     * voice call, emergency alert will be displayed in a notification format rather than playing
     * alert tone.
     */
    static final String NOTIFICATION_CHANNEL_EMERGENCY_ALERTS_IN_VOICECALL = "broadcastMessagesInVoiceCall";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        String action = intent.getAction();
        Log.d(TAG, "onStartCommand: " + action);
        showNewAlert(intent);

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        mAudioManager = (AudioManager)  getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onDestroy() {
        // Stop listening for incoming calls.

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Alert type
     */
    public enum AlertType {
        DEFAULT,
        ETWS_DEFAULT,
        ETWS_EARTHQUAKE,
        ETWS_TSUNAMI,
        TEST,
        AREA,
        INFO,
        OTHER
    }

    private void showNewAlert(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            Log.e(TAG, "received SHOW_NEW_ALERT_ACTION with no extras!");
            return;
        }

        SmsCbMessage cbm = intent.getParcelableExtra(EXTRA_MESSAGE);
        mute = intent.getBooleanExtra(MUTE, false);

        if (cbm == null) {
            Log.e(TAG, "received SHOW_NEW_ALERT_ACTION with no message extra");
            return;
        }

        openEmergencyAlertNotification(cbm, mute);

    }

    static int getETWSMessageClass(int messageId) {
        switch (messageId) {
            case SmsCbConstants.MESSAGE_ID_ETWS_EARTHQUAKE_WARNING:
                return SmsCbEtwsInfo.ETWS_WARNING_TYPE_EARTHQUAKE;

            case SmsCbConstants.MESSAGE_ID_ETWS_EARTHQUAKE_AND_TSUNAMI_WARNING:
                return SmsCbEtwsInfo.ETWS_WARNING_TYPE_EARTHQUAKE_AND_TSUNAMI;

            case SmsCbConstants.MESSAGE_ID_ETWS_TSUNAMI_WARNING:
                return SmsCbEtwsInfo.ETWS_WARNING_TYPE_TSUNAMI;

            case SmsCbConstants.MESSAGE_ID_ETWS_OTHER_EMERGENCY_TYPE:
                return SmsCbEtwsInfo.ETWS_WARNING_TYPE_OTHER_EMERGENCY;

            case SmsCbConstants.MESSAGE_ID_ETWS_TEST_MESSAGE:
                return SmsCbEtwsInfo.ETWS_WARNING_TYPE_TEST_MESSAGE;

            default:
                return SmsCbEtwsInfo.ETWS_WARNING_TYPE_UNKNOWN;
        }
    }

    static int getCmasMessageClass(int messageId) {
        switch (messageId) {
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_PRESIDENTIAL_LEVEL:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_PRESIDENTIAL_LEVEL_LANGUAGE:
                return SmsCbCmasInfo.CMAS_CLASS_PRESIDENTIAL_LEVEL_ALERT;

            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_OBSERVED:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_OBSERVED_LANGUAGE:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_LIKELY:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_IMMEDIATE_LIKELY_LANGUAGE:
                return SmsCbCmasInfo.CMAS_CLASS_EXTREME_THREAT;

            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_OBSERVED:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_OBSERVED_LANGUAGE:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_LIKELY:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXTREME_EXPECTED_LIKELY_LANGUAGE:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_OBSERVED:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_OBSERVED_LANGUAGE:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_LIKELY:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_IMMEDIATE_LIKELY_LANGUAGE:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_OBSERVED:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_OBSERVED_LANGUAGE:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_LIKELY:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_SEVERE_EXPECTED_LIKELY_LANGUAGE:
                return SmsCbCmasInfo.CMAS_CLASS_SEVERE_THREAT;

            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_CHILD_ABDUCTION_EMERGENCY:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_CHILD_ABDUCTION_EMERGENCY_LANGUAGE:
                return SmsCbCmasInfo.CMAS_CLASS_CHILD_ABDUCTION_EMERGENCY;

            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_REQUIRED_MONTHLY_TEST:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_REQUIRED_MONTHLY_TEST_LANGUAGE:
                return SmsCbCmasInfo.CMAS_CLASS_REQUIRED_MONTHLY_TEST;

            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXERCISE:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_EXERCISE_LANGUAGE:
                return SmsCbCmasInfo.CMAS_CLASS_CMAS_EXERCISE;

            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_OPERATOR_DEFINED_USE:
            case SmsCbConstants.MESSAGE_ID_CMAS_ALERT_OPERATOR_DEFINED_USE_LANGUAGE:
                return SmsCbCmasInfo.CMAS_CLASS_OPERATOR_DEFINED_USE;
            case  SmsCbConstants.MESSAGE_ID_CMAS_ALERT_PUBLIC_SAFETY:
                return  SmsCbCmasInfo.CMAS_CLASS_PUBLIC_SAFETY;
            default:

                return SmsCbCmasInfo.CMAS_CLASS_UNKNOWN;
        }
    }

    /* ETWS Test message including header */
    static final byte[] etwsMessageNormal = ETWS.hexStringToBytes("000011001101" + "EA305BAE57CE770C531790E85C716CBF3044573065B930675730" + "9707767A751F30025F37304463FA308C306B5099304830664E0B30553044FF086C178C615E81FF09" +  "0000000000000000000000000000");
    static final byte[] etwsMessageCancel = ETWS.hexStringToBytes("000011001101" + "EA305148307B3069002800310030003A0035" + "00320029306E7DCA602557309707901F5831309253D66D883057307E3059FF086C178C615E81FF09" +   "00000000000000000000000000000000000000000000");
    static final byte[] etwsMessageTest = ETWS.hexStringToBytes("000011031101" + "EA305BAE57CE770C531790E85C716CBF3044" +    "573065B9306757309707300263FA308C306B5099304830664E0B30553044FF086C178C615E81FF09" +   "00000000000000000000000000000000000000000000");
    static final byte[] etwsMessageMax = ETWS.hexStringToBytes("000011001101" + "0074006500730074" +  "0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
    static final byte[] etwsMessageBase = ETWS.hexStringToBytes("000011001101");
    static final byte[] etwsTestBase = ETWS.hexStringToBytes("000011031101");

    static final String PRES_ALERT = "THE PRESIDENT HAS ISSUED AN EMERGENCY ALERT. CHECK LOCAL MEDIA FOR MORE DETAILS";
    static final String EXTREME_ALERT = "FLASH FLOOD WARNING FOR SOUTH COCONINO COUNTY - NORTH CENTRAL ARIZONA UNTIL 415 PM MST";
    static final String SEVERE_ALERT = "SEVERE WEATHER WARNING FOR SOMERSET COUNTY - NEW JERSEY UNTIL 415 PM MST";
    static final String AMBER_ALERT = "AMBER ALERT:Mountain View,CA VEH'07 Blue Honda Civic CA LIC 5ABC123. Check www.amberalert.gov/active.htm, call 858-123-4567, or email mberalert@mountainview.ca.gov for more information.";
    static final String MONTHLY_TEST_ALERT = "This is a test of the emergency alert system. This is only a test. Call (123)456-7890.";
    static final String PUBLIC_SAFETY_MESSAGE = "This is a public safety message.";
    static final String STATE_LOCAL_ALERT = "This is a state/local test message.";


    /**
     * Display an alert message for emergency alerts.
     * @param message the alert to display
     */
    private void openEmergencyAlertNotification(SmsCbMessage message, Boolean mute) {
        // Close dialogs and window shade
        Intent closeDialogs = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(closeDialogs);

        // start audio/vibration/speech service for emergency alerts
        Intent audioIntent = new Intent(this, CellBroadcastAlertAudio.class);
        audioIntent.setAction(CellBroadcastAlertAudio.ACTION_START_ALERT_AUDIO);

        AlertType alertType = AlertType.DEFAULT;
        if (message.isEtwsMessage()) {
            alertType = AlertType.ETWS_DEFAULT;
            if (message.getEtwsWarningInfo() != null) {
                int warningType = message.getEtwsWarningInfo().getWarningType();

                switch (warningType) {
                    case SmsCbEtwsInfo.ETWS_WARNING_TYPE_EARTHQUAKE:
                    case SmsCbEtwsInfo.ETWS_WARNING_TYPE_EARTHQUAKE_AND_TSUNAMI:
                        alertType = AlertType.ETWS_EARTHQUAKE;
                        break;
                    case SmsCbEtwsInfo.ETWS_WARNING_TYPE_TSUNAMI:
                        alertType = AlertType.ETWS_TSUNAMI;
                        break;
                    case SmsCbEtwsInfo.ETWS_WARNING_TYPE_TEST_MESSAGE:
                        alertType = AlertType.TEST;
                        break;
                    case SmsCbEtwsInfo.ETWS_WARNING_TYPE_OTHER_EMERGENCY:
                        alertType = AlertType.OTHER;
                        break;
                }
            }
        }

        audioIntent.putExtra(CellBroadcastAlertAudio.ALERT_AUDIO_TONE_TYPE, alertType);
        audioIntent.putExtra(CellBroadcastAlertAudio.ALERT_AUDIO_VIBRATION_PATTERN_EXTRA, mVibrationPattern);
        audioIntent.putExtra(CellBroadcastAlertAudio.ALERT_AUDIO_OVERRIDE_DND_EXTRA, true);
        audioIntent.putExtra(CellBroadcastAlertAudio.ALERT_AUDIO_SUB_INDEX, -1);
        audioIntent.putExtra(CellBroadcastAlertAudio.ALERT_AUDIO_DURATION,  10);

        if(!mute) {
            startService(audioIntent);
        }

        ArrayList<SmsCbMessage> messageList = new ArrayList<>();
        messageList.add(message);

        Intent alertDialogIntent = createDisplayMessageIntent(this, CellBroadcastAlertDialog.class, messageList);
        alertDialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(alertDialogIntent);
    }



    /**
     * Add the new alert to the notification bar (non-emergency alerts), or launch a
     * high-priority immediate intent for emergency alerts.
     * @param message the alert to display
     */
    //@RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("StringFormatMatches")
    public static void addToNotificationBar(SmsCbMessage message, ArrayList<SmsCbMessage> messageList, Context context, boolean fromSaveState) {
        int channelTitleId = CellBroadcastResources.getDialogTitleResource(context, message);

        CharSequence channelName = context.getText(channelTitleId);
        String messageBody = message.getMessageBody();

        final android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannels(context);

        // Create intent to show the new messages when user selects the notification.
        Intent intent = createDisplayMessageIntent(context, CellBroadcastAlertDialog.class,  messageList);

        intent.putExtra(CellBroadcastAlertDialog.FROM_NOTIFICATION_EXTRA, true);
        intent.putExtra(CellBroadcastAlertDialog.FROM_SAVE_STATE_NOTIFICATION_EXTRA, fromSaveState);

        PendingIntent pi;
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WATCH)) {
            pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        } else {
            pi = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder builder;
        String channelId = NOTIFICATION_CHANNEL_EMERGENCY_ALERTS ;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // use default sound/vibration/lights for non-emergency broadcasts
           builder = new NotificationCompat.Builder(context, channelId)
                            .setSmallIcon(R.drawable.ic_warning_googred)
                            .setTicker(channelName)
                            .setWhen(System.currentTimeMillis())
                            .setCategory(Notification.CATEGORY_SYSTEM)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setColor(context.getResources().getColor(R.color.notification_color))
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setOngoing(false);
        }else{
            // use default sound/vibration/lights for non-emergency broadcasts
            builder =  new NotificationCompat.Builder(context, channelId)
                            .setSmallIcon(R.drawable.ic_warning_googred)
                            .setTicker(channelName)
                            .setWhen(System.currentTimeMillis())
                            .setCategory(Notification.CATEGORY_SYSTEM)
                            .setPriority(Notification.PRIORITY_HIGH)
                            //.setColor(context.getResources().getColor(R.color.notification_color))
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setOngoing(false);
        }

        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WATCH)) {
            builder.setDeleteIntent(pi);
            // FEATURE_WATCH/CWH devices see this as priority
            builder.setVibrate(new long[]{0});
        } else {
            builder.setContentIntent(pi);
            // This will break vibration on FEATURE_WATCH, so use it for anything else
            builder.setDefaults(android.app.Notification.DEFAULT_ALL);
        }

        // increment unread alert count (decremented when user dismisses alert dialog)
        int unreadCount = messageList.size();
        if (unreadCount > 1) {
            // use generic count of unread broadcasts if more than one unread
            builder.setContentTitle(context.getString(R.string.notification_multiple_title));
            builder.setContentText(context.getString(R.string.notification_multiple, unreadCount));
        } else {
            builder.setContentTitle(channelName)
                    .setContentText(messageBody)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(messageBody));
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build());

        Intent audioIntent = new Intent(context, CellBroadcastAlertAudio.class);
        audioIntent.setAction(CellBroadcastAlertAudio.ACTION_START_ALERT_AUDIO);
        audioIntent.putExtra(CellBroadcastAlertAudio.ALERT_AUDIO_TONE_TYPE, AlertType.OTHER);
        context.startService(audioIntent);

    }

    /**
     * Creates the notification channel and registers it with NotificationManager. If a channel
     * with the same ID is already registered, NotificationManager will ignore this call.
     */
    //@RequiresApi(api = Build.VERSION_CODES.O)
    //@RequiresApi(api = Build.VERSION_CODES.O)
    static void createNotificationChannels(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), context.getString(R.string.notification_channel_emergency_alerts));
        Intent ii = new Intent(context.getApplicationContext(), CellBroadcastAlertService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.setBigContentTitle(context.getString(R.string.notification_channel_emergency_alerts));

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_warning_googred);
        mBuilder.setContentTitle(context.getString(R.string.notification_channel_emergency_alerts));
        mBuilder.setPriority(Notification.PRIORITY_MAX);

        NotificationManager mNotificationManager =  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_EMERGENCY_ALERTS,   context.getString(R.string.notification_channel_emergency_alerts), NotificationManager.IMPORTANCE_LOW));
            final  NotificationChannel nonEmergency = new NotificationChannel(NOTIFICATION_CHANNEL_NON_EMERGENCY_ALERTS, context.getString(R.string.notification_channel_broadcast_messages), NotificationManager.IMPORTANCE_DEFAULT);
            nonEmergency.enableVibration(true);
            notificationManager.createNotificationChannel(nonEmergency);

            final NotificationChannel emergencyAlertInVoiceCall = new NotificationChannel(NOTIFICATION_CHANNEL_EMERGENCY_ALERTS_IN_VOICECALL, context.getString(R.string.notification_channel_broadcast_messages_in_voicecall), NotificationManager.IMPORTANCE_HIGH);
            emergencyAlertInVoiceCall.enableVibration(true);
            notificationManager.createNotificationChannel(emergencyAlertInVoiceCall);
        }


    }

    private static Intent createDisplayMessageIntent(Context context, Class intentClass, ArrayList<SmsCbMessage> messageList) {
        // Trigger the list activity to fire up a dialog that shows the received messages
        Intent intent = new Intent(context, intentClass);
        intent.putParcelableArrayListExtra(CellBroadcastAlertService.SMS_CB_MESSAGE_EXTRA, messageList);
        return intent;
    }
}
