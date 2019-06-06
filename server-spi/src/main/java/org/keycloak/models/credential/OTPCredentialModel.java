package org.keycloak.models.credential;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.common.util.Time;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.credential.dto.OTPCredentialData;
import org.keycloak.models.credential.dto.OTPSecretData;
import org.keycloak.models.OTPPolicy;
import org.keycloak.models.RealmModel;

import java.io.IOException;

public class OTPCredentialModel extends CredentialModel {

    public final static String TYPE = "otp";

    public final static String TOTP = "totp";
    public final static String HOTP = "hotp";

    private final OTPCredentialData credentialData;
    private final OTPSecretData secretData;

    private OTPCredentialModel(String secretValue, String subType, int digits, int counter, int period, String algorithm) {
        credentialData = new OTPCredentialData(subType, digits, counter, period, algorithm);
        secretData = new OTPSecretData(secretValue);
    }

    private OTPCredentialModel(OTPCredentialData credentialData, OTPSecretData secretData) {
        this.credentialData = credentialData;
        this.secretData = secretData;
    }

    public static OTPCredentialModel createTOTP(String secretValue, int digits, int period, String algorithm){
        OTPCredentialModel credentialModel = new OTPCredentialModel(secretValue, TOTP, digits, 0, period, algorithm);
        credentialModel.fillCredentialModelFields();
        return credentialModel;
    }

    public static OTPCredentialModel createHOTP(String secretValue, int digits, int counter, String algorithm) {
        OTPCredentialModel credentialModel = new OTPCredentialModel(secretValue, HOTP, digits, counter, 0, algorithm);
        credentialModel.fillCredentialModelFields();
        return credentialModel;
    }

    public static OTPCredentialModel createFromPolicy(RealmModel realm, String secretValue) {
        return createFromPolicy(realm, secretValue, "");
    }

    public static OTPCredentialModel createFromPolicy(RealmModel realm, String secretValue, String userLabel) {
        OTPPolicy policy = realm.getOTPPolicy();

        OTPCredentialModel credentialModel = new OTPCredentialModel(secretValue, policy.getType(), policy.getDigits(),
                policy.getInitialCounter(), policy.getPeriod(), policy.getAlgorithm());
        credentialModel.fillCredentialModelFields();
        credentialModel.setUserLabel(userLabel);
        return credentialModel;
    }

    public static OTPCredentialModel createFromCredentialModel(CredentialModel credentialModel) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            OTPCredentialData credentialData = objectMapper.readValue(credentialModel.getCredentialData(), OTPCredentialData.class);
            OTPSecretData secretData = objectMapper.readValue(credentialModel.getSecretData(), OTPSecretData.class);

            OTPCredentialModel otpCredentialModel = new OTPCredentialModel(credentialData, secretData);
            otpCredentialModel.setUserLabel(credentialModel.getUserLabel());
            otpCredentialModel.setCreatedDate(credentialModel.getCreatedDate());
            otpCredentialModel.setType(TYPE);
            otpCredentialModel.setId(credentialModel.getId());
            otpCredentialModel.setSecretData(credentialModel.getSecretData());
            otpCredentialModel.setCredentialData(credentialModel.getCredentialData());
            return otpCredentialModel;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void updateCounter(int counter) {
        ObjectMapper objectMapper = new ObjectMapper();
        credentialData.setCounter(counter);
        try {
            setCredentialData(objectMapper.writeValueAsString(credentialData));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public OTPCredentialData getOTPCredentialData() {
        return credentialData;
    }

    public OTPSecretData getOTPSecretData() {
        return secretData;
    }

    private void fillCredentialModelFields(){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            setCredentialData(objectMapper.writeValueAsString(credentialData));
            setSecretData(objectMapper.writeValueAsString(secretData));
            setType(TYPE);
            setCreatedDate(Time.currentTimeMillis());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
