 /**
 每日一次
 1.download stock info json
 2.convert json to sql
 3.import sql
*/ 
def s2 = module.web.Webget.download{
        url "https://isin.twse.com.tw/isin/C_public.jsp?strMode=2"
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

println 'parse 上市 end'

def s4 = module.web.Webget.download{
        url "https://isin.twse.com.tw/isin/C_public.jsp?strMode=4"
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

println 'parse 上櫃 end'

new FileOutputStream('tw_stock.txt').withWriter('UTF-8') { writer ->
    writer << "${s2}"
    writer << "\r\n${s4}"
}
println 'save to file end'

def sqlhead = 'REPLACE INTO `stock_tw`.`stock` (`security_code`, `stock_name`, `isin_code`, `listing_day`, `stock_type`, `stock_category`, `cfi_code`, `product_type`, `update_day`) VALUES '
def updateDay = ''
def productType =''
new File("tw_stock.txt").eachLine { line ->
    if(line.startsWith('最近更新日期:')){
        updateDay = line.replace('最近更新日期:','').replace('/','')
        println updateDay
    }else if(line.startsWith(' ')){
        productType = line.trim()
        println productType
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
new File("stocklist_sql").mkdir()
new FileOutputStream('./stocklist_sql/stock.sql').withWriter('UTF-8') { writer ->
    writer << sqlhead.substring(0, sqlhead.length() - 1)+';\r\n';
}
println 'convert to sql end'

module.db.SqlExecuter.execute{
    dir './stocklist_sql'
}
module.io.FileBetch.execute{
    clean './stocklist_sql'
    delete './stocklist_sql' 'tw_stock.txt'
}
println 'import stocklist end'
