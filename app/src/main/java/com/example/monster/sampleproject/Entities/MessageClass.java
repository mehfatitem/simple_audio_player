package com.example.monster.sampleproject.Entities;

public class MessageClass {

    private String warning;
    private String internetConnectionStatus;
    private String allRightReserved;
    private String username;
    private String selectedRadio;
    private String stopped;
    private String radioInfoServiceUrl;
    private String loading;

    public MessageClass () {
        setAllProperties();
    }

    private void setAllProperties() {
        
        setWarning("Uyarı");
        setInternetConnectionStatus("İnternet bağlantısını aktif hale getiriniz!");
        setAllRightReserved("Tüm Hakları Saklıdır © ");
        setUsername("mehfatitem");
        setSelectedRadio("Radyo Yayını Seçiniz...");
        setStopped("Durduruldu ...");
        setRadioInfoServiceUrl("https://mehfatitem54.000webhostapp.com/internet_radio_web_service/webservice/server.php?operation=getRadioInfo");
        setLoading("Yükleniyor...");
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public String getInternetConnectionStatus() {
        return internetConnectionStatus;
    }

    public void setInternetConnectionStatus(String internetConnectionStatus) {
        this.internetConnectionStatus = internetConnectionStatus;
    }

    public String getAllRightReserved() {
        return allRightReserved;
    }

    public void setAllRightReserved(String allRightReserved) {
        this.allRightReserved = allRightReserved;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSelectedRadio() {
        return selectedRadio;
    }

    public void setSelectedRadio(String selectedRadio) {
        this.selectedRadio = selectedRadio;
    }

    public String getStopped() {
        return stopped;
    }

    public void setStopped(String stopped) {
        this.stopped = stopped;
    }

    public String getRadioInfoServiceUrl() {
        return radioInfoServiceUrl;
    }

    public void setRadioInfoServiceUrl(String radioInfoServiceUrl) {
        this.radioInfoServiceUrl = radioInfoServiceUrl;
    }

    public String getLoading() {
        return loading;
    }

    public void setLoading(String loading) {
        this.loading = loading;
    }
}
