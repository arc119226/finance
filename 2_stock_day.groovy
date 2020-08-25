/**
每日一次
*/
@Grab('mysql:mysql-connector-java:5.1.39')
@GrabConfig(systemClassLoader=true)
import groovy.sql.Sql

def sql = Sql.newInstance('jdbc:mysql://127.0.0.1:3306/stock_tw?useUnicode=yes&characterEncoding=UTF-8&character_set_server=utf8mb4',
						  'root',
						  'Esorn@ldorn110','com.mysql.jdbc.Driver')

def stockCodes = sql.rows("select stock.security_code,stock.listing_day from stock where stock.stock_type='上市' order by stock.listing_day")

stockCodes.each{it->
	def security_code = it.security_code
	def listing_day = it.listing_day
	def resultSql =''
	if(!new File('./stock_day/'+security_code+'.sql').exists()){
		module.processor.ProcessorRunner.runMonthByMonth{
			startYear 2020//Integer.valueOf(listing_day.toString()[0..3])
			startMonth 8//Integer.valueOf(listing_day.toString()[4..5])
			startday 1//Integer.valueOf(listing_day.toString()[6..7])
			endYear 2020
			endMonth 8
			endDay 1
			process{yyyyMmDd->
				println yyyyMmDd
			    def returnJson = module.web.Webget.download{
			         url "https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&lang=en&date=${yyyyMmDd}&stockNo=${security_code}"
			         decode 'utf-8'
			    }
			    //print ' fetch api done'

				module.parser.JsonConvert.convert{
		        	input returnJson
		        	parseRule {json->
			            if(json.stat != 'OK'){
			               return null
			            }
			            def fields = json.fields.collect(fieldNormalize)
			            if(resultSql==''){
			            	resultSql = "REPLACE INTO `stock_tw`.`stock_day` (`${fields.join('`,`')}`,`traded_day`,`security_code`) VALUES "
			            }
			            for(int i=0;i<json.data.size;i++){
			            	def dt = json.data[i]
			            	def dayArr = dt[0].split('/')
							def year = Integer.parseInt(dayArr[0].trim())
							def month = String.format("%02d", Integer.parseInt(dayArr[1].trim()))
							def day = String.format("%02d", Integer.parseInt(dayArr[2].trim()))
							def price_day = "${year}${month}${day}" 
			                def _data = json.data[i].collect(valueNormalise).join("','");
			                if(resultSql.endsWith('VALUES ')){
			                  resultSql+="\r\n('${_data}','${price_day}','${security_code}')"
			               }else{
			                   resultSql+="\r\n,('${_data}','${price_day}','${security_code}')"
			               }
			            }
		        	}
		    	}//convert
			}//process
		}//run
		if(resultSql && !resultSql.endsWith('VALUES ')){
	    	new File('./stock_day/').mkdir()
	 		new FileOutputStream('./stock_day/'+security_code+'.tmp').withWriter('UTF-8') { writer ->
	    		writer << resultSql+';'
	 		}
	 		new File('./stock_day/'+security_code+'.tmp').renameTo('./stock_day/'+security_code+'.sql')
	 		//print ', save sql done'
		}
		println security_code
	}else{
		println "${security_code} already done."
	}
}//each stock
module.db.SqlExecuter.execute{
    dir './stock_day'
}
module.io.FileBetch.execute{
	clean './stock_day'
}
println 'import stock_day done'