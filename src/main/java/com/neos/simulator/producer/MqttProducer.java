/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.neos.simulator.producer;

import java.io.FileInputStream;
import java.io.FileReader;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


/**
 * Un produttore MQTT invia eventi json al broker MQTT specificato nella configurazione.
 * L'esempio seguente mostra una configurazione di esempio che invia eventi JSON a un broker MQTT in esecuzione localmente in ascolto sulla porta MQTT predefinita.
 * L'esempio include anche i due campi opzionali: nome utente e password
 * {
 * "type": "mqtt",
 * "broker.server": "tcp://localhost",
 * "broker.port": 1883,
 * "topic": "/logevent",
 * "clientId": "LogEvent",
 * "qos": 2,
 * "username": "whoami",
 * "password": "whatsmypassword"
 * }
 * Il produttore MQTT supporta la configurazione specifica del passaggio per QOS e argomento.
 * L'intera configurazione e ogni elemento in essa contenuto sono opzionali.
 * Aggiungi un elemento "mqtt" alla mappa "producerConfig":
 * <p>
 * "mqtt" : {
 * "topic": "/elsewhere",
 * "qos": 1
 * }
 * Created by ygalblum on 11/24/16.
 */
public class MqttProducer extends EventProducer {
    private static final Logger LOGGER = LogManager.getLogger(MqttProducer.class);

    /* Constants fpr Properties names */
    private static final String PRODUCER_TYPE_NAME = "mqtt";
    private static final String BROKER_SERVER_PROP_NAME = "broker.server";
    private static final String BROKER_PORT_PROP_NAME = "broker.port";
    private static final String TOPIC_PROP_NAME = "topic";
    private static final String CLIENT_ID_PROP_NAME = "clientId";
    private static final String QOS_PROP_NAME = "qos";
    private static final String USERNAME_PROP_NAME = "username";
    private static final String PASSWORD_PROP_NAME = "password";
    private static final String IS_SSL_ACTIVE = "isSslActive";

    /* Constants for default values */
    private static final String DEFAULT_CLIENT_ID = "JsonGenerator";
    private static final int DEFAULT_QOS = 2;

    /* Instance properties */
    private final MqttClient mqttClient;
    private final String topic;
    private final int qos;

