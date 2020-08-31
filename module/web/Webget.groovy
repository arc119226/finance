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
        System.setProperty("http.keepAlive", "false");
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
    int retry=100,sleeptime=25
    boolean validate=false
    boolean validatePb=false
    boolean validateInv=false
    boolean validateShortSell=false
    boolean validateHighLight=false
    boolean validateForeign=false
    boolean validateIndustrial=false
    boolean validateMarginTransactionl=false
    boolean validateTaiex=false
    boolean validateStockDay=false

    boolean parsePage=false

    def parsePage(boolean parsePage){
        this.parsePage=parsePage
    }

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
    def validate(boolean validate){
        this.validate=validate
    }
    def validatePb(boolean validatePb){
        this.validatePb=validatePb
    }
    def validateInv(boolean validateInv){
        this.validateInv=validateInv
    }
    def validateShortSell(boolean validateShortSell){
        this.validateShortSell=validateShortSell
    }
    def validateHighLight(boolean validateHighLight){
        this.validateHighLight=validateHighLight
    }
    def validateForeign(boolean validateForeign){
        this.validateForeign=validateForeign
    }
    def validateIndustrial(boolean validateIndustrial){
        this.validateIndustrial=validateIndustrial
    }
    def validateMarginTransactionl(boolean validateMarginTransactionl){
        this.validateMarginTransactionl=validateMarginTransactionl
    }
    def validateTaiex(boolean validateTaiex){
        this.validateTaiex=validateTaiex
    }
    def validateStockDay(boolean validateStockDay){
        this.validateStockDay=validateStockDay
    }
    def openConnection(){
        int failCount = 0
        for(int i=0; i<=retry;i++){
            def get = new URL(url).openConnection()
            get.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"); 
            if(get.getResponseCode().equals(200)) {
                InputStream is = get.getInputStream();
                if(decode == null){
                    def r=org.apache.commons.io.IOUtils.toString(is);
                    get.disconnect()
                    if(r){
                        return r
                    }
                }else if(decode && parsePage){
                    def r=org.apache.commons.io.IOUtils.toString(is,decode);
                    get.disconnect()
                    if(r){
                        return r
                    }
                }else{
                    def r=org.apache.commons.io.IOUtils.toString(is, decode);
                    get.disconnect()
                    if(r!=null && r.contains('OK')){
                        if(validate){
                            //need strict validate
                            def pattern = ~/(\d+\/\d+\/\d+)/
                            def macher = r =~ pattern
                            macher.find()
                            macher.size()
                            macher.each{
                                String strValue = it[0].replaceAll(/^(\d+)(\/)(\d+)(\/)(\d+)$/,'$1$3$5')
                                if(strValue.length()==8){
                                    def value = Integer.valueOf(strValue);
                                    if(value < Integer.valueOf(url[-8..-1]) || value > Integer.valueOf(url[-8..-3]+'31')){
                                        failCount++
                                    }
                                }else{
                                    failCount++
                                }
                            }
                            if(failCount == 0 && !r.contains('null')){
                                return r
                            }
                        }else if(validatePb){
                            def yyyyMmDd = url[-8..-1];
                            if(r.contains('"date":"'+yyyyMmDd+'"')){
                                return r
                            }else{
                                failCount++
                            }
                        }else if(validateInv){
                            def yyyyMmDd = url[-8..-1];
                            if(r.contains('"date":"'+yyyyMmDd+'"')){
                                return r
                            }else{
                                failCount++
                            }
                        }else if(validateShortSell){
                            def yyyyMmDd = url[-8..-1];
                            if(r.contains('"date":"'+yyyyMmDd+'"')){
                                return r
                            }else{
                                failCount++
                            }
                        }else if(validateHighLight){
                            def yyyyMmDd = url[-8..-1];
                            if(r.contains('"date":"'+yyyyMmDd+'"')){
                                return r
                            }else{
                                failCount++
                            }
                        }else if(validateForeign){
                            def yyyyMmDd = url[-8..-1];
                            if(r.contains('"date":"'+yyyyMmDd+'"')){
                                return r
                            }else{
                                failCount++
                            }
                        }else if(validateIndustrial){
                            def yyyyMmDd = url[-8..-1];
                            println yyyyMmDd
                            if(r.contains('"date":"'+yyyyMmDd+'"')){
                                return r
                            }else{
                                failCount++
                            }
                        }else if(validateMarginTransactionl){
                            def yyyyMmDd = url[-8..-1];
                            if(r.contains('"date":"'+yyyyMmDd+'"')){
                                return r
                            }else{
                                failCount++
                            }
                        }else if(validateTaiex){
                            def yyyyMmDd = url[-8..-1];
                            if(r.contains('"date":"'+yyyyMmDd+'"')){
                                return r
                            }else{
                                failCount++
                            }
                        }else if(validateStockDay){
                            def yyyyMmDd = url[-8..-1];
                            if(r.contains('"date":"'+yyyyMmDd+'"')){
                                return r
                            }else{
                                failCount++
                            }
                        }else{
                            return r
                        }
                    }else if(r.contains('Data Not Found!')||
                                    r.contains('please retry!')||
                                    r.contains('No Data!')||
                                    r.contains('No data')||
                                    r.contains('No data found.')||
                                    r.contains('Sorry')){
                            return r
                    }
                }//end else
            }//if
            if(i==retry){
                File error = new File(errorLog)
                error.append('\n'+get.getResponseCode()+' '+url+'')
                System.exit(1)
                return null
            }//if
            print '.'+"${i} "
            
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

