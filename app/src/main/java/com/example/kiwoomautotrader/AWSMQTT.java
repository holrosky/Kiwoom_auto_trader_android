package com.example.kiwoomautotrader;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyStore;
import java.util.UUID;

public class AWSMQTT {
    private final String LOG_TAG = AWSMQTT.class.getCanonicalName();

    private final String AWS_IOT_POLICY_NAME = "kiwoom";
    private final String CERTIFICATE_ID = "default";
    private final String COGNITO_POOL_ID = "ap-northeast-2:0ebd5f86-092c-4154-9996-30100cc6ef12";
    private final String CUSTOMER_SPECIFIC_ENDPOINT = "a29ljsynycxctz-ats.iot.ap-northeast-2.amazonaws.com";
    private final String KEYSTORE_NAME = "iot_keystore";
    private final String KEYSTORE_PASSWORD = "password";
    private final Regions MY_REGION = Regions.AP_NORTHEAST_2;

    private KeyStore clientKeyStore = null;
    private String keystorePath;
    private String keystoreName;
    private String keystorePassword;
    private String certificateId;
    private boolean isConnected;

    AWSIotMqttManager mqttManager;
    AWSIotClient mIotAndroidClient;
    String clientId;

    CognitoCachingCredentialsProvider credentialsProvider;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public AWSMQTT(Context context)
    {
        clientId = UUID.randomUUID().toString();

        sharedPreferences = context.getSharedPreferences("sharedPreferences", 0);
        editor = sharedPreferences.edit();

        isConnected = false;

        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                context, // context
                COGNITO_POOL_ID, // Identity Pool ID
                MY_REGION // Region
        );

        Region region = Region.getRegion(MY_REGION);

        // Initialize the AWSIotMqttManager with the configuration
        mqttManager = new AWSIotMqttManager(
                clientId,
                CUSTOMER_SPECIFIC_ENDPOINT);

        mqttManager.setKeepAlive(10);

        // Set Last Will and Testament for MQTT.  On an unclean disconnect (loss of connection)
        // AWS IoT will publish this message to alert other clients.
        AWSIotMqttLastWillAndTestament lwt = new AWSIotMqttLastWillAndTestament("my/lwt/topic",
                "Android client lost connection", AWSIotMqttQos.QOS0);
        mqttManager.setMqttLastWillAndTestament(lwt);

        // IoT Client (for creation of certificate if needed)
        mIotAndroidClient = new AWSIotClient(credentialsProvider);
        mIotAndroidClient.setRegion(region);

        keystorePath = context.getFilesDir().getPath();
        keystoreName = KEYSTORE_NAME;
        keystorePassword = KEYSTORE_PASSWORD;
        certificateId = CERTIFICATE_ID;

