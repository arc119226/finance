 /**
 每日一次
 1.download stock info json
 2.convert json to sql
 3.import sql
*/ 
class StockList{
    def url1='https://isin.twse.com.tw/isin/C_public.jsp?strMode=2'
    def url2='https://isin.twse.com.tw/isin/C_public.jsp?strMode=4'
    def listFileName='tw_stock.txt'
    def sqlDirName='stocklist_sql'

    def run(){
        try { 
           def s2 = module.web.Webget.download{
                url url1
            }
            s2 = s2.trim().replace('<link rel="stylesheet" href="http://isin.twse.com.tw/isin/style1.css" type="text/css">','')
            .replace("<body><table  align=center><h2><strong><font class='h1'>本國上市證券國際證券辨識號碼一覽表</font></strong></h2><h2><strong><font class='h1'>",'')
            .replace("</font></strong></h2><h2><font color='red'><center>掛牌日以正式公告為準</center></font></h2></table><TABLE class='h4' align=center cellSpacing=3 cellPadding=2 width=750 border=0><tr align=center><td bgcolor=#D5FFD5>有價證券代號及名稱 </td>",'')
            .replace("<td bgcolor=#D5FFD5>國際證券辨識號碼(ISIN Code)</td><td bgcolor=#D5FFD5>上市日</td><td bgcolor=#D5FFD5>市場別</td><td bgcolor=#D5FFD5>產業別</td><td bgcolor=#D5FFD5>CFICode</td><td bgcolor=#D5FFD5>備註</td></tr>",'')
            .replace("<center>",'')
            .replace("</center>",'\r\n')
            .replace("</tr></table><font color='red'>掛牌日以正式公告為準</center></font>",'')
            .replaceAll("<tr><td bgcolor=#FAFAD2 colspan=7 ><B>",'')
            .replaceAll("<B>",'')
            .replaceAll("</td></tr>","\r\n")
            .replaceAll("<tr><td bgcolor=#FAFAD2>",'')
            .replace("</table><font color='red'>掛牌日以正式公告為準",'')
            .replaceAll("</td><td bgcolor=#FAFAD2>",',')
            .replaceAll("</font>",'')
            .replaceAll('　',',')
            .replaceAll(',,',',')
            .replace("<body><table  align=center><h2><strong><font class='h1'>本國上櫃證券國際證券辨識號碼一覽表</strong></h2><h2><strong><font class='h1'>",'')
            .replaceAll('\\r\\n\\r\\n','')
            .replaceAll('\\r\\n\\r\\n','')
            .replaceAll('  ',' ')
            .replace('/','')
            .replace('\n','').trim()

            module.io.FileBetch.execute{
                log '\nparse 上市 end'
            }

            def s4 = module.web.Webget.download{
                url url2
            }
            s4 = s4.trim().replace('<link rel="stylesheet" href="http://isin.twse.com.tw/isin/style1.css" type="text/css">','')
                        .replace("<body><table  align=center><h2><strong><font class='h1'>本國上市證券國際證券辨識號碼一覽表</font></strong></h2><h2><strong><font class='h1'>",'')
                        .replace("</font></strong></h2><h2><font color='red'><center>掛牌日以正式公告為準</center></font></h2></table><TABLE class='h4' align=center cellSpacing=3 cellPadding=2 width=750 border=0><tr align=center><td bgcolor=#D5FFD5>有價證券代號及名稱 </td>",'')
                        .replace("<td bgcolor=#D5FFD5>國際證券辨識號碼(ISIN Code)</td><td bgcolor=#D5FFD5>上市日</td><td bgcolor=#D5FFD5>市場別</td><td bgcolor=#D5FFD5>產業別</td><td bgcolor=#D5FFD5>CFICode</td><td bgcolor=#D5FFD5>備註</td></tr>",'')
                        .replace("<center>",'')
                        .replace("</center>",'\r\n')
                        .replace("</tr></table><font color='red'>掛牌日以正式公告為準</center></font>",'')
                        .replaceAll("<tr><td bgcolor=#FAFAD2 colspan=7 ><B>",'')
                        .replaceAll("<B>",'')
                        .replaceAll("</td></tr>","\r\n")
                        .replaceAll("<tr><td bgcolor=#FAFAD2>",'')
                        .replace("</table><font color='red'>掛牌日以正式公告為準",'')
                        .replaceAll("</td><td bgcolor=#FAFAD2>",',')
                        .replaceAll("</font>",'')
                        .replaceAll('　',',')
                        .replaceAll(',,',',')
                        .replace("<body><table  align=center><h2><strong><font class='h1'>本國上櫃證券國際證券辨識號碼一覽表</strong></h2><h2><strong><font class='h1'>",'')
                        .replaceAll('\\r\\n\\r\\n','')
                        .replaceAll('\\r\\n\\r\\n','')
                        .replaceAll('  ',' ')
                        .replace('/','')
                        .replace('\n','').trim()

            module.io.FileBetch.execute{
                log '\nparse 上櫃 end'
            }
            new FileOutputStream(listFileName).withWriter('UTF-8') { writer ->
                writer << "${s4}"
                writer << "\r\n${s2}"
            }
            module.io.FileBetch.execute{
                log '\nsave to file end'
            }

            def sqlhead = 'REPLACE INTO `stock_tw`.`stock` (`security_code`, `stock_name`, `isin_code`, `listing_day`, `stock_type`, `stock_category`, `cfi_code`, `product_type`, `update_day`) VALUES '
            def updateDay = ''
            def productType =''
            new File(listFileName).eachLine { line ->
                if(line.startsWith('最近更新日期:')){
                    updateDay = line.replace('最近更新日期:','').replace('/','')
                    module.io.FileBetch.execute{
                        log "\n${updateDay}"
                    }
                }else if(line.startsWith(' ')){
                    productType = line.trim()
                    module.io.FileBetch.execute{
                        log "\n${productType}"
                    }
                }else{
                    def dataArr = line.split(',');
                    if(productType=='股票'){
                        def subCommand = "\r\n('${dataArr[0].trim()}', '${dataArr[1].trim()}', '${dataArr[2].trim()}', '${dataArr[3].trim()}', '${dataArr[4].trim()}', '${dataArr[5].trim()}', '${dataArr[6].trim()}', '${productType}', '${updateDay}'),"
                        sqlhead = sqlhead + subCommand
                    }else{
                        def subCommand = "\r\n('${dataArr[0].trim()}', '${dataArr[1].trim()}', '${dataArr[2].trim()}', '${dataArr[3].trim()}', '${dataArr[4].trim()}', '', '${dataArr[5].trim()}', '${productType}', '${updateDay}'),"
                        sqlhead = sqlhead + subCommand
                    }
                }
            }
            new File(sqlDirName).mkdir()
            new FileOutputStream("./${sqlDirName}/stock.sql").withWriter('UTF-8') { writer ->
                writer << sqlhead.substring(0, sqlhead.length() - 1)+';\r\n';
            }
            module.io.FileBetch.execute{
                log '\nconvert to sql end'
            }
            module.db.SqlExecuter.execute{
                dir './'+sqlDirName
            }
            module.io.FileBetch.execute{
                clean './'+sqlDirName
                delete './'+sqlDirName
            }
            module.io.FileBetch.execute{
                delete listFileName
                log '\nimport stocklist end'
            }

        } catch(Exception e) {
            e.printStackTrace()
            module.io.FileBetch.execute{
                errorLog "${e.message}"
            }
        }
    }
}
static main(args) {
    new StockList().run()
}






