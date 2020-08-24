@Grab('mysql:mysql-connector-java:5.1.39')
@GrabConfig(systemClassLoader=true)
import groovy.sql.Sql
def sql = Sql.newInstance('jdbc:mysql://127.0.0.1:3306/stock_tw?useUnicode=yes&characterEncoding=UTF-8&character_set_server=utf8mb4',
						  'root',
						  'Esorn@ldorn110','com.mysql.jdbc.Driver')

def stockCodes = sql.rows("select stock.security_code,stock.listing_day from stock where stock.stock_type='上市' order by stock.listing_day")

stockCodes.each{
	def security_code=it.security_code
	def resultSql = ''
	if(!new File("./yearly_trading_summary/${security_code}.sql").exists()){
			def returnJson = module.web.Webget.download{
					     url "https://www.twse.com.tw/exchangeReport/FMNPTK?response=json&lang=en&stockNo=${security_code}"
					     decode 'utf-8'
					     retry 100
					     sleeptime 50
					}
		module.parser.JsonConvert.convert{
						    input returnJson
							parseRule {json->
							    if(json.stat != 'OK'){
							    	//print '.'
							    	return ''
						        }
						        resultSql = "REPLACE INTO `stock_tw`.`yearly_trading_summary` (`year`, `trade_volume`, `trade_value`, `transaction`, `highest_price`, `date_highest_price`, `lowest_price`, `date_lowest_price`, `average_closing_price`, `security_code`) VALUES "
						    	for(int i=0;i<json.data.size;i++){
						    		def _data = json.data[i]
						    		def year = _data[0]
						    		def trade_volume = _data[1].replaceAll(',','')
						    		def trade_value = _data[2].replaceAll(',','')
						    		def transaction = _data[3].replaceAll(',','')
						    		def highest_price = _data[4].replaceAll(',','')
						    		def date_highest_price =null
						    		if(_data[5]){
						    				def dtH = _data[5].split('/')
						    				date_highest_price = String.format('%04d%02d%02d',year,Integer.valueOf(dtH[0]),Integer.valueOf(dtH[1]))
						    		}
						    		def date_lowest_price =null
						    		def lowest_price = _data[6]
						    		if(_data[7]){
						    			def dtL = _data[7].split("/")
						    			date_lowest_price = String.format('%04d%02d%02d',year,Integer.valueOf(dtL[0]),Integer.valueOf(dtL[1]))
						    		}
						    		def average_closing_price = _data[8]
						    		 if(resultSql.endsWith('VALUES ')){
						    		 	if(date_lowest_price && date_lowest_price){
						    				resultSql+= "\r\n('${year}', '${trade_volume}', '${trade_value}', '${transaction}', '${highest_price}', ${date_highest_price}, '${lowest_price}', ${date_lowest_price}, '${average_closing_price}', '${security_code}')"
						    		 	}
						    		 }else{
						    		 	if(date_lowest_price && date_lowest_price){
						    		 		resultSql+= "\r\n,('${year}', '${trade_volume}', '${trade_value}', '${transaction}', '${highest_price}', ${date_highest_price}, '${lowest_price}', ${date_lowest_price}, '${average_closing_price}', '${security_code}')"	
						    		 	}
						    		 }
						    	}

						    }
						}
		if(resultSql && !resultSql.endsWith('VALUES ')){
			new File('./yearly_trading_summary/').mkdir()
			new FileOutputStream("./yearly_trading_summary/${security_code}.tmp").withWriter('UTF-8') { writer ->
				writer << resultSql+';'
			}
			new File("./yearly_trading_summary/${security_code}.tmp").renameTo("./yearly_trading_summary/${security_code}.sql")
			println "${security_code}"
		}
	}else{
			println "${security_code} already done."
	}
}
module.db.SqlExecuter.execute{
    dir './yearly_trading_summary'
}
module.io.FileBetch.execute{
	clean './yearly_trading_summary'
}
println 'import yearly_trading_summary done'