        // To load cert/key from keystore on filesystem
        try {
            if (AWSIotKeystoreHelper.isKeystorePresent(keystorePath, keystoreName)) {
                if (AWSIotKeystoreHelper.keystoreContainsAlias(certificateId, keystorePath,
                        keystoreName, keystorePassword)) {
                    Log.i(LOG_TAG, "Certificate " + certificateId
                            + " found in keystore - using for MQTT.");
                    // load keystore from file into memory to pass on connection
                    clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                            keystorePath, keystoreName, keystorePassword);
                } else {
                    Log.i(LOG_TAG, "Key/cert " + certificateId + " not found in keystore.");
                }
            } else {
                Log.i(LOG_TAG, "Keystore " + keystorePath + "/" + keystoreName + " not found.");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "An error occurred retrieving cert/key from keystore.", e);
        }

        if (clientKeyStore == null) {
            Log.i(LOG_TAG, "Cert/key was not found in keystore - creating new key and certificate.");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Create a new private key and certificate. This call
                        // creates both on the server and returns them to the
                        // device.
                        CreateKeysAndCertificateRequest createKeysAndCertificateRequest =
                                new CreateKeysAndCertificateRequest();
                        createKeysAndCertificateRequest.setSetAsActive(true);
                        final CreateKeysAndCertificateResult createKeysAndCertificateResult;
                        createKeysAndCertificateResult =
                                mIotAndroidClient.createKeysAndCertificate(createKeysAndCertificateRequest);
                        Log.i(LOG_TAG,
                                "Cert ID: " +
                                        createKeysAndCertificateResult.getCertificateId() +
                                        " created.");

                        // store in keystore for use in MQTT client
                        // saved as alias "default" so a new certificate isn't
                        // generated each run of this application
                        AWSIotKeystoreHelper.saveCertificateAndPrivateKey(certificateId,
                                createKeysAndCertificateResult.getCertificatePem(),
                                createKeysAndCertificateResult.getKeyPair().getPrivateKey(),
                                keystorePath, keystoreName, keystorePassword);

                        // load keystore from file into memory to pass on
                        // connection
                        clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                                keystorePath, keystoreName, keystorePassword);

                        // Attach a policy to the newly created certificate.
                        // This flow assumes the policy was already created in
                        // AWS IoT and we are now just attaching it to the
                        // certificate.
                        AttachPrincipalPolicyRequest policyAttachRequest =
                                new AttachPrincipalPolicyRequest();
                        policyAttachRequest.setPolicyName(AWS_IOT_POLICY_NAME);
                        policyAttachRequest.setPrincipal(createKeysAndCertificateResult
                                .getCertificateArn());
                        mIotAndroidClient.attachPrincipalPolicy(policyAttachRequest);
                    } catch (Exception e) {
                        Log.e(LOG_TAG,
                                "Exception occurred when generating new private key and certificate.",
                                e);
                    }
                }
            }).start();
        }
    }

    public void connect() {
        try {
            mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
                public void onStatusChanged(final AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus status, final Throwable throwable) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                String str = LOG_TAG;
                                if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connecting) {
                                    MainActivity.setMqttServerStatus("연결중");
                                } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected) {
                                    isConnected = true;
                                    MainActivity.setMqttServerStatus("연결됨");
                                    subscribe();


                                } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Reconnecting) {
                                    MainActivity.setMqttServerStatus("다시 연결중");
                                    if (throwable != null) {
                                        Log.e(LOG_TAG, "Connection error.", throwable);
                                    }
                                } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost) {
                                    isConnected = false;
                                    MainActivity.setMqttServerStatus("연결 끊김");
                                    if (throwable != null) {
                                        Log.e(LOG_TAG, "Connection error.", throwable);
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(LOG_TAG, "Message encoding error.", e);
                            }
                        }
                    });

                }
            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "Connection error.", e);
        }
    }

    public void disconnect()
    {
        mqttManager.disconnect();
    }

    public boolean isConnected()
    {
        return isConnected;
    }

    public void publish(String msg)
    {
        String topic = sharedPreferences.getString("sCode", null) + "/" + sharedPreferences.getString("kiwoom_id", null) + "/"  + sharedPreferences.getString("trade_mode", "test") + "/android";
        mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
    }

    public void subscribe() {
        try {
            mqttManager.subscribeToTopic(sharedPreferences.getString("sCode", null) + "/" + sharedPreferences.getString("kiwoom_id", null) + "/"  + sharedPreferences.getString("trade_mode", "test") + "/#", AWSIotMqttQos.QOS0, new AWSIotMqttNewMessageCallback() {
                public void onMessageArrived(String topic, final byte[] data) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                String message = new String(data, "UTF-8");
                                message = message.replaceAll("'", "\"");
                                JSONObject messageJson = new JSONObject(message);

                                Log.d(LOG_TAG, "Message received: " + message);
                                if (topic.contains("auto_trader"))
                                {
                                    if (messageJson.getString("command").equals("running_state"))
                                    {
                                        MainActivity.resetTraderStatusCount();
                                    }
                                    else if (messageJson.getString("command").equals("json_state_from_auto_trader") || messageJson.getString("command").equals("json_update_from_auto_trader"))
                                    {
                                        Log.e(LOG_TAG, message);

                                        messageJson.remove("command");

                                        JSONArray accArr = messageJson.getJSONArray("acc_num_list");
                                        MainActivity.setAcc(accArr);

                                        editor.putString("strategy_json", messageJson.toString());
                                        editor.commit();

                                        MainActivity.setStrategyList(messageJson);
                                        MainActivity.setAllClear(messageJson.getJSONObject("profit_total_clear"));

                                        Log.d(LOG_TAG, messageJson.toString());
                                    }
                                    else if (messageJson.getString("command").equals("clear_position_from_auto_trader"))
                                    {
                                        if (Integer.valueOf(messageJson.getString("result")) == 0)
                                            Toast.makeText(MainActivity.mContext,"청산 완료!", Toast.LENGTH_SHORT).show();
                                        else if (Integer.valueOf(messageJson.getString("result")) == -1)
                                            Toast.makeText(MainActivity.mContext,"해당 전략으로 진입한 포지션이 없습니다!", Toast.LENGTH_SHORT).show();
                                    }
                                    else if (messageJson.getString("command").equals("remove_result_from_auto_trader"))
                                        MainActivity.strategyRemove(messageJson);

                                    else if (messageJson.getString("command").equals("profit_from_auto_trader"))
                                        MainActivity.setProfit(messageJson);

                                    else if (messageJson.getString("command").equals("length_from_auto_trader")) {
                                        MainActivity.resetTraderStatusCount();
                                        MainActivity.setLengthInfo(messageJson);
                                    }

                                    else if (messageJson.getString("command").equals("clear_all_position_result_from_auto_trader")){
                                        MainActivity.setClearAllResult(messageJson);
                                    }

                                }




                            } catch (Exception e) {
                                Log.e(LOG_TAG, "Message encoding error.", e);
                            }
                        }
                    });

                }
            });
        } catch (Exception e) {
            Log.e(LOG_TAG, "Subscription error.", e);
        }
    }

    public void requestJsonState()
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command", "json_state_require_from_android");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        publish(jsonObject.toString());
    }

}
