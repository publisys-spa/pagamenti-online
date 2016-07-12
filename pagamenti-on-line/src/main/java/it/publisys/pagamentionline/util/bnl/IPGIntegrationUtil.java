package it.publisys.pagamentionline.util.bnl;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author mcolucci
 */
public class IPGIntegrationUtil {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd-HH:mm:ss");
    private String fmtDate = dateFormat.format(new Date(System.currentTimeMillis()));
    private String serverURL;
    private String storeId;
    private String ksig;
    private String charge;
    private String currency;
    private String hash;

    private String responseSuccessURL;
    private String responseFailURL;

    public IPGIntegrationUtil(String serverURL, String storeId, String ksig,
                              String charge, String currency) {
        super();
        this.serverURL = serverURL;
        this.storeId = storeId;
        this.ksig = ksig;
        this.charge = charge;
        this.currency = currency;
    }

    public String createHash() {
        String stringToHash = storeId + fmtDate + charge + currency + ksig;
        return calculateHashFromHex(new StringBuffer(stringToHash));
    }

    private String calculateHashFromHex(StringBuffer buffer) {
        String algorithm = "SHA1";
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch (Exception e) {
            throw new IllegalArgumentException("Algorithm '" + algorithm + "' not supported");
        }
        StringBuilder result = new StringBuilder();
        StringBuilder sb = new StringBuilder();

        byte[] bytes = buffer.toString().getBytes();
        for (byte b : bytes) {
            sb.append(Character.forDigit((b & 240) >> 4, 16));
            sb.append(Character.forDigit((b & 15), 16));
        }
        buffer = new StringBuffer(sb.toString());
        messageDigest.update(buffer.toString().getBytes());
        byte[] message = messageDigest.digest();
        for (byte b : message) {
            String apps = Integer.toHexString(b & 0xff);
            if (apps.length() == 1) {
                apps = "0" + apps;
            }
            result.append(apps);
        }
        return result.toString();
    }

    public String getHash() {
        return createHash();
    }

    public String getCharge() {
        return charge;
    }

    public String getKsig() {
        return ksig;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getFormattedSysDate() {
        return fmtDate;
    }

    public String getFmtDate() {
        return fmtDate;
    }

    public String getServerURL() {
        return serverURL;
    }

    public String getCurrency() {
        return currency;
    }

    public String getResponseSuccessURL() {
        return responseSuccessURL;
    }

    public void setResponseSuccessURL(String responseSuccessURL) {
        this.responseSuccessURL = responseSuccessURL;
    }

    public String getResponseFailURL() {
        return responseFailURL;
    }

    public void setResponseFailURL(String responseFailURL) {
        this.responseFailURL = responseFailURL;
    }

}

/*
 [PARAM]approval_code --> N:IGFS_20090
 [PARAM]MYBANK --> 0
 [PARAM]txndate_processed --> 2015-06-09 12:04:24.334
 [PARAM]timezone --> CET
 [PARAM]response_hash --> 6a19b0de2ad9530dbf8139ef44deb6cc9084c3f8
 [PARAM]oid --> PAG-386ce647-167e-42eb-a672-341af81efdf4
 [PARAM]refnumber --> 3060908820980650
 [PARAM]txntype --> PURCHASE
 [PARAM]ERROR_LIST --> MSG_20090
 [PARAM]currency --> EUR
 [PARAM]processor_response_code --> IGFS_20090
 [PARAM]chargetotal --> 0.01
 [PARAM]terminal_id --> 06840346
 [PARAM]status --> TRANSAZIONE CANCELLATA DALL'UTENTE
 */
