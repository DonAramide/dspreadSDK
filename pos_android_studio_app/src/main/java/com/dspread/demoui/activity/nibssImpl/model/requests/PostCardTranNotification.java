package com.dspread.demoui.activity.nibssImpl.model.requests;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "rm_PostCardTran_Notification",indices = {@Index(value = {"transactionReference"},
        unique = true)})

public class PostCardTranNotification {

    @PrimaryKey(autoGenerate = true)
    private long id;


    @ColumnInfo(name = "tranCode")
    private String tranCode;

    @ColumnInfo(name = "currency")
    private String currency;

    @ColumnInfo(name = "amount")
    public Double amount;


    public Double fee;

    private String deviceId;

    @ColumnInfo(name = "providerReference")
    private String providerReference;

    @ColumnInfo(name = "merchantId")
    private String merchantId;

    @ColumnInfo(name = "transactionReference")
    private String transactionReference;

    @ColumnInfo(name = "statusCode")
    private String statusCode;

    @ColumnInfo(name = "narration")
    private String narration;

    @ColumnInfo(name = "customerName")
    private String customerName;

    @ColumnInfo(name = "maskedPan")
    private String maskedPan;

    @ColumnInfo(name = "payAuthCode")
    private String payAuthCode;


    @ColumnInfo(name = "authCode")
    private String authCode;

    @ColumnInfo(name = "cardScheme")
    private String cardScheme;

    @ColumnInfo(name = "retrievalNumber")
    private String retrievalNumber;

    @ColumnInfo(name = "nuban")
    private String nuban;

    @ColumnInfo(name = "expiryDate")
    private String expiryDate;

    @ColumnInfo(name = "stan")
    private String stan;

    @ColumnInfo(name = "terminalId")
    private String terminalId;

    @ColumnInfo(name = "beneficiaryAccount")
    private String beneficiaryAccount;

    @ColumnInfo(name = "beneficiaryName")
    private String beneficiaryName;


    @ColumnInfo(name = "beneficiaryBankCode")
    private String beneficiaryBankCode;


    @ColumnInfo(name = "dateTime")
    private String dateTime;


    @ColumnInfo(name = "walletAccount")
    private String walletAccount;


    @ColumnInfo(name = "aid")
    private String aid;


    private String baseAppVersion;
    private String pos_event_notification_staging;



    //  private UdfDataList udfDataList;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "appLabel")
    private String appLabel;


    @ColumnInfo(name = "phoneNo")
    private String phoneNo;

    @ColumnInfo(name = "geolocation")
    private String geolocation;


    private List<UdfData> udfDataList;

    public PostCardTranNotification() {
    }


    @Override
    public String toString() {
        return "PostCardTranNotification{" +
                "id=" + id +
                ", tranCode='" + tranCode + '\'' +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                ", providerReference='" + providerReference + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", transactionReference='" + transactionReference + '\'' +
                ", statusCode='" + statusCode + '\'' +
                ", narration='" + narration + '\'' +
                ", customerName='" + customerName + '\'' +
                ", maskedPan='" + maskedPan + '\'' +
                ", payAuthCode='" + payAuthCode + '\'' +
                ", authCode='" + authCode + '\'' +
                ", cardScheme='" + cardScheme + '\'' +
                ", retrievalNumber='" + retrievalNumber + '\'' +
                ", nuban='" + nuban + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", stan='" + stan + '\'' +
                ", terminalId='" + terminalId + '\'' +
                ", beneficiaryAccount='" + beneficiaryAccount + '\'' +
                ", beneficiaryName='" + beneficiaryName + '\'' +
                ", beneficiaryBankCode='" + beneficiaryBankCode + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", walletAccount='" + walletAccount + '\'' +
                ", fee='" + fee + '\'' +
                ", aid='" + aid + '\'' +
                ", message='" + message + '\'' +
                ", appLabel='" + appLabel + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTranCode() {
        return tranCode;
    }

    public void setTranCode(String tranCode) {
        this.tranCode = tranCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public void setProviderReference(String providerReference) {
        this.providerReference = providerReference;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMaskedPan() {
        return maskedPan;
    }

    public void setMaskedPan(String maskedPan) {
        this.maskedPan = maskedPan;
    }

    public String getPayAuthCode() {
        return payAuthCode;
    }

    public void setPayAuthCode(String payAuthCode) {
        this.payAuthCode = payAuthCode;
    }

    public String getCardScheme() {
        return cardScheme;
    }

    public void setCardScheme(String cardScheme) {
        this.cardScheme = cardScheme;
    }

    public String getRetrievalNumber() {
        return retrievalNumber;
    }

    public String getRrn() {
        return retrievalNumber;
    }

    public void setRetrievalNumber(String retrievalNumber) {
        this.retrievalNumber = retrievalNumber;
    }

    public String getNuban() {
        return nuban;
    }

    public void setNuban(String nuban) {
        this.nuban = nuban;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getBeneficiaryAccount() {
        return beneficiaryAccount;
    }

    public void setBeneficiaryAccount(String beneficiaryAccount) {
        this.beneficiaryAccount = beneficiaryAccount;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBeneficiaryBankCode() {
        return beneficiaryBankCode;
    }

    public void setBeneficiaryBankCode(String beneficiaryBankCode) {
        this.beneficiaryBankCode = beneficiaryBankCode;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getWalletAccount() {
        return walletAccount;
    }

    public void setWalletAccount(String walletAccount) {
        this.walletAccount = walletAccount;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

//    public UdfDataList getUdfDataList() {
//        return udfDataList;
//    }
//
//    public void setUdfDataList(UdfDataList udfDataList) {
//        this.udfDataList = udfDataList;
//    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }


    public String getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(String geolocation) {
        this.geolocation = geolocation;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<UdfData> getUdfDataList() {
        return udfDataList;
    }

    public void setUdfDataList(List<UdfData> udfDataList) {
        this.udfDataList = udfDataList;
    }

    public String getBaseAppVersion() {
        return baseAppVersion;
    }

    public void setBaseAppVersion(String baseAppVersion) {
        this.baseAppVersion = baseAppVersion;
    }

    public String getPos_event_notification_staging() {
        return pos_event_notification_staging;
    }

    public void setPos_event_notification_staging(String pos_event_notification_staging) {
        this.pos_event_notification_staging = pos_event_notification_staging;
    }
}