    public MqttProducer(Map<String, Object> props) throws MqttException {
        super();
        String brokerHost = (String) props.get(BROKER_SERVER_PROP_NAME);
        Integer brokerPort = (Integer.parseInt((String) props.get(BROKER_PORT_PROP_NAME)));
        String brokerAddress = brokerHost + ":" + brokerPort.toString();

        String clientId = (String) props.get(CLIENT_ID_PROP_NAME);
        String username = (String) props.get(USERNAME_PROP_NAME);
        String password = (String) props.get(PASSWORD_PROP_NAME);

        if (password != null && password.equalsIgnoreCase("access_token")) {
            password = get_access_token(props);
        }

        topic = (String) props.get(TOPIC_PROP_NAME);
        Integer _qos = (Integer) props.get(QOS_PROP_NAME);
        qos = null == _qos ? DEFAULT_QOS : _qos;

        mqttClient = new MqttClient(brokerAddress,
                null == clientId ? DEFAULT_CLIENT_ID : clientId);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        Boolean isSSLActive = (Boolean) props.get(IS_SSL_ACTIVE);
        if (isSSLActive) {
            try {
                connOpts.setSocketFactory(getSocketFactory("/home/opc/newemqx/certs/cacert.pem", "/home/opc/newemqx/certs/cert.pem", "/home/opc/newemqx/certs/key.pem", "Scry@123"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (null != username) {
            connOpts.setUserName(username);
            if (null != password) {
                connOpts.setPassword(password.toCharArray());
            }
        }

        LOGGER.debug("Connecting to broker: " + brokerAddress);
        mqttClient.connect(connOpts);
        LOGGER.debug("Connected - MQTT");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void publish(String event) {
        String _topic = null;
        Integer _qos = null;
//        Object value = producerConfig.get(PRODUCER_TYPE_NAME);
//        if (null != value && Map.class.isAssignableFrom(value.getClass())) {
//            Map<String, Object> config = (Map<String, Object>) value;
        _topic = "test";
        _qos = 2;
        //}
        logEvent(event, null == _topic ? topic : _topic, null == _qos ? qos : _qos);
    }

    @Override
    public void publish(String event, String topic) {
        Integer _qos = null;
        _qos = 2;
        //}
        LOGGER.info("TOPIC FOR MQTT {}", topic);
        logEvent(event, topic, _qos);
    }

    /**
     * @param event the value of event
     * @param qos   the value of qos
     * @param topic the value of topic
     */
    private void logEvent(String event, String topic, int qos) {
        MqttMessage message = new MqttMessage(event.getBytes());
        message.setQos(qos);
        try {
            mqttClient.publish(topic, message);
            LOGGER.debug("Message published");
        } catch (MqttException ex) {
            LOGGER.error("Failed to publish message", ex);
        }
    }

    @Override
    public void stop() {
        if (null != mqttClient) {
            try {
                mqttClient.disconnect();
                System.out.println("Disconnected");
            } catch (MqttException ex) {
                LOGGER.error("Error in disconnect", ex);
            }
        }
    }

    public Logger getLog() {
        return LOGGER;
    }

    public String getProducerTypeName() {
        return PRODUCER_TYPE_NAME;
    }

    public String getBrokerServerPropName() {
        return BROKER_SERVER_PROP_NAME;
    }

    public String getBrokerPortPropName() {
        return BROKER_PORT_PROP_NAME;
    }

    public String getTopicPropName() {
        return TOPIC_PROP_NAME;
    }

    public String getClientIdPropName() {
        return CLIENT_ID_PROP_NAME;
    }

    public String getQosPropName() {
        return QOS_PROP_NAME;
    }

    public String getUsernamePropName() {
        return USERNAME_PROP_NAME;
    }

    public String getPasswordPropName() {
        return PASSWORD_PROP_NAME;
    }

    public String getDefaultClientId() {
        return DEFAULT_CLIENT_ID;
    }

    public int getDefaultQos() {
        return DEFAULT_QOS;
    }

    public MqttClient getMqttClient() {
        return mqttClient;
    }

    public String getTopic() {
        return topic;
    }

    public int getQos() {
        return qos;
    }

    private String get_access_token(Map<String, Object> props) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("client_id", (String) props.get("client_id"));
        map.add("client_secret", (String) props.get("client_secret"));
        map.add("grant_type", (String) props.get("grant_type"));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        String access_token_url = (String) props.get("idp_token_url");
        ResponseEntity<String> response = restTemplate.postForEntity(access_token_url, request, String.class);

        try {
            System.out.println("Access Token Response ---------" + response.getBody());
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(response.getBody());
            System.out.println("jsonObject ---------" + jsonObject);
            if (jsonObject.keySet().contains("access_token")) {
                String access_token = (String) jsonObject.get("access_token");
                System.out.println("access_token ---------" + access_token);
                return access_token;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static SSLSocketFactory getSocketFactory(final String caCrtFile,
                                                    final String crtFile, final String keyFile, final String password)
            throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        // load CA certificate
        LOGGER.info("load ca certificate");
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        FileInputStream is = new FileInputStream(caCrtFile);
        X509Certificate caCertificate = (X509Certificate) fact.generateCertificate(is);

        // load client certificate
        LOGGER.info("load client certificate");
        fact = CertificateFactory.getInstance("X.509");
        is = new FileInputStream(crtFile);
        X509Certificate clientCertificate = (X509Certificate) fact.generateCertificate(is);

        // load client private key
        LOGGER.info("load client key");
        PEMParser pemParser = new PEMParser(new FileReader(keyFile));
        Object object = pemParser.readObject();
        PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder()
                .build(password.toCharArray());
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter()
                .setProvider("BC");
        KeyPair key;
        if (object instanceof PEMEncryptedKeyPair) {
            key = converter.getKeyPair(((PEMEncryptedKeyPair) object)
                    .decryptKeyPair(decProv));
        } else {
            key = converter.getKeyPair((PEMKeyPair) object);
        }
        pemParser.close();

        // CA certificate is used to authenticate server
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        caKs.load(null, null);
        caKs.setCertificateEntry("ca-certificate", caCertificate);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
        tmf.init(caKs);

        // client key and certificates are sent to server so it can authenticate
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("certificate", clientCertificate);
        ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(),
                new java.security.cert.Certificate[]{clientCertificate});
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
                .getDefaultAlgorithm());
        kmf.init(ks, password.toCharArray());

        // finally, create SSL socket factory
        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return context.getSocketFactory();
    }
}


class HttpTrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1)
            throws CertificateException {
        // TODO Auto-generated method stub
    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1)
            throws CertificateException {
        // TODO Auto-generated method stub
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[]{};
    }
}
