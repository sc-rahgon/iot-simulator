package com.neos.simulator.config;

import com.mongodb.*;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import org.bson.BsonDocument;
import org.bson.Document;
import com.mongodb.connection.SslSettings;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.print.Doc;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MongoDBConfig {
    public static MongoClient createMongoClientSettings() {
        String connectionString = "mongodb://CN=mongodb-1.devneom.com,O=Default Company Ltd,L=Default City,C=XX@mongodb-1.devneom.com:13027,mongodb-2.devneom.com:13027,mongodb-3.devneom.com:13027/?replicaSet=rs-4&tls=true&authMechanism=MONGODB-X509";

        String keystorePath = "/home/opc/certificates/mongo/keystore.jks";
        String truststorePath = "/home/opc/certificates/mongo/truststore.jks";
        String keystorePassword = "Scry@123";
        String truststorePassword = "Scry@123";
        System.setProperty("javax.net.ssl.keyStore", keystorePath);
        System.setProperty("javax.net.ssl.keyStorePassword", keystorePassword);
        System.setProperty("javax.net.ssl.trustStore", truststorePath);
        System.setProperty("javax.net.ssl.trustStorePassword", truststorePassword);

        MongoClientURI uri = new MongoClientURI(connectionString);
        return new MongoClient(uri);

    }

    public static void main(String[] args) {
        MongoDBConfig mongoDBConfig = new MongoDBConfig();
        MongoClient mongoClient = mongoDBConfig.createMongoClientSettings();
        System.out.println("Databases:");
        MongoIterable<String> db = mongoClient.listDatabaseNames();
        for (String dbName : db) {
            System.out.println(dbName);
        }
    }
}
