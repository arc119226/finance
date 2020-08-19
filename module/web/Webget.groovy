package module.web
@Grab(group='commons-io', module='commons-io', version='2.5')

import java.net.URL
import java.net.URLConnection
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate

class Webget{
    static{
        TrustManager[] trustAllCerts = [ 
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                
                }
            }
        ];
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }//end static
    String url=null,errorLog='error.txt',decode=null
    int retry=3,sleeptime=100

    def url(String url){
        this.url=url
    }
    def errorLog(String errorLog){
        this.errorLog=errorLog
    }
    def retry(int retry){
        this.retry=retry
    }
    def decode(String decode){
        this.decode=decode
    }
    def sleeptime(int sleeptime){
        this.sleeptime=sleeptime
    }

    def openConnection(){
        for(int i=0; i<=retry;i++){
            def get = new URL(url).openConnection()
            if(get.getResponseCode().equals(200)) {
                InputStream is = get.getInputStream();
                if(decode == null){
                    return org.apache.commons.io.IOUtils.toString(is);
                }else{
                    return org.apache.commons.io.IOUtils.toString(is, decode);
                }
            }//if
            if(i==retry){
                File error = new File(errorLog)
                error.append('\n'+get.getResponseCode()+' '+url+'')
                return ''
            }//if
            sleep(sleeptime)
        }//for
    }//download
    def static download(@DelegatesTo(Webget) Closure block){
        Webget m = new Webget()
        block.delegate = m
        block()
        return m.openConnection()
    }
}

